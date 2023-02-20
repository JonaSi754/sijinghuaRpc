package org.sijinghua.rpc.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import org.sijinghua.rpc.common.exception.SerializerException;
import org.sijinghua.rpc.serialization.api.Serialization;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SPIClass
public class KryoSerialization implements Serialization {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerialization.class);

    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute kryo serialization...");
        if (obj == null) {
            throw new SerializerException("object to be serialized is null");
        }
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(obj.getClass(), new JavaSerializer());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeClassAndObject(output, obj);
        output.flush();
        output.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            throw new SerializerException(e.getMessage(), e);
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute kryo deserialization...");
        if (data == null) {
            throw new SerializerException("data to be deserialized is null");
        }
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.register(cls, new JavaSerializer());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);
        return (T) kryo.readClassAndObject(input);
    }
}
