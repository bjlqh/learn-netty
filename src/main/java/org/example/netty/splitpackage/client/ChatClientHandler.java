package org.example.netty.splitpackage.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.example.netty.splitpackage.utils.MyMessageProtocol;

public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg.trim());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("发送数据");
        for (int i = 0; i < 10; i++) {
            String msg = "你好，我是张三！";
            MyMessageProtocol protocol = new MyMessageProtocol();
            protocol.setLen(msg.getBytes(CharsetUtil.UTF_8).length); //内容有多少个字节
            protocol.setContent(msg.getBytes(CharsetUtil.UTF_8)); //内容转化为字节
            ctx.writeAndFlush(protocol);
        }
    }
}
