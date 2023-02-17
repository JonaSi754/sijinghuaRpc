package org.sijinghua.rpc.provider.common.scanner;

import org.sijinghua.rpc.annotation.RpcService;
import org.sijinghua.rpc.common.helper.RpcServiceHelper;
import org.sijinghua.rpc.common.scanner.ClassScanner;
import org.sijinghua.rpc.protocol.meta.ServiceMeta;
import org.sijinghua.rpc.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description @RpcService注解扫描器
 */
public class RpcServiceScanner extends ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(RpcServiceScanner.class);

    /**
     * 扫描指定包下的类，并筛选@RpcService注解标注的类
     */
    public static Map<String, Object> doScannerWithRpcServiceAnnotationAndRegistryService
    (String host, int port, String scanPackage, RegistryService registryService) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList.isEmpty()) {
            return handlerMap;
        }
        classNameList.forEach((className) -> {
            try {
                Class<?> clazz = Class.forName(className);
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                if (rpcService != null) {
                    // 优先使用interfaceClass，interfaceClass的name为空，再使用interfaceClassName
                    // 向注册中心注册服务元数据
                    ServiceMeta serviceMeta = new ServiceMeta(getServiceName(rpcService), rpcService.version(),
                            rpcService.group(), host, port);
                    registryService.register(serviceMeta);
                    // handlerMap中的Key先简单存储为ServiceName + version + group，后续根据实际情况处理key
                    handlerMap.put(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(),
                            serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()), clazz.newInstance());
                }
            } catch (Exception e) {
                logger.error("scan classes throws exception: ", e);
            }
        });
        return handlerMap;
    }

    /**
     * 获取serviceName
     */
    private static String getServiceName(RpcService rpcService) {
        // 优先使用interfaceClass
        Class clazz = rpcService.interfaceClass();
        if (clazz == null || clazz == void.class) {
            return rpcService.interfaceClassName();
        }
        String serviceName = clazz.getName();
        if (serviceName == null || serviceName.trim().isEmpty()) {
            serviceName = rpcService.interfaceClassName();
        }
        return serviceName;
    }
}
