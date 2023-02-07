package org.sijinghua.rpc.test.provider.service.impl;

import org.sijinghua.rpc.annotation.RpcService;
import org.sijinghua.rpc.test.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RpcService(interfaceClass = DemoService.class, interfaceClassName = "org.sijinghua.rpc.test.api.DemoService",
version = "1.0.0", group = "sijinghua")
public class ProviderDemoServiceImpl implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);

    @Override
    public String hello(String str) {
        logger.info("调用hello方法传入的参数为 ===>>> {}", str);
        return "hello, " + str + ", this is sijinghua's rpc.";
    }
}
