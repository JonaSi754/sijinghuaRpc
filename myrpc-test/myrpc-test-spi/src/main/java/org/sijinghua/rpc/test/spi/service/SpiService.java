package org.sijinghua.rpc.test.spi.service;

import org.sijinghua.rpc.spi.annotation.SPI;

@SPI("spiService")
public interface SpiService {
    String hello(String name);
}
