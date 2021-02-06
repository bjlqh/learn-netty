package org.example.aio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AioClient {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        AsynchronousSocketChannel asyncSocketChannel = AsynchronousSocketChannel.open();
        asyncSocketChannel.connect(new InetSocketAddress("127.0.0.1", 9000)).get();
        asyncSocketChannel.write(ByteBuffer.wrap("HelloServer".getBytes()));
        ByteBuffer buffer = ByteBuffer.allocate(512);
        Future<Integer> future = asyncSocketChannel.read(buffer);
        int count = 1;
        while (!future.isDone()) {
            Thread.sleep(1000);
            System.out.println(count++);
        }
        Integer len = future.get();
        if (len != 1) {
            System.out.println("客户端收到信息：" + new String(buffer.array(), 0, len));
        }
    }
}
