package org.sijinghua.rpc.codec;


import org.sijinghua.rpc.serialization.api.Serialization;
import org.sijinghua.rpc.spi.loader.ExtensionLoader;

public interface RpcCodec {
    /**
     * 根据serializationType通过SPI获取序列化句柄
     * @param serializationType 序列化方式
     * @return Serialization对象
     */
    default Serialization getSerialization(String serializationType) {
        return ExtensionLoader.getExtension(Serialization.class, serializationType);
    }
}
