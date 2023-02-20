package org.sijinghua.rpc.serialization.jdk;

import org.sijinghua.rpc.common.exception.SerializerException;
import org.sijinghua.rpc.serialization.api.Serialization;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@SPIClass
public class JdkSerialization implements Serialization {
    private static final Logger logger = LoggerFactory.getLogger(JdkSerialization.class);

    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute jdk serialize...");
        if (obj == null) {
            throw new SerializerException("serialize object is null");
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(obj);
            return os.toByteArray();
        } catch (IOException e) {
            throw new SerializerException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute jdk deserialize...");
        if (data == null) {
            throw new SerializerException("deserialze data is null");
        }
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            ObjectInputStream in = new ObjectInputStream(is);
            return (T) in.readObject();
        } catch (Exception e) {
            throw new SerializerException(e.getMessage(), e);
        }
    }
}
