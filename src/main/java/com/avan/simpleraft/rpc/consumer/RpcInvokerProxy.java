package com.avan.simpleraft.rpc.consumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.avan.simpleraft.rpc.constants.ReqType;
import com.avan.simpleraft.rpc.constants.RpcConstant;
import com.avan.simpleraft.rpc.constants.SerilizeType;
import com.avan.simpleraft.rpc.core.Header;
import com.avan.simpleraft.rpc.core.RequestHolder;
import com.avan.simpleraft.rpc.core.RpcFuture;
import com.avan.simpleraft.rpc.core.RpcProtocol;
import com.avan.simpleraft.rpc.core.RpcRequest;
import com.avan.simpleraft.rpc.core.RpcResponse;
import com.avan.simpleraft.rpc.protocol.NettyClient;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcInvokerProxy<T> implements InvocationHandler, MethodInterceptor{

    private String address;
    private int port;
    NettyClient client;
    private int timeout;


    public RpcInvokerProxy(String address, int port) {
        this.address = address;
        this.port = port;
        timeout = 100;
        client = new NettyClient(address, port);
    }

    @Override
    public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
        
        long requestId = RequestHolder.REQUEST_ID.getAndIncrement();
        Header header = new Header(RpcConstant.MAGIC, SerilizeType.JAVATYPE.code(), ReqType.REQUEST.code(), requestId, 0);
        RpcRequest request = new RpcRequest(arg1.getDeclaringClass().getName(), arg1.getName(), arg2, arg1.getParameterTypes());
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>(header, request);
        client.sendRequest(protocol);
        RpcFuture<RpcResponse> future = new RpcFuture<RpcResponse>(new DefaultPromise<>(new DefaultEventLoop()));
        RequestHolder.REQUEST_MAP.put(requestId, future);
        try {
            RpcResponse response = future.getPromise().get(timeout, TimeUnit.MILLISECONDS);
            return response.getData();
        } catch (TimeoutException e) {
            return null;
        }
    }

    @Override
    public Object intercept(Object arg0, Method arg1, Object[] arg2, MethodProxy arg3) throws Throwable {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'intercept'");
    }
    
}
