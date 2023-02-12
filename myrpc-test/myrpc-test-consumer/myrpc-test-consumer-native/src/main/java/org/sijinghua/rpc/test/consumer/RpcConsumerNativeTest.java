package org.sijinghua.rpc.test.consumer;

import org.sijinghua.rpc.consumer.RpcClient;
import org.sijinghua.rpc.test.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumerNativeTest {
    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerNativeTest.class);

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("1.0.0", "sijinghua", "jdk",
                3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("Jonathan");
        logger.info("返回的结果数据 ===>>> " + result);
        rpcClient.shutdown();
    }
}
