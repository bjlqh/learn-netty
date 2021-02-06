package org.example.netty.chat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class ChatServerInboundHandler extends ChannelInboundHandlerAdapter {

    //GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户加入聊天室的信息推送给其它在线的客户端
        //该方法会将channelGroup中所有的channel遍历，并发送消息
        String date = sdf.format(new Date());
        channelGroup.writeAndFlush(date + " [ 客户端 ]" + channel.remoteAddress() + " 上线了");
        //将当前channel加入到channelGroup中
        //这里add的原因：不用给自己推送自己上线了。
        channelGroup.add(channel);
        System.out.println(ctx.channel().remoteAddress() + " 上线了");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将某客户端离开的信息推送给当前在线的所有其他客户端
        String date = sdf.format(new Date());
        channelGroup.writeAndFlush(date + " [ 客户端 ]" + channel.remoteAddress() + " 下线了");
        System.out.println(channel.remoteAddress() + " 下线了");
        System.out.println("ChannelGroup size=" + channelGroup.size());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //获取当前发送数据客户端channel
        Channel channel = ctx.channel();
        //这时遍历 ChannelGroup，根据不同情况，发送不同的消息
        String date = sdf.format(new Date());
        Iterator<Channel> iterator = channelGroup.iterator();
        while (iterator.hasNext()) {
            Channel ch = iterator.next();
            if (channel != ch) {
                //不是当前的channel就转发消息
                ch.writeAndFlush(date + " [ 客户端 ]" + channel.remoteAddress() + " 发送了消息: " + msg);
            } else {
                //是当前的消息就回显自己的消息
                ch.writeAndFlush("[ 自己 ]" + " 发送了消息: " + msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
