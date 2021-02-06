package org.example.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel asyncServerSocketChannel =
                AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(9000));
        asyncServerSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel asyncSocketChannel, Object attachment) {
                try {
                    System.out.println("2--" + Thread.currentThread().getName());
                    asyncServerSocketChannel.accept(attachment, this);
                    SocketAddress address = asyncSocketChannel.getRemoteAddress();
                    System.out.println(address);
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    asyncSocketChannel.read(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer buffer) {
                            System.out.println("3--" + Thread.currentThread().getName());
                            //
                            buffer.flip();
                            System.out.println(new java.lang.String(buffer.array(), 0, result));
                            asyncSocketChannel.write(ByteBuffer.wrap(("HelloClient:" + address).getBytes()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            exc.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        System.out.println("1--" + Thread.currentThread().getName());
        Thread.sleep(Integer.MAX_VALUE);
    }
}
