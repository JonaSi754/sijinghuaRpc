package org.sijinghua.rpc.spi.factory;

import org.sijinghua.rpc.spi.annotation.SPI;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.sijinghua.rpc.spi.loader.ExtensionLoader;

import java.util.Optional;

@SPIClass
public class SpiExtensionFactory implements ExtensionFactory {
    @Override
    public <T> T getExtension(final String key, final Class<T> clazz) {
        return Optional.ofNullable(clazz)
                .filter(Class::isInterface)
                .filter(cls -> cls.isAnnotationPresent(SPI.class))
                .map(ExtensionLoader::getExtensionLoader)
                .map(ExtensionLoader::getDefaultSpiClassInstance)
                .orElse(null);
    }
}
