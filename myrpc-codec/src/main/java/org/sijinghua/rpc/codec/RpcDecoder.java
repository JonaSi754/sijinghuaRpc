package org.sijinghua.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.sijinghua.rpc.common.utils.SerializationUtils;
import org.sijinghua.rpc.constants.RpcConstants;
import org.sijinghua.rpc.protocol.RpcProtocol;
import org.sijinghua.rpc.protocol.enumeration.RpcType;
import org.sijinghua.rpc.protocol.header.RpcHeader;
import org.sijinghua.rpc.protocol.request.RpcRequest;
import org.sijinghua.rpc.protocol.response.RpcResponse;
import org.sijinghua.rpc.serialization.api.Serialization;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder implements RpcCodec {
    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 可读长度小于header长度则返回
        if (in.readableBytes() < RpcConstants.HEADER_TOTAL_LEN) {
            return;
        }
        in.markReaderIndex();

        // 通过魔数判断是否使用自定义Rpc框架
        short magic = in.readShort();
        if (magic != RpcConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }

        // 依次读取header内容
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();

        ByteBuf serializationTypeByteBuf = in.readBytes(SerializationUtils.MAX_SERIALIZATION_TYPE_COUNT);
        String serializationType = SerializationUtils.subString(serializationTypeByteBuf.toString(StandardCharsets.UTF_8));

        int dataLength = in.readInt();;
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        RpcType msgTypeEnum = RpcType.findByType(msgType);
        // 没有对应的消息类型
        if (msgTypeEnum == null) {
            return;
        }

        RpcHeader header = new RpcHeader();
        header.setMagic(magic);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerializationType(serializationType);
        header.setMsgLen(dataLength);
        // TODO Serialization是扩展点
        Serialization serialization = getJdkSerialization();
        switch (msgTypeEnum) {
            case REQUEST:
                RpcRequest request = serialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = serialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                // TODO
                break;
        }
    }
}
