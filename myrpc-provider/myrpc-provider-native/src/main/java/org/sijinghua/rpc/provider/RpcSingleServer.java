package org.sijinghua.rpc.provider;

import org.sijinghua.rpc.common.scanner.server.RpcServiceScanner;
import org.sijinghua.rpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcSingleServer extends BaseServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    public RpcSingleServer(String serverAddress, String scanPackage, String reflectType) {
        super(serverAddress, reflectType);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationAndRegistryService(scanPackage);
        } catch (Exception e) {
            logger.error("RPC server init error");
        }
    }
}
