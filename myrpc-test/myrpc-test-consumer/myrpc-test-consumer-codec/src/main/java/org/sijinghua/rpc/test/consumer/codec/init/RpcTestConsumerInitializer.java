package org.sijinghua.rpc.test.consumer.codec.init;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.sijinghua.rpc.codec.RpcDecoder;
import org.sijinghua.rpc.codec.RpcEncoder;
import org.sijinghua.rpc.test.consumer.codec.handler.RpcTestConsumerHandler;

public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new RpcEncoder());
        cp.addLast(new RpcDecoder());
        cp.addLast(new RpcTestConsumerHandler());
    }
}
