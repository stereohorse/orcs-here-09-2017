package ru.stereohorse.mesos.framework

import com.containersol.minimesos.cluster.MesosCluster
import com.containersol.minimesos.junit.MesosClusterTestRule
import org.junit.ClassRule
import org.junit.Test

class FrameworkTest {

    @ClassRule
    public static final MesosClusterTestRule RULE =
            MesosClusterTestRule.fromFile("src/test/resources/minimesosFile")

    public static final MesosCluster cluster = RULE.getMesosCluster()


    @Test
    void 'should start one agent'() {
        assert cluster.getAgents().size() == 1
    }
}
