package ru.stereohorse.mesos

import static java.util.UUID.randomUUID

class Task {

    String id = randomUUID().toString()

    String name
    String dockerImage

    Integer cpus
    Integer memMb

    String agentId

    String state = 'STAGED'

    String lastAckUuid
}
