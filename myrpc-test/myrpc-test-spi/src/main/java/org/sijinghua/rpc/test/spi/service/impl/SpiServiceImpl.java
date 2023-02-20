package org.sijinghua.rpc.test.spi.service.impl;

import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.sijinghua.rpc.test.spi.service.SpiService;

@SPIClass
public class SpiServiceImpl implements SpiService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
