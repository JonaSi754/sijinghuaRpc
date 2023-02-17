package org.sijinghua.rpc.provider.common.server.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.sijinghua.rpc.codec.RpcDecoder;
import org.sijinghua.rpc.codec.RpcEncoder;
import org.sijinghua.rpc.provider.common.handler.RpcProviderHandler;
import org.sijinghua.rpc.provider.common.server.api.Server;
import org.sijinghua.rpc.registry.api.RegistryService;
import org.sijinghua.rpc.registry.api.config.RegistryConfig;
import org.sijinghua.rpc.registry.zookeeper.ZookeeperRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BaseServer implements Server {
    private static final Logger logger = LoggerFactory.getLogger(BaseServer.class);

    // 主机域名或IP地址
    protected String host = "127.0.0.1";

    // 端口号
    protected int port = 27110;

    // 反射类型
    private String reflectType;

    // 存储的是实体类关系
    protected Map<String, Object> handlerMap = new HashMap<>();

    // 服务注册实例
    protected RegistryService registryService;

    public BaseServer(String serverAddress, String registryAddress, String registryType, String reflectType) {
        if (!StringUtils.isEmpty(serverAddress)) {
            String[] serverArray = serverAddress.split(":");
            this.host = serverArray[0];
            this.port = Integer.parseInt(serverArray[1]);
        }
        this.registryService = getRegistryService(registryAddress, registryType);
        this.reflectType = reflectType;
    }

    private RegistryService getRegistryService(String registryAddress, String registryType) {
        // TODO 后续扩展支持SPI
        RegistryService registryService = null;
        try {
            registryService = new ZookeeperRegistryService();
            registryService.init(new RegistryConfig(registryAddress, registryType));
        } catch (Exception e) {
            logger.error("RPC server init error", e);
        }
        return registryService;
    }

    /**
     * 启动服务端Netty服务器
     */
    @Override
    public void startNettyServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcDecoder())
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcProviderHandler(reflectType, handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            logger.info("Server started on: {}:{}", host, port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("Rpc server start error", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
