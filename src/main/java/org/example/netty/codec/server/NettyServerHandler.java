package org.example.netty.codec.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.example.netty.codec.config.pojo.User;
import org.example.netty.codec.config.pojo.proto.ProtostuffUtil;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端读取到数据：" + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //ctx.writeAndFlush("你好,client");
        //ctx.writeAndFlush(new User(12580,"jack"));
        ctx.writeAndFlush(Unpooled.copiedBuffer(ProtostuffUtil.serializer(new User(12580, "jack"))));
    }

    /**
     * 捕捉异常
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
