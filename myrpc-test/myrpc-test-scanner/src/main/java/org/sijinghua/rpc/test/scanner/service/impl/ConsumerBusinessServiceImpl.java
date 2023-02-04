package org.sijinghua.rpc.test.scanner.service.impl;

import org.sijinghua.rpc.annotation.RpcReference;
import org.sijinghua.rpc.test.scanner.service.ConsumerBusinessService;
import org.sijinghua.rpc.test.scanner.service.DemoService;

public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(group = "sijinghua")
    private DemoService demoService;
}
