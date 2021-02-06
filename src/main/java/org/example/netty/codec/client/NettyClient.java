package org.example.netty.codec.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class NettyClient {

    public static void main(String[] args) {
        //客户端需要一个事件循环组
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        // 创建客户端启动对象
        Bootstrap bootstrap = new Bootstrap();
        //设置参数
        try {
            bootstrap.group(loopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast("encoder", new StringEncoder());
                    //pipeline.addLast("decoder", new StringDecoder());
                    //pipeline.addLast(new ObjectDecoder(10240, ClassResolvers.cacheDisabled(null)));
                    pipeline.addLast(new NettyClientHandler());
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
            channelFuture.channel().closeFuture().sync();
            System.out.println("client 启动...");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }
}
