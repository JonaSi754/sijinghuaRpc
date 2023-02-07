package org.sijinghua.rpc.test.consumer.handler;

import org.sijinghua.rpc.consumer.common.RpcConsumer;
import org.sijinghua.rpc.protocol.RpcProtocol;
import org.sijinghua.rpc.protocol.enumeration.RpcType;
import org.sijinghua.rpc.protocol.header.RpcHeaderFactory;
import org.sijinghua.rpc.protocol.request.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumerHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);

    public static void main(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance();
        Object result = consumer.sendRequest(getRpcRequestProtocol());
        logger.info("从服务消费者获取到的数据 ===>>> " + result.toString());
        consumer.close();
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocol() {
        // 模拟数据发送
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk", RpcType.REQUEST.getType()));
        RpcRequest request = new RpcRequest();
        request.setClassName("org.sijinghua.rpc.test.api.DemoService");
        request.setGroup("sijinghua");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"sijinghua"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }
}
