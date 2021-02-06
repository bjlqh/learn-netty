package org.example.netty.heartbeat.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 自定义 Handler 需要继承 netty规定好的某个 HandlerAdapter
 */
public class HeartBeatServerHandler extends SimpleChannelInboundHandler<String> {


    /**
     * 有读取事件触发的方法
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("=======> [server] receive message: " + msg);
        if ("HeartBeat Package".equals(msg)) {
            ctx.channel().writeAndFlush("ok");
        } else {
            System.out.println(msg);
        }
    }

    private int readIdleTimes = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;
        String eventType = null;
        switch (event.state()) {
            case READER_IDLE: {
                eventType = "读空闲";
                readIdleTimes++;  //读空闲计数 +1
                break;
            }
            case WRITER_IDLE: {
                eventType = "写空闲";
                //不处理
                break;
            }
            case ALL_IDLE: {
                eventType = "读写空闲";
                //不处理
                break;
            }
            default:
                break;
        }

        System.out.println(ctx.channel().remoteAddress() + "超时事件:" + eventType);
        if (readIdleTimes > 3) {
            System.out.println("[server] 读空闲超过3次，关闭连接，释放更多资源");
            ctx.channel().writeAndFlush("idle close");
            ctx.channel().close();
        }
    }

    /**
     * 客户端连接成功调用的方法
     *
     * @param ctx 上下文对象，含有通道的 channel,管道 pipeline
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("HelloClient: " + ctx.channel().remoteAddress());
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
