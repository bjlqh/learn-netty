package org.example.netty.splitpackage.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyMessageDecoder extends ByteToMessageDecoder {

    //内容的字节数
    int length = 0;

    /**
     * 每次 writeAndFlush的时候都会调用
     * 解码 需要将二进制字节码 转换成 MyMessageProtocol数据包对象
     * 一定是先发长度，再发内容
     *
     * @param out
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println();
        System.out.println("MyMessageDecoder decode 被调用");

        System.out.println(in);
        // 4个字节的长度
        if (in.readableBytes() >= 4) {
            if (length == 0) {
                length = in.readInt();
            }
            if (in.readableBytes() < length) {
                System.out.println("当前可读数据不够，继续等待下次数据一起发送过来...");
                return;
            }
            byte[] content = new byte[length];
            if (in.readableBytes() >= length) {
                in.readBytes(content);
                //封装成MyMessageProtocol对象，传递到下一个handler业务处理
                MyMessageProtocol protocol = new MyMessageProtocol();
                protocol.setLen(length);
                protocol.setContent(content);
                out.add(protocol);
            }
        }
        length = 0;
    }
}
