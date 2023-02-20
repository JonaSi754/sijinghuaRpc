package org.sijinghua.rpc.test.consumer;

import org.junit.Before;
import org.junit.Test;
import org.sijinghua.rpc.consumer.RpcClient;
import org.sijinghua.rpc.proxy.api.async.IAsyncObjectProxy;
import org.sijinghua.rpc.proxy.api.future.RpcFuture;
import org.sijinghua.rpc.test.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumerNativeTest {
    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerNativeTest.class);

    private RpcClient rpcClient;

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("127.0.0.1:2181", "zookeeper", "1.0.0",
                "sijinghua", "jdk", 3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("Jonathan");
        logger.info("返回的结果数据 ===>>> " + result);
        rpcClient.shutdown();
    }

    @Before
    public void initRpcClient() {
        rpcClient = new RpcClient("127.0.0.1:2181", "zookeeper", "1.0.0",
                "sijinghua", "protostuff", 3000, false, false);
    }

    @Test
    public void testInterfaceRpc() {
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("Jona");
        logger.info("返回的数据 ===>>> " + result);
        rpcClient.shutdown();
    }

    @Test
    public void testAsyncInterfaceRpc() throws Exception {
        RpcClient rpcClient = new RpcClient("127.0.0.1:2181", "zookeeper", "1.0.0",
                "sijinghua", "jdk", 3000, false, false);
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RpcFuture future = demoService.call("hello", "meat");
        logger.info("返回的结果数据 ===>>> " + future.get());
        rpcClient.shutdown();
    }
}
