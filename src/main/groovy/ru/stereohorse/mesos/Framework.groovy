package ru.stereohorse.mesos

class Framework {

    static void main(String... args) {

        def zk = new Zk(address: 'localhost:2181')

        def scheduler = new Scheduler(
                name: 'bla-bla-framework',
                mesos: new Mesos(
                        apiUrl: 'http://localhost:5050/api/v1/scheduler'
                ),
                zk: zk
        )

        def api = new Api(scheduler: scheduler)

        zk.start()
        api.start()
        scheduler.start()
    }
}
