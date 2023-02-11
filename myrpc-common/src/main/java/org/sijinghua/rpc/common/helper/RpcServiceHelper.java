package org.sijinghua.rpc.common.helper;

/**
 * Rpc的字符串拼接工具
 */
public class RpcServiceHelper {
    /**
     * 拼接字符串
     * @param serviceName 服务名称
     * @param version 服务版本号
     * @param group 服务分组
     * @return 服务名称#服务版本号#服务分组
     */
    public static String buildServiceKey(String serviceName, String version, String group) {
        return String.join("#", serviceName, version, group);
    }
}
