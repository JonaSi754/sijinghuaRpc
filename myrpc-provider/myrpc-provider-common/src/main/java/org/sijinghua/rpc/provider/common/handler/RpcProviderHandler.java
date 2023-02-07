package org.sijinghua.rpc.provider.common.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.sijinghua.rpc.common.helper.RpcServiceHelper;
import org.sijinghua.rpc.common.threadpool.ServerThreadPool;
import org.sijinghua.rpc.constants.RpcConstants;
import org.sijinghua.rpc.protocol.RpcProtocol;
import org.sijinghua.rpc.protocol.enumeration.RpcStatus;
import org.sijinghua.rpc.protocol.enumeration.RpcType;
import org.sijinghua.rpc.protocol.header.RpcHeader;
import org.sijinghua.rpc.protocol.request.RpcRequest;
import org.sijinghua.rpc.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    private static final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

    // 采用哪种类型调用真实方法
    private final String reflectType;

    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(String reflectType, Map<String, Object> handlerMap) {
        this.reflectType = reflectType;
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {
        // 调用ServerThreadPool类的submit方法使任务异步执行
        ServerThreadPool.submit(() -> {
            RpcHeader header = protocol.getHeader();
            header.setMsgType((byte) RpcType.RESPONSE.getType());
            RpcRequest request = protocol.getBody();
            logger.debug("Receive request " + header.getRequestId());
            RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();

            try {
                Object result = handle(request);
                response.setResult(result);
                response.setAsync(request.getAsync());
                response.setOneway(request.getOneway());
                header.setStatus((byte) RpcStatus.SUCCESS.getCode());
            } catch (Throwable t) {
                response.setError(t.toString());
                header.setStatus((byte) RpcStatus.FAIL.getCode());
                logger.error("RPC Server handle request error", t);
            }
            responseRpcProtocol.setHeader(header);
            responseRpcProtocol.setBody(response);
            ctx.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    logger.debug("Send response for request " + header.getRequestId());
                }
            });
        });
    }

    private Object handle(RpcRequest request) throws Throwable {
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(),
                request.getVersion(), request.getGroup());
        for (Map.Entry <String, Object> entry: handlerMap.entrySet()) {
            logger.info("key: " + entry.getKey() + ", value: " + entry.getValue());
        }
        Object serviceBean = handlerMap.get(serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist: %s:%s",
                    request.getClassName(), request.getMethodName()));
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        logger.debug(serviceClass.getName());
        logger.debug(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (Class<?> parameterType : parameterTypes) {
                logger.debug(parameterType.getName());
            }
        }

        if (parameters != null && parameters.length > 0) {
            for (Object parameter : parameters) {
                logger.debug(parameter.toString());
            }
        }
        return invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
    }

    private Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName,
                                Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        switch (this.reflectType) {
            case RpcConstants.REFLECT_TYPE_JDK:
                return this.invokeJDKMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
            case RpcConstants.REFLECT_TYPE_CGLIB:
                return this.invokeCGLibMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
            default:
                throw new IllegalArgumentException("not support reflect type");
        }
    }

    private Object invokeJDKMethod(Object serviceBean, Class<?> serviceClass, String methodName,
                                   Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        // JDK reflect
        logger.info("use jdk reflect type invoke method...");
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }

    private Object invokeCGLibMethod(Object serviceBean, Class<?> serviceClass, String methodName,
                                     Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        // CGLib reflect
        logger.info("use cglib reflect type invoke method...");
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }
}
