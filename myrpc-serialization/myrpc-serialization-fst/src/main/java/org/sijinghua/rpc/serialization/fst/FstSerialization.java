package org.sijinghua.rpc.serialization.fst;

import org.nustaq.serialization.FSTConfiguration;
import org.sijinghua.rpc.common.exception.SerializerException;
import org.sijinghua.rpc.serialization.api.Serialization;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SPIClass
public class FstSerialization implements Serialization {
    private static final Logger logger = LoggerFactory.getLogger(FstSerialization.class);

    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute fst serialization...");
        if (obj == null) {
            throw new SerializerException("object to be serialized is null");
        }
        FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
        return conf.asByteArray(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute fst deserialization...");
        if (data == null) {
            throw new SerializerException("data to be deserialized is null");
        }
        FSTConfiguration conf = FSTConfiguration.getDefaultConfiguration();
        return (T) conf.asObject(data);
    }
}
