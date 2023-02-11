package org.sijinghua.rpc.test.consumer.handler;

import org.sijinghua.rpc.consumer.common.RpcConsumer;
import org.sijinghua.rpc.consumer.common.context.RpcContext;
import org.sijinghua.rpc.protocol.RpcProtocol;
import org.sijinghua.rpc.protocol.enumeration.RpcType;
import org.sijinghua.rpc.protocol.header.RpcHeaderFactory;
import org.sijinghua.rpc.protocol.request.RpcRequest;
import org.sijinghua.rpc.proxy.api.callback.AsyncRpcCallback;
import org.sijinghua.rpc.proxy.api.future.RpcFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcConsumerHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);

    public static void main(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance();
        RpcFuture future = consumer.sendRequest(getRpcRequestProtocol());
        future.addCallBack(new AsyncRpcCallback() {
            @Override
            public void onSuccess(Object result) {
                logger.info("从服务消费者获得的消息 ===>>> " + result);
            }

            @Override
            public void onException(Exception e) {
                logger.info("抛出了异常 ===>>> " + e);
            }
        });
        // 避免消费者与服务者在数据交互过程中消费者断开连接
        Thread.sleep(200);
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
        request.setOneway(true);
        protocol.setBody(request);
        return protocol;
    }
}
