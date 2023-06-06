package com.avan.simpleraft.rpc.codec;

import java.util.List;

import com.avan.simpleraft.rpc.constants.ReqType;
import com.avan.simpleraft.rpc.constants.RpcConstant;
import com.avan.simpleraft.rpc.core.Header;
import com.avan.simpleraft.rpc.core.RpcProtocol;
import com.avan.simpleraft.rpc.core.RpcRequest;
import com.avan.simpleraft.rpc.core.RpcResponse;
import com.avan.simpleraft.rpc.serial.ISerializer;
import com.avan.simpleraft.rpc.serial.SerializerManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("==========begin RpcDecoder ==============");
        if(in.readableBytes() < RpcConstant.HEAD_TOTAL_LENGTH){
            return;
        }
        in.markReaderIndex();
        short magic = in.readShort();
        if(magic != RpcConstant.MAGIC){
            throw new IllegalArgumentException("Illegal request parameter 'magic',"+magic);
        }
        byte serialType = in.readByte();
        byte reqType = in.readByte();
        long requestId = in.readLong();
        int dataLength = in.readInt();
        if(dataLength < in.readableBytes()){
            in.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[dataLength];
        in.readBytes(bytes);
        Header header = new Header(magic, serialType, reqType, requestId, dataLength);
        ISerializer iSerializer = SerializerManager.getISerializer(serialType);
        ReqType rt = ReqType.findByCode(reqType);
        switch(rt){
            case REQUEST:
                RpcRequest request = iSerializer.reserialize(bytes, RpcRequest.class);
                RpcProtocol<RpcRequest> reqProto = new RpcProtocol<RpcRequest>(header, request);
                out.add(reqProto);
                break;
            case RESPONSE:
                RpcResponse response = iSerializer.reserialize(bytes, RpcResponse.class);
                RpcProtocol<RpcResponse> repProto = new RpcProtocol<RpcResponse>(header, response);
                out.add(repProto);
                break;
            case HEARTBEAT:
                break;
            default:
                break;
        }
    }
    
}
