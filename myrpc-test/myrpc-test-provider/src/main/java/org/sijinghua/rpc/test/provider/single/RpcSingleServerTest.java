package org.sijinghua.rpc.test.provider.single;

import org.junit.Test;
import org.sijinghua.rpc.provider.RpcSingleServer;

public class RpcSingleServerTest {
    @Test
    public void startRpcSingleServer() {
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880", "127.0.0.1:2181",
                "zookeeper", "org.sijinghua.rpc.test", "asm");
        singleServer.startNettyServer();
    }
}
