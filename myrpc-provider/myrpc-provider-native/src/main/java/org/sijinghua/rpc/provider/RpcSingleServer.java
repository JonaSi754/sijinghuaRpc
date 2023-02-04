package org.sijinghua.rpc.provider;

import org.sijinghua.rpc.common.scanner.server.RpcServiceScanner;
import org.sijinghua.rpc.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcSingleServer extends BaseServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    public RpcSingleServer(String serverAddress, String scanPackage) {
        super(serverAddress);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationAndRegistryService(scanPackage);
        } catch (Exception e) {
            logger.error("RPC server init error");
        }
    }
}
