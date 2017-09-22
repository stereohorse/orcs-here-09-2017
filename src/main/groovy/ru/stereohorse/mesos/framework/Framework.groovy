package ru.stereohorse.mesos.framework

class Framework {

    static void main(String... args) {

        def mesos = new Mesos(apiUrl: 'http://localhost:5050/api/v1/scheduler')
        def zk = new Zk(address: 'localhost:2181')
        def scheduler = new Scheduler(name: 'bla-bla-framework', mesos: mesos, zk: zk)

        def api = new Api(scheduler: scheduler)

        zk.start()
        api.start()
        scheduler.start()
    }
}
