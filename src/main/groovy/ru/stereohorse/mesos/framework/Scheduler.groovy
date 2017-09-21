package ru.stereohorse.mesos.framework

class Scheduler {

    String frameworkId

    String name

    Map lastOffer

    List<Task> tasks

    Mesos mesos


    void handle(Map event) {

        if (event.type == 'OFFERS') {
            lastOffer = event
        } else if (event.type == 'SUBSCRIBED') {
            frameworkId = event.subscribed.framework_id.value
        }
    }

    void launch(Task task) {
        mesos.launch this, task
    }

    void start() {
        mesos.subscribe this
    }

    String getLastOfferId() {
        return lastOffer.offers.offers[0].id.value
    }

    String getLastOfferAgentId() {
        return lastOffer.offers.offers[0].agent_id.value
    }
}
