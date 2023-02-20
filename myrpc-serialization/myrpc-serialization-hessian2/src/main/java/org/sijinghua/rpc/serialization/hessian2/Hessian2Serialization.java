package org.sijinghua.rpc.serialization.hessian2;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.sijinghua.rpc.common.exception.SerializerException;
import org.sijinghua.rpc.serialization.api.Serialization;
import org.sijinghua.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SPIClass
public class Hessian2Serialization implements Serialization {
    private static final Logger logger = LoggerFactory.getLogger(Hessian2Serialization.class);

    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute hessian2 serialization...");
        if (obj == null) {
            throw new SerializerException("object to be serialized is null");
        }
        byte[] result = new byte[0];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);
        try {
            hessian2Output.startMessage();
            hessian2Output.writeObject(obj);
            hessian2Output.flush();
            hessian2Output.completeMessage();
            result = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializerException(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute hessian2 deserialization...");
        if (data == null) {
            throw new SerializerException("data to be deserialized is null");
        }
        T object = null;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Hessian2Input hessian2Input = new Hessian2Input(byteArrayInputStream);
        try {
            hessian2Input.startMessage();
            object = (T) hessian2Input.readObject();
            hessian2Input.completeMessage();
        } catch (IOException e) {
            throw new SerializerException(e.getMessage(), e);
        } finally {
            try {
                if (null != hessian2Input) {
                    hessian2Input.close();
                    byteArrayInputStream.close();
                }
            } catch (IOException e) {
                throw new SerializerException(e.getMessage(), e);
            }
            return object;
        }
    }
}
