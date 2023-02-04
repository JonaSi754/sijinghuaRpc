package org.sijinghua.rpc.common.scanner.reference;

import org.sijinghua.rpc.annotation.RpcReference;
import org.sijinghua.rpc.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RpcReferenceScanner extends ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(RpcReferenceScanner.class);

    /**
     * 扫描指定包下的类，并筛选使用@RpcReference注释标注的类
     */
    public static Map<String, Object> doScannerWithRpcReferenceAnnotationFilter(String scanPackage) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList.isEmpty()) {
            return handlerMap;
        }
        classNameList.forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                Field[] declaredFields = clazz.getDeclaredFields();
                Stream.of(declaredFields).forEach(field -> {
                    RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                    if (rpcReference != null) {
                        // TODO 处理后序逻辑，将@RpcReference注解标注的接口引用代理对象，放入全局缓存中
                        logger.info("当前标注了@RpcReference注释的字段名称 ===>>> " + field.getName());
                        logger.info("@RpcReference注解上标注的属性如下：");
                        logger.info("version ===>>> " + rpcReference.version());
                        logger.info("group ===>>> " + rpcReference.group());
                        logger.info("registryAddress ===>>> " + rpcReference.registryAddress());
                        logger.info("registryType ===>>> " + rpcReference.registryType());
                    }
                });
            } catch (Exception e) {
                logger.error("scan classes throws exception: ", e);
            }
        });
        return handlerMap;
    }
}
