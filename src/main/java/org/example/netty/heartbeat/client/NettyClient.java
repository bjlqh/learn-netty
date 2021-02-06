package org.example.netty.heartbeat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class NettyClient {

    public static void main(String[] args) {
        //客户端需要一个事件循环组
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            //创建客户端启动对象
            //注意客户端使用的不是 ServerBootStrap 而是 BootStrap
            Bootstrap bootstrap = new Bootstrap();
            // 设置相关参数
            bootstrap.group(loopGroup); // 设置线程组
            bootstrap.channel(NioSocketChannel.class); // 使用 NioSocketChannel 作为客户端的通道实现
            bootstrap.handler(new ChannelInitializer<SocketChannel>() { // 加入处理器
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("encoder", new StringEncoder());
                    pipeline.addLast("decoder", new StringDecoder());
                    pipeline.addLast(new HeartBeatClientHandler());
                }
            });
            System.out.println("netty client start");
            //启动客户端去连接服务器端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    Channel channel = future.channel();
                    if (!channel.isActive()) {
                        System.out.println("即将关闭客户端");
                        channel.close();
                    }
                }
            });
            handler2(channelFuture);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }

    private static void handler2(ChannelFuture channelFuture) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            channelFuture.channel().writeAndFlush(s);
        }
    }

    private static void handler(ChannelFuture channelFuture) throws InterruptedException {
        int count = 0;
        while (channelFuture.channel().isActive()) {
            Thread.sleep(1000);
            if (count % 2 == 0) {
                channelFuture.channel().writeAndFlush("咚");
            } else {
                channelFuture.channel().writeAndFlush("砰砰");
            }
            count++;
        }
    }
}
