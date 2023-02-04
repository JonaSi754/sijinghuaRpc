package org.sijinghua.rpc.test.scanner.provider;

import org.sijinghua.rpc.annotation.RpcService;
import org.sijinghua.rpc.test.scanner.service.DemoService;

@RpcService(interfaceClass = DemoService.class, interfaceClassName = "org.sijinghua.rpc.test.scanner.service.DemoService",
version = "1.0.0", group = "sijinghua")
public class ProviderDemoServiceImpl implements DemoService {

}
