package ru.stereohorse.mesos

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

class Scheduler {

    String frameworkId
    String name
    Map lastOffer

    Map<String, Task> tasks = [:]

    Mesos mesos
    Zk zk


    void handle(Map event) {

        if (event.type == 'OFFERS') {
            lastOffer = event
        } else if (event.type == 'SUBSCRIBED') {
            frameworkId = event.subscribed.framework_id.value
        } else if (event.type == 'UPDATE') {
            String taskId = event.update.status.task_id.value

            Task task = tasks[taskId]
            task.state = event.update.status.state
            task.lastAckUuid = event.update.status.uuid
            task.agentId = event.update.status.agent_id.value

            mesos.acknowledge this, task
        }
    }

    void start() {
        zk.ensurePathExists this.zkDataPath

        byte[] rawTasks = zk.read(this.zkDataPath)

        if (rawTasks.length != 0) {
            tasks = new ObjectMapper().readValue(
                    zk.read(this.zkDataPath),
                    new TypeReference<Map<String, Task>>() {})
        }

        mesos.subscribe this
    }

    String getZkDataPath() {
        return "/$name"
    }

    void kill(String taskId) {
        mesos.kill this, new Task(id: taskId)
        tasks.remove taskId
        persistTasks()
    }

    void launch(Task task) {
        mesos.launch this, task
        tasks.put task.id, task
        persistTasks()
    }

    void persistTasks() {
        byte[] rawTasks = new ObjectMapper().writeValueAsBytes(tasks)
        zk.write this.zkDataPath, rawTasks
    }

    String getLastOfferId() {
        return lastOffer.offers.offers[0].id.value
    }

    String getLastOfferAgentId() {
        return lastOffer.offers.offers[0].agent_id.value
    }
}
