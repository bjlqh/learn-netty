package org.example.netty.reconnection.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

public class NettyClient {

    private String host;
    private int port;
    private Bootstrap bootstrap;
    private EventLoopGroup group;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    private void init() {
        //客户端需要一个事件循环组
        group = new NioEventLoopGroup();
        //创建客户端启动对象
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new NettyClientHandler(NettyClient.this));
                    }
                });
    }

    public void connect() throws InterruptedException {
        System.out.println("netty client start");
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                //重连交给后端线程处理
                if (!future.isSuccess()) {
                    future.channel().eventLoop().schedule(() -> {
                        System.err.println("重连服务端...");
                        try {
                            connect();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }, 3000, TimeUnit.MILLISECONDS);
                } else {
                    System.out.println("连接服务端成功");
                }
            }
        });
        channelFuture.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws InterruptedException {
        NettyClient nettyClient = new NettyClient("127.0.0.1", 9000);
        nettyClient.connect();
    }
}
