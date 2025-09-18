package com.xiangxi.message.core;

/**
 * @author 初心
 * Create by on 2025/9/18 10:12 04
 */
public interface SenderFactory {
    String type();
    MessageSender<?,?,?> getSender(String channel);
}