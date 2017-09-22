package ru.stereohorse.mesos.framework

import org.apache.zookeeper.ZooKeeper

import static org.apache.zookeeper.CreateMode.PERSISTENT
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE

class Zk {

    ZooKeeper zk
    String address


    void start() {
        zk = new ZooKeeper(address, 2000, null)
    }

    void ensurePathExists(String path) {
        if (!zk.exists(path, false)) {
            zk.create(path, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT)
        }
    }

    void write(String path, byte[] data) {
        zk.setData path, data, -1
    }

    byte[] read(String path) {
        zk.getData(path, false, null)
    }
}
