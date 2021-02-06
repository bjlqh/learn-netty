package org.example.netty.splitpackage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.example.netty.splitpackage.utils.MyMessageDecoder;

public class ChatServer {

    public static void main(String[] args) {
        //创建处理连接请求的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //创建处理客户端业务的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //创建服务器端的请求对象
        ServerBootstrap bootstrap = new ServerBootstrap();
        //配置参数
        try {
            //设置两个线程组
            bootstrap.group(bossGroup, workerGroup);
            //使用 NioServerSocketChannel作为服务器的通道实现
            bootstrap.channel(NioServerSocketChannel.class);
            //初始化服务器端请求队列，设置大小，多个客户端请求进来放到队列里等待
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //创建并初始化通道，设置通道参数
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //服务端：出栈事件  从tail->head (自下而上)  使用的 outboundhandler
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new MyMessageDecoder());
                    pipeline.addLast(new ChatServerHandler());
                }
            });
            System.out.println("server启动...");
            ChannelFuture channelFuture = bootstrap.bind(9000).sync();
            //关闭通道
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
