package org.example.netty.splitpackage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.example.netty.splitpackage.utils.MyMessageEncoder;

public class ChatClient {

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
                    //客户端：入栈事件  从head->tail (自上而下)  使用的 inboundhandler
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new MyMessageEncoder());
                    pipeline.addLast(new ChatClientHandler());
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
            //得到 channel
            Channel channel = channelFuture.channel();
            System.out.println("=========" + channel.localAddress() + "=========");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }
}
