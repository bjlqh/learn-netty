package org.example.netty.codec.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.netty.codec.config.pojo.User;
import org.example.netty.codec.config.pojo.proto.ProtostuffUtil;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("客户端收到");
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        User user = ProtostuffUtil.deserializer(bytes, User.class);
        System.out.println("消息：" + user);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client发送数据");
        ctx.writeAndFlush("测试String编码");
    }
}
