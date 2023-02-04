package org.sijinghua.rpc.codec;


import org.sijinghua.rpc.serialization.api.Serialization;
import org.sijinghua.rpc.serialization.jdk.JdkSerialization;

public interface RpcCodec {
    default Serialization getJdkSerialization() {
        return new JdkSerialization();
    }
}
