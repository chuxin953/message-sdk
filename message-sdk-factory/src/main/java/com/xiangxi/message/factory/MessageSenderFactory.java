package com.xiangxi.message.factory;


import com.xiangxi.message.core.MessageSender;
import com.xiangxi.message.core.SenderFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author 初心
 * Create by on 2025/9/18 09:24 44
 */
public class MessageSenderFactory {
    private static final Map<String, SenderFactory> factoryCache = new HashMap<>();

    private static boolean initialized = false;

    private static synchronized void init(){
        if (initialized) return;

        ServiceLoader<SenderFactory> loader = ServiceLoader.load(SenderFactory.class);
        for (SenderFactory f : loader) {
            factoryCache.put(f.type(), f);
            System.out.println("Loaded Factory: " + f.type() + " -> " + f.getClass().getName());
        }
        initialized = true;
    }

    /**
     * 获取指定类型与渠道的 Sender
     *
     * @param type    消息类型，例如 sms / email
     * @param channel 渠道，例如 tencent / ali
     * @param <C>     配置类型
     * @param <M>     请求类型
     * @param <R>     响应类型
     * @return MessageSender 实例
     */
    @SuppressWarnings("unchecked")
    public static <C, M, R> MessageSender<C, M, R> getSender(String type, String channel) {
        if (!initialized) init();
        SenderFactory factory = factoryCache.get(type);
        if (factory == null) throw new IllegalArgumentException("No factory for type " + type);
        return (MessageSender<C, M, R>) factory.getSender(channel);
    }

}
