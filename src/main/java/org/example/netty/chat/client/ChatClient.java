package org.example.netty.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

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
                    //加入特殊分隔符包解码器
                    //pipeline.addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer("_".getBytes())));
                    //向pipeline里添加解码器
                    pipeline.addLast("encoder", new StringEncoder());
                    pipeline.addLast("decoder", new StringDecoder());
                    //向pipeline里添加编码器
                    //加入自己的业务处理 handler
                    pipeline.addLast(new ChatClientHandler());
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
            //得到 channel
            Channel channel = channelFuture.channel();
            System.out.println("=========" + channel.localAddress() + "=========");
            //客户端输入信息
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                //通过channel发送到服务端
                channel.writeAndFlush(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }
}
