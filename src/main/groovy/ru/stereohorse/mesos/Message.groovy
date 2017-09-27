package ru.stereohorse.mesos

import okhttp3.MediaType
import okhttp3.RequestBody

class Message {

    static RequestBody forSubscription(Scheduler scheduler) {
        return RequestBody.create(MediaType.parse('application/json'),
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
    }

    static RequestBody forTaskLaunch(Scheduler scheduler, Task task) {
        RequestBody.create(MediaType.parse('application/json'),
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
    }

    static RequestBody forTaskKill(Scheduler scheduler, Task task) {
        return RequestBody.create(MediaType.parse('application/json'),
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
    }

    static RequestBody forUpdateAck(Scheduler scheduler, Task task) {
        return RequestBody.create(MediaType.parse('application/json'),
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
    }
}
