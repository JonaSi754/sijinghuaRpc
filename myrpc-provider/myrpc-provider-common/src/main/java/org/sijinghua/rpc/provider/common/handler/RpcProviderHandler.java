package org.sijinghua.rpc.provider.common.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RpcProviderHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

    private final Map<String, Object> handler;

    public RpcProviderHandler(Map<String, Object> handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        logger.info("Rpc提供者收到的数据为：{}", o.toString());
        logger.info("handlerMap中存放的数据如下所示：");
        for (Map.Entry<String, Object> entry: handler.entrySet()) {
            logger.info(entry.getKey() + " === " + entry.getValue());
        }
        // 直接返回数据
        ctx.writeAndFlush(o);
    }
}
