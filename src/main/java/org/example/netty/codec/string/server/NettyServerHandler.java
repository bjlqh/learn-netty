package org.example.netty.codec.string.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 自定义 Handler 需要继承 netty规定好的某个 HandlerAdapter
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("客户端发送的消息是：" + msg);
    }

    /**
     * 数据读取完毕处理的方式
     *
     * @param ctx 上下文对象，含有通道的 channel,管道 pipeline
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("HelloClient");
    }

    /**
     * 处理异常，一般是关闭通道
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
