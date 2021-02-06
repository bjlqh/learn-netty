package org.example.netty.splitpackage.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyMessageEncoder extends MessageToByteEncoder<MyMessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MyMessageProtocol msg, ByteBuf out) throws Exception {
        System.out.println("MyMessageEncoder encode 方法被调用");
        out.writeInt(msg.getLen());      //不管int值为多少，写入DirectByteBuf里，都占4个字节
        out.writeBytes(msg.getContent());//写入具体内容的字节数，比如：一个中文占3个字节（UTF-8编码中）
    }
}
