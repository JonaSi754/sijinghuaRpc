package org.sijinghua.rpc.common.scanner.server;

import org.sijinghua.rpc.annotation.RpcService;
import org.sijinghua.rpc.common.helper.RpcServiceHelper;
import org.sijinghua.rpc.common.scanner.ClassScanner;
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
    public static Map<String, Object> doScannerWithRpcServiceAnnotationAndRegistryService(String scanPackage) throws Exception {
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
                    // TODO 后续逻辑向注册中心注册服务元数据
                    // handlerMap中的Key先简单存储为ServiceName + version + group，后续根据实际情况处理key
                    String serviceName = rpcService.interfaceClassName();
                    String key = RpcServiceHelper.buildServiceKey(serviceName, rpcService.version(), rpcService.group());
                    handlerMap.put(key, clazz.newInstance());
                }
            } catch (Exception e) {
                logger.error("scan classes throws exception: ", e);
            }
        });
        return handlerMap;
    }
}
