package com.avan.simpleraft.rpc.handler;

import java.lang.reflect.Method;

import com.avan.simpleraft.rpc.constants.ReqType;
import com.avan.simpleraft.rpc.core.Header;
import com.avan.simpleraft.rpc.core.RpcProtocol;
import com.avan.simpleraft.rpc.core.RpcRequest;
import com.avan.simpleraft.rpc.core.RpcResponse;
import com.avan.simpleraft.rpc.spring.SpringBeansManager;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>>{

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> msg) throws Exception {
        RpcProtocol<RpcResponse> protocol = new RpcProtocol<RpcResponse>();
        Header header = msg.getHeader();
        RpcRequest request = msg.getContent();
        Object res = invoke(request);
        header.setReqType(ReqType.RESPONSE.code());
        RpcResponse response = new RpcResponse();
        response.setData(res);
        response.setMsg("success");
        
        protocol.setHeader(header);
        protocol.setContent(response);
        ctx.writeAndFlush(protocol);
    }

    protected Object invoke(RpcRequest request){
        try{
            Class<?> clazz = Class.forName(request.getClassName());
            Object bean = SpringBeansManager.getBean(clazz);
            Method method = clazz.getMethod(request.getMethodName(), request.getParamTypes());
            Object[] params = request.getParams();
            Object res = method.invoke(bean, params);
            return res;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }




}
