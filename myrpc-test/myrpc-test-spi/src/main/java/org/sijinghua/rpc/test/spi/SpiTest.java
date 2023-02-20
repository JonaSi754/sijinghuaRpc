package org.sijinghua.rpc.test.spi;

import org.junit.Test;
import org.sijinghua.rpc.spi.loader.ExtensionLoader;
import org.sijinghua.rpc.test.spi.service.SpiService;

public class SpiTest {
    @Test
    public void testSpiLoader() {
        SpiService spiService = ExtensionLoader.getExtension(SpiService.class, "spiService");
        String result = spiService.hello("Jonathan");
        System.out.println(result);
    }
}
