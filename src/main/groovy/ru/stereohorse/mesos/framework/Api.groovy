package ru.stereohorse.mesos.framework

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import static spark.Spark.get
import static spark.Spark.post

@Slf4j
class Api {

    Scheduler scheduler


    void start() {

        get '/tasks', { req, res ->
            scheduler.tasks
        }

        post '/tasks', { req, res ->
            def json = new JsonSlurper().parseText(req.body()) as Map
            scheduler.launch new Task(json)
            return "OK"
        }
    }
}
