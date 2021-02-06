package org.example.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class NettyByteBuf {

    public static void main(String[] args) {

        //test1();
        //test2();
        test3();
    }

    private static void test3() {
        String content = "hello,netty";
        ByteBuf byteBuf = Unpooled.buffer(10);
        int length = content.length();
        byteBuf.writeByte(length);
        byteBuf.writeBytes(content.getBytes());
        System.out.println("写完以后:" + "byteBuf = " + byteBuf);
        //查看 并不是读取
        for (int i = 0; i < length; i++) {
            System.out.println(byteBuf.getByte(i));
        }
        System.out.println("查看以后:" + "byteBuf = " + byteBuf);
        //往外读
        for (int i = 0; i < length; i++) {
            System.out.println(i + ":" + byteBuf.readByte());
            System.out.println("剩余可读:" + byteBuf.readableBytes());

        }
        System.out.println("读完以后:" + "byteBuf = " + byteBuf);
    }

    private static void test2() {
        //用 Unpooled工具创建 ByteBuf
        ByteBuf buf = Unpooled.copiedBuffer("hello,jack", CharsetUtil.UTF_8);
        //使用相关方法
        if (buf.hasArray()) {
            byte[] bytes = buf.array();
            String content = new String(bytes, CharsetUtil.UTF_8);
            System.out.println(content);
            System.out.println("byteBuf = " + buf);

            System.out.println("ridx: " + buf.readerIndex());
            System.out.println("widx: " + buf.writerIndex());
            System.out.println("cap " + buf.capacity());

            byte b = buf.getByte(0);
            System.out.println(b + " in ASCII is " + new String(new byte[]{b}));

            int len = buf.readableBytes();
            System.out.println("可读的字节数：" + len);

            System.out.println("取出各个字节数：");
            for (int i = 0; i < len; i++) {
                System.out.print((char) buf.getByte(i) + " ");
            }
            System.out.println();

            //范围读取
            System.out.println(buf.getCharSequence(0, 5, CharsetUtil.UTF_8));
            System.out.println(buf.getCharSequence(3, 5, CharsetUtil.UTF_8));
            System.out.println(buf.getCharSequence(6, 10, CharsetUtil.UTF_8));
        }
    }

    private static void test1() {
        //创建 byteBuf对象，该对象内部包含一个字节数组byte[10]
        //通过 readerIndex 和 writeIndex 和 capacity,将 buffer分成三个区域
        //已读区域：[0,ridx)
        //可读区域：[ridx,widx)
        //可写区域：[widx,cap)
        ByteBuf byteBuf = Unpooled.buffer(10);
        System.out.println("byteBuf = " + byteBuf);

        //往里写
        for (int i = 0; i < 8; i++) {
            byteBuf.writeByte(i);
        }
        System.out.println("写完以后:" + "byteBuf = " + byteBuf);
        //查看 并不是读取
        for (int i = 0; i < 5; i++) {
            System.out.println(byteBuf.getByte(i));
        }
        System.out.println("查看以后:" + "byteBuf = " + byteBuf);
        //往外读
        for (int i = 0; i < 5; i++) {
            System.out.println(byteBuf.readByte());
        }
        System.out.println("读完以后:" + "byteBuf = " + byteBuf);
    }
}
