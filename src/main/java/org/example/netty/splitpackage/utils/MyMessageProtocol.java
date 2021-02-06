package org.example.netty.splitpackage.utils;

import java.util.Arrays;

/**
 * 自定义协议包
 */
public class MyMessageProtocol {

    //定义一次发送包的长度
    private int len;

    //一次发送包体内容
    private byte[] content;

    public MyMessageProtocol() {
    }

    public MyMessageProtocol(int len, byte[] content) {
        this.len = len;
        this.content = content;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MyMessageProtocol{" +
                "len=" + len +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
