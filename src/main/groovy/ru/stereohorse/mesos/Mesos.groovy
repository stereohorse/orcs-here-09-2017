package ru.stereohorse.mesos

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

        RequestBody requestBody = Message.forTaskLaunch(scheduler, task)
        sendToStream(requestBody)
    }

    void kill(Scheduler scheduler, Task task) {

        RequestBody requestBody = Message.forTaskKill(scheduler, task)
        sendToStream(requestBody)
    }

    void acknowledge(Scheduler scheduler, Task task) {

        RequestBody requestBody = Message.forUpdateAck(scheduler, task)
        sendToStream(requestBody)
    }


    private Reader readerFor(Scheduler scheduler) {

        RequestBody requestBody = Message.forSubscription(scheduler)

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

    private void sendToStream(RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(apiUrl)
                .header('Mesos-Stream-Id', streamId)
                .post(requestBody)
                .build()

        client.newCall(request)
                .execute()
    }
}
