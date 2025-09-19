package com.xiangxi.message.manager;


import com.xiangxi.message.api.MessageSender;
import com.xiangxi.message.common.exception.MessageSendException;
import com.xiangxi.message.log.MessageEventPublisher;
import com.xiangxi.message.log.MessageSentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一入口
 * @author 初心
 * Create by on 2025/9/18 09:24 44
 */
/**
 * 消息发送统一调度入口。
 * <p>
 * 通过 Java SPI 动态发现并注册各消息通道的 {@link com.xiangxi.message.api.MessageSender} 实现，
 * 并按 "type:channel" 进行路由（例如：sms:tencent、email:xxx）。
 * </p>
 * <p>
 * 线程安全说明：
 * <ul>
 *   <li>初始化采用惰性加载（lazy-init），并通过 synchronized 确保只执行一次；</li>
 *   <li>已加载的实现缓存于 ConcurrentHashMap，支持并发访问；</li>
 *   <li>使用前无需手动初始化，首次调用时会自动完成 SPI 加载。</li>
 * </ul>
 * </p>
 * <p>
 * 使用方式：
 * <ul>
 *   <li>{@link #getSender(String, String)} 根据 type 与 channel 获取具体实现；</li>
 *   <li>{@link #send(String, String, Object, Object)} 直接发送消息并发布发送事件。</li>
 * </ul>
 * </p>
 */
public class MessageSenderManager {

    // 按 type 缓存 SPI 实现
    /**
     * 按 "type:channel" 缓存已加载的 MessageSender 实现（SPI 加载），并发安全。
     * key 示例：sms:tencent、email:demo。
     */
    private static final Map<String, MessageSender<?,?,?>> senderMap = new ConcurrentHashMap<>();

    private static final MessageEventPublisher event = new MessageEventPublisher();
    private static final Logger log = LoggerFactory.getLogger(MessageSenderManager.class);


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
            senderMap.put(key, sender);
        });
        if (senderMap.isEmpty()) {
            log.warn("No MessageSender implementations found via SPI");
        }
        initialized = true;
    }

    /**
     * 获取指定类型与渠道的 Sender。
     *
     * @param type    消息类型，例如 sms / email
     * @param channel 渠道，例如 tencent / ali
     * @param <C>     配置类型
     * @param <M>     消息类型
     * @param <R>     返回类型
     * @return MessageSender 实例
     * @throws IllegalArgumentException 当指定 type/channel 未找到对应实现时抛出
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
     * 获取所有已加载的 MessageSender（key 形如 type:channel）。
     *
     * @return senderMap 的只读视图或引用（请勿在外部修改）。
     */
    public static Map<String, MessageSender<?, ?, ?>> getAllSenders() {
        if (!initialized) init();
        return senderMap;
    }

    /**
     * 发送消息的便捷方法：根据 type 与 channel 路由到具体实现完成发送，并发布发送成功事件。
     *
     * @param type    消息类型
     * @param channel 渠道
     * @param config  发送配置
     * @param message 发送消息体
     * @param <C>     配置类型
     * @param <M>     消息体类型
     * @param <R>     返回类型
     * @return 发送结果
     * @throws MessageSendException 发送失败时由具体实现抛出
     */
    public static <C, M, R> R send(String type, String channel, C config, M message) throws MessageSendException {
        MessageSender<C, M, R> sender = getSender(type, channel);
        R result = sender.send(config, message);
        event.publish(new MessageSentEvent(sender, sender.type(), sender.channel(), message, result));
        return result;
    }
}
