package ru.stereohorse.mesos.framework


class Framework {

    static void main(String... args) {

        def mesos = new Mesos(apiUrl: 'http://localhost:5050/api/v1/scheduler')
        def scheduler = new Scheduler(name: 'bla-bla-framework', mesos: mesos)

        def api = new Api(scheduler: scheduler)

        api.start()
        scheduler.start()
    }
}
