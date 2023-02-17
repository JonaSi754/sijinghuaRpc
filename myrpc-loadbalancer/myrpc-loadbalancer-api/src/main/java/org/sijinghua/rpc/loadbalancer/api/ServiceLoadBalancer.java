package org.sijinghua.rpc.loadbalancer.api;

import java.util.List;

public interface ServiceLoadBalancer<T> {
    /**
     * 以负载均衡的方式选择一个服务节点
     * @param servers 服务列表
     * @param hashcode Hash值
     * @return 可用的服务节点
     */
    T select(List<T> servers, int hashcode);
}
