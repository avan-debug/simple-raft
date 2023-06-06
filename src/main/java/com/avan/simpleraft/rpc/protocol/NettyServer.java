package com.avan.simpleraft.rpc.protocol;

import org.apache.commons.logging.Log;

import com.avan.simpleraft.rpc.codec.RpcDecoder;
import com.avan.simpleraft.rpc.codec.RpcEncoder;
import com.avan.simpleraft.rpc.constants.RpcConstant;
import com.avan.simpleraft.rpc.handler.RpcServerHandler;
import com.avan.simpleraft.rpc.testnetty.NettyServerHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {
    public static void startServer(String hostname, int port, SimpleChannelInboundHandler myhandler){
        startRun(hostname, port, myhandler);
    }
    public static void startRun(String hostname, int port, SimpleChannelInboundHandler myhandler){
        //设置bossGroup线程数为1
        EventLoopGroup bossGroup = new NioEventLoopGroup(3);
        //设置workerGroup线程数为16
        EventLoopGroup workerGroup = new NioEventLoopGroup(16);
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup) //设置两个线程组
            // 使用NioServerSocketChannel作为服务器的通道实现
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 12, 4, 0, 0))
                                .addLast(new RpcDecoder())
                                .addLast(new RpcEncoder())
                                .addLast(myhandler);
                }
            });
            
            log.info("netty server start。。");
            ChannelFuture cf = bootstrap.bind(hostname, port).sync();


            cf.channel().closeFuture().sync();

        }catch(Exception e){

        }finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
