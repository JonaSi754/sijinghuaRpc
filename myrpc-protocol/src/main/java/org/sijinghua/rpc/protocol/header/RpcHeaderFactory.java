package org.sijinghua.rpc.protocol.header;

import org.sijinghua.rpc.common.id.idFactory;
import org.sijinghua.rpc.constants.RpcConstants;

public class RpcHeaderFactory {
    public static RpcHeader getRequestHeader(String serializationType, int messageType) {
        RpcHeader header = new RpcHeader();
        long requestId = idFactory.getId();
        header.setMagic(RpcConstants.MAGIC);
        header.setRequestId(requestId);
        header.setMsgType((byte)messageType);
        header.setStatus((byte)0x1);
        header.setSerializationType(serializationType);
        return header;
    }
}
