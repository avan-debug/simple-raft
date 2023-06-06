package com.avan.simpleraft.rpc.protocol;

import com.avan.simpleraft.rpc.codec.RpcDecoder;
import com.avan.simpleraft.rpc.codec.RpcEncoder;
import com.avan.simpleraft.rpc.core.RpcProtocol;
import com.avan.simpleraft.rpc.core.RpcRequest;
import com.avan.simpleraft.rpc.handler.RpcClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {
    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private String address;
    private int port;


    public NettyClient(String address, int port) {
        log.info("begin init NettyClient");
        this.address = address;
        this.port = port;
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 12, 4, 0, 0))
                            .addLast(new RpcEncoder())
                            .addLast(new RpcDecoder())
                            .addLast(new RpcClientHandler());

            };
        });
    }

    public void sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception{
        ChannelFuture future = bootstrap.connect(address, port).sync();

        future.addListener(listener -> {
            if(future.isSuccess()){
                log.info("connect rpc server {} success.",this.address);
            }else{
                log.info("connect rpc server {} fail.",this.address);
                future.cause().printStackTrace();
                group.shutdownGracefully();
            }
        });
        future.channel().writeAndFlush(protocol);
    }

    public String getAddress() {
        return address;
    }



    public void setAddress(String address) {
        this.address = address;
    }



    public int getPort() {
        return port;
    }



    public void setPort(int port) {
        this.port = port;
    }

    
    public void destory() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            
            Bootstrap bootstrap = new Bootstrap();

            bootstrap
            .group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 12, 4, 0, 0))
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new RpcClientHandler());

                };
            });
            System.out.println("netty client start。。");
            //启动客户端去连接服务器端
            ChannelFuture cf = bootstrap.connect("127.0.0.1", 9000).sync();
            cf.channel().closeFuture().sync();
        }finally{
            group.shutdownGracefully();
        }
    }

}
