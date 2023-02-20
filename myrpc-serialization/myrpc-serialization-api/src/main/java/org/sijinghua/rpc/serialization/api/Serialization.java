package org.sijinghua.rpc.serialization.api;

import org.sijinghua.rpc.constants.RpcConstants;
import org.sijinghua.rpc.spi.annotation.SPI;

@SPI(RpcConstants.SERIALIZATION_JDK)
public interface Serialization {
    /**
     * 序列化
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] data, Class<T> cls);
}
