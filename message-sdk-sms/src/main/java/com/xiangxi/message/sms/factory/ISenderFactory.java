package com.xiangxi.message.sms.factory;


import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.core.MessageSender;
import com.xiangxi.message.core.SenderFactory;
import com.xiangxi.message.sms.ISmsSender;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * MessageSenderFactory - 基于 SPI 的工厂
 * 支持泛型和多类型渠道自动加载
 */
public class ISenderFactory implements SenderFactory {

    private static final Map<String, ISmsSender<?, ?>> senders = new HashMap<>();
    private static boolean initialized = false;

    private static synchronized void init() {
        if (initialized) return;

        @SuppressWarnings("rawtypes")
        ServiceLoader<ISmsSender> loader = ServiceLoader.load(ISmsSender.class);
        for (ISmsSender<?, ?> sender : loader) {
            senders.put(sender.channel(), sender);
            System.out.println("Loaded SMS sender: " + sender.type() + ":" + sender.channel()
                    + " -> " + sender.getClass().getName());
        }
        initialized = true;
    }
    // 获取具体 ISmsSender
    @SuppressWarnings("unchecked")
    public static  <C, M> ISmsSender<C, M> getSmsSender(String channel) {
        if (!initialized) init();
        return (ISmsSender<C, M>) senders.get(channel);
    }

    @Override
    public String type() {
        return MessageType.SMS.name();
    }

    @Override
    public MessageSender<?, ?, ?> getSender(String channel) {
        return getSmsSender(channel);
    }
}