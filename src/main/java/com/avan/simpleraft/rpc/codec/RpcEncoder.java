package com.avan.simpleraft.rpc.codec;

import com.avan.simpleraft.rpc.core.Header;
import com.avan.simpleraft.rpc.core.RpcProtocol;
import com.avan.simpleraft.rpc.serial.ISerializer;
import com.avan.simpleraft.rpc.serial.SerializerManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>>{

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf out) throws Exception {
        log.info("==========begin RpcEecoder ==============");
        RpcProtocol<Object> rpcProto = msg;
        Header header = rpcProto.getHeader();
        short magic = header.getMagic();
        byte serialType = header.getSerialType();
        byte reqType = header.getReqType();
        long requestId = header.getRequestId();
        out.writeShort(magic);
        out.writeByte(serialType);
        out.writeByte(reqType);
        out.writeLong(requestId);
        ISerializer iSerializer = SerializerManager.getISerializer(serialType);
        byte[] bytes = iSerializer.serialize(rpcProto.getContent());
        header.setLength(bytes.length);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
