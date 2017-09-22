package ru.stereohorse.mesos.framework

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import static spark.Spark.get
import static spark.Spark.post
import static spark.Spark.delete

@Slf4j
class Api {

    Scheduler scheduler


    void start() {

        get '/tasks', { req, res ->
            JsonOutput.toJson scheduler.tasks
        }

        post '/tasks', { req, res ->
            def json = new JsonSlurper().parseText(req.body()) as Map
            def task = new Task(json)

            scheduler.launch task
            return JsonOutput.toJson(task)
        }

        delete '/tasks/:taskId', { req, res ->
            scheduler.kill req.params(':taskId')
            return ''
        }
    }
}
