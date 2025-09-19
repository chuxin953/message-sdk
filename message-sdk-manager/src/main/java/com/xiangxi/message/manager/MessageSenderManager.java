package com.xiangxi.message.manager;


import com.xiangxi.message.api.MessageSender;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一入口
 * @author 初心
 * Create by on 2025/9/18 09:24 44
 */
public class MessageSenderManager {

    // 按 type 缓存 SPI 实现
    private static final Map<String, MessageSender<?,?,?>> senderMap = new ConcurrentHashMap<>();

    private static boolean initialized = false;

    private static synchronized void init(){
        if (initialized) return;

        ServiceLoader<MessageSender> loader = ServiceLoader.load(MessageSender.class);
        loader.forEach(sender -> {
            String key = sender.type() + ":" + sender.channel();
            if (senderMap.containsKey(key)) {
                throw new IllegalStateException("Duplicate MessageSender for type: " + key);
            }
            // 包装日志，泛型安全
            senderMap.put(key, new LoggingSenderDecorator<>(sender));
        });
        if (senderMap.isEmpty()) {
            System.out.println("Warning: No MessageSender implementations found via SPI");
        }
        initialized = true;
    }

    /**
     * 获取指定类型与渠道的 Sender
     *
     * @param type    消息类型，例如 sms / email
     * @param channel 渠道，例如 tencent / ali
     * @param <C>     配置类型
     * @param <M>     消息类型
     * @param <R>     返回类型
     * @return MessageSender 实例
     */
    @SuppressWarnings("unchecked")
    public static <C, M, R> MessageSender<C, M, R> getSender(String type, String channel) {
        if (!initialized) init();
        String key = type + ":" + channel;
        MessageSender<?, ?, ?> sender = senderMap.get(key);
        if (sender == null) {
            throw new IllegalArgumentException("No MessageSender found for type: " + type + ", channel: " + channel);
        }
        return (MessageSender<C, M, R>) sender;
    }

    /**
     * 获取所有已加载的 MessageSender
     */
    public static Map<String, MessageSender<?, ?, ?>> getAllSenders() {
        if (!initialized) init();
        return senderMap;
    }

}
