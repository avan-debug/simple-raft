package com.avan.simpleraft.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.avan.simpleraft.RaftNode;
import com.avan.simpleraft.proto.Request;
import com.avan.simpleraft.proto.Response;
import com.avan.simpleraft.rpc.consumer.RpcInvokerProxy;
import com.avan.simpleraft.rpc.protocol.NettyClient;
import com.avan.simpleraft.service.HandleRequest;
import com.avan.simpleraft.service.HandleRequestImpl;

import lombok.Data;

@Data
public class RpcClient {

    private String address;

    private int port;
    
    private NettyClient nettyClient;

    public RpcClient(String address, int port) {
        this.address = address;
        this.port = port;
        nettyClient = new NettyClient(address, port);
    }

    public <R> R send(Request request){
		return send(request, 100);
    }

    public <R> R send(Request request, int timeout){
		InvocationHandler handler = new RpcInvokerProxy(request.getIp(), request.getPort(), nettyClient, timeout);
		RpcServerInterface proxy = (RpcServerInterface)Proxy.newProxyInstance(RpcServerInterface.class.getClassLoader(), new Class<?>[]{RpcServerInterface.class}, handler);
		Response<R> res = (Response<R>)proxy.handleReq(request);
		return res.getResult();
    }

    public void close(){
        nettyClient.destory();
    }
}
