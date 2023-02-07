package org.sijinghua.rpc.consumer.common.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.sijinghua.rpc.protocol.RpcProtocol;
import org.sijinghua.rpc.protocol.header.RpcHeader;
import org.sijinghua.rpc.protocol.request.RpcRequest;
import org.sijinghua.rpc.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    private static final Logger logger = LoggerFactory.getLogger(RpcConsumerHandler.class);

    private volatile Channel channel;

    private SocketAddress remotePeer;

    // 存储请求ID和RpcResponse协议的映射关系，持续等待与之对应的键值对的出现
    private Map<Long, RpcProtocol<RpcResponse>> pendingResponse = new ConcurrentHashMap<>();

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> protocol) throws Exception {
        if (protocol == null) {
            return;
        }
        logger.info("服务消费者接受到的数据 ===>>> {}", JSONObject.toJSONString(protocol));
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        pendingResponse.put(requestId, protocol);
    }

    /**
     * 服务消费者向服务提供者发送请求
     */
    public Object sendRequest(RpcProtocol<RpcRequest> protocol) {
        logger.info("服务消费者发送的数据 ===>>> {}", JSONObject.toJSONString(protocol));
        channel.writeAndFlush(protocol);
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        // 异步转同步
        while (true) {
            RpcProtocol<RpcResponse> responseRpcProtocol = pendingResponse.remove(requestId);
            if (responseRpcProtocol != null) {
                return responseRpcProtocol.getBody().getResult();
            }
        }
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
