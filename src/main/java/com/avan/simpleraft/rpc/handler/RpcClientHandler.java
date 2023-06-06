package com.avan.simpleraft.rpc.handler;

import com.avan.simpleraft.rpc.core.Header;
import com.avan.simpleraft.rpc.core.RequestHolder;
import com.avan.simpleraft.rpc.core.RpcFuture;
import com.avan.simpleraft.rpc.core.RpcProtocol;
import com.avan.simpleraft.rpc.core.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>>{

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> msg) throws Exception {
        log.info("receive rpc server result");
        Header header = msg.getHeader();
        Long reqId = header.getRequestId();
        RpcFuture<RpcResponse> future = RequestHolder.REQUEST_MAP.remove(reqId);
        future.getPromise().setSuccess(msg.getContent());
    }
    
}
