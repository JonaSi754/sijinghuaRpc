package org.sijinghua.rpc.registry.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.sijinghua.rpc.common.helper.RpcServiceHelper;
import org.sijinghua.rpc.protocol.meta.ServiceMeta;
import org.sijinghua.rpc.registry.api.RegistryService;
import org.sijinghua.rpc.registry.api.config.RegistryConfig;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ZookeeperRegistryService implements RegistryService {
    /**
     * 初始化Curator客户端时，进行连接重试的间隔时间
     */
    public static final int BASE_SLEEP_TIME_MS = 1000;

    /**
     * 初始化Curator客户端时，进行连接重试的最大次数
     */
    public static final int MAX_RETRIES = 3;

    /**
     * 服务注册到Zookeeper的根路径
     */
    public static final String ZK_BASE_PATH = "sijinghua_rpc";

    /**
     * 服务注册与发现的ServiceDiscovery实例
     */
    private ServiceDiscovery<ServiceMeta> serviceDiscovery;

    /**
     * 构建CuratorFramework客户端，并初始化serviceDiscovery
     * @param registryConfig 服务注册的基本信息
     * @throws Exception 抛出异常
     */
    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryConfig.getRegistryAddr(),
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder
                .builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(
                        serviceMeta.getServiceName(),
                        serviceMeta.getServiceVersion(),
                        serviceMeta.getServiceGroup())
                )
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(
                        serviceMeta.getServiceName(),
                        serviceMeta.getServiceVersion(),
                        serviceMeta.getServiceGroup())
                )
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashcode) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        ServiceInstance<ServiceMeta> instance = this.selectOneServiceInstance((List<ServiceInstance<ServiceMeta>>) serviceInstances);
        if (instance != null) {
            return instance.getPayload();
        }
        return null;
    }

    private ServiceInstance<ServiceMeta> selectOneServiceInstance(List<ServiceInstance<ServiceMeta>> serviceInstances) {
        if (serviceInstances == null || serviceInstances.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(serviceInstances.size());
        return serviceInstances.get(index);
    }

    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}
