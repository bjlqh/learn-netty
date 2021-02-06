package org.example.netty.heartbeat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class NettyServer {

    public static void main(String[] args) {
        //创建两个线程组 bossGroup 和 workGroup,
        //bossGroup 只是处理连接请求，真正和客户端业务处理，会交给 workerGroup 处理
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //workerGroup 含有的子线程 NioEventLoop 的个数默认为 CPU 核数的两倍
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务器端的请求对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 配置参数
            bootstrap.group(bossGroup, workerGroup); // 设置两个线程组
            bootstrap.channel(NioServerSocketChannel.class); // 使用 NioServerSocketChannel 作为服务器的通道实现
            //初始化服务器连接队列大小，服务端处理客户端连接请求是顺序处理的， 所以同一时间只能处理一个客户端连接。
            //多个客户端同时来的时候，服务端将不能处理的客户端连接请求，放在队列中等待处理
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                //创建通道初始化对象，设置初始化参数
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("encoder",new StringEncoder());
                    pipeline.addLast("decoder",new StringDecoder());
                    //IdleStateHandler的readerIdleTime参数指定超过3秒还没有收到客户端的连接。
                    //会触发IdleStateEvent事件并且交给下一个handler处理，下一个handler必须实现userEventTriggered方法，处理对应的事件。
                    pipeline.addLast(new IdleStateHandler(3,0,0, TimeUnit.SECONDS));
                    pipeline.addLast(new HeartBeatServerHandler());
                }
            });
            System.out.println("netty server start...");
            // 绑定一个端口并且同步，生成一个 ChannelFuture 异步对象，通过 isDone() 等方法可以判断异步事件的执行情况。
            // 启动服务器(并绑定端口)，bind 是异步事件，sync方法是等待异步操作执行完毕
            ChannelFuture cf = bootstrap.bind(9000).sync();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        System.out.println("--------------");
    }
}
