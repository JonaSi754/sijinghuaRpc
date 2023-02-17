package org.sijinghua.rpc.test.registry;

import org.junit.Before;
import org.junit.Test;
import org.sijinghua.rpc.protocol.meta.ServiceMeta;
import org.sijinghua.rpc.registry.api.RegistryService;
import org.sijinghua.rpc.registry.api.config.RegistryConfig;
import org.sijinghua.rpc.registry.zookeeper.ZookeeperRegistryService;

import java.io.IOException;

public class ZookeeperRegistryTest {
    private RegistryService registryService;
    private ServiceMeta serviceMeta;

    @Before
    public void init() throws Exception {
        RegistryConfig registryConfig = new RegistryConfig("127.0.0.1:2181", "zookeeper");
        this.registryService = new ZookeeperRegistryService();
        registryService.init(registryConfig);
        this.serviceMeta = new ServiceMeta(ZookeeperRegistryTest.class.getName(), "1.0.0",
                "sijinghua", "127.0.0.1", 8080);
    }

    @Test
    public void testRegister() throws Exception {
        this.registryService.register(serviceMeta);
    }

    @Test
    public void testUnregister() throws Exception {
        this.registryService.unRegister(serviceMeta);
    }

    @Test
    public void testDiscovery() throws Exception {
        this.registryService.discovery(RegistryService.class.getName(), "sijinghua".hashCode());
    }

    @Test
    public void testDestroy() throws IOException {
        this.registryService.destroy();
    }
}
