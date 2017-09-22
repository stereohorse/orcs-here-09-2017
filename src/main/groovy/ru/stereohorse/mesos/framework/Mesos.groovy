package ru.stereohorse.mesos.framework

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import okhttp3.*

import static java.util.concurrent.TimeUnit.MINUTES

@Slf4j
class Mesos {

    String apiUrl

    String streamId

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(1, MINUTES)
            .build()


    void subscribe(Scheduler scheduler) {

        def eventsStream = readerFor scheduler
        def jsonSlurper = new JsonSlurper()

        String line
        while ((line = eventsStream.readLine()) != null) {
            log.info "received $line"

            def body = jsonSlurper.parseText line

            if (body instanceof Map) {
                scheduler.handle body as Map
            }
        }
    }

    void launch(Scheduler scheduler, Task task) {

        RequestBody requestBody = RequestBody.create(MediaType.parse('application/json'),
                """
                {
                  "framework_id": {
                    "value": "${scheduler.frameworkId}"
                  },
                  "type": "ACCEPT",
                  "accept": {
                    "offer_ids": [
                      {
                        "value": "${scheduler.lastOfferId}"
                      }
                    ],
                    "operations": [
                      {
                        "type": "LAUNCH",
                        "launch": {
                          "task_infos": [
                            {
                              "name": "${task.name}",
                              "task_id": {
                                "value": "${task.id}"
                              },
                              "agent_id": {
                                "value": "${scheduler.lastOfferAgentId}"
                              },
                              "command": {
                                "shell": false
                              },
                              "container": {
                                "type": "DOCKER",
                                "docker": {
                                  "image": "${task.dockerImage}"
                                }
                              },
                              "resources": [
                                {
                                  "name": "cpus",
                                  "type": "SCALAR",
                                  "scalar": {
                                    "value": ${task.cpus}
                                  }
                                },
                                {
                                  "name": "mem",
                                  "type": "SCALAR",
                                  "scalar": {
                                    "value": ${task.memMb}
                                  }
                                }
                              ]
                            }
                          ]
                        }
                      }
                    ],
                    "filters": {
                      "refuse_seconds": 5
                    }
                  }
                }
                """.getBytes())

        sendToStream(requestBody)
    }

    void kill(Scheduler scheduler, Task task) {

        RequestBody requestBody = RequestBody.create(MediaType.parse('application/json'),
                """
                {
                  "framework_id": {
                    "value": "${scheduler.frameworkId}"
                  },
                  "type": "KILL",
                  "kill": {
                    "task_id": {
                      "value": "${task.id}"
                    }
                  }
                }
                """.getBytes())

        sendToStream(requestBody)
    }

    void acknowledge(Scheduler scheduler, Task task) {

        RequestBody requestBody = RequestBody.create(MediaType.parse('application/json'),
                """
                {
                  "framework_id": {
                    "value": "${scheduler.frameworkId}"
                  },
                  "type": "ACKNOWLEDGE",
                  "acknowledge": {
                    "agent_id": {
                      "value": "${task.agentId}"
                    },
                    "task_id": {
                      "value": "${task.id}"
                    },
                    "uuid": "${task.lastAckUuid}"
                  }
                }
                """.getBytes())

        sendToStream(requestBody)
    }

    private void sendToStream(RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(apiUrl)
                .header('Mesos-Stream-Id', streamId)
                .post(requestBody)
                .build()

        client.newCall(request)
                .execute()
    }


    private Reader readerFor(Scheduler scheduler) {

        RequestBody requestBody = RequestBody.create(MediaType.parse('application/json'),
                """
                {
                   "type": "SUBSCRIBE",
                   "subscribe": {
                      "framework_info": {
                        "user": "",
                        "name": "${scheduler.name}"
                      }
                   }
                }
                """.getBytes())

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build()

        Response response = client
                .newCall(request)
                .execute()

        streamId = response.header('Mesos-Stream-Id')

        return response.body().charStream()
    }
}
