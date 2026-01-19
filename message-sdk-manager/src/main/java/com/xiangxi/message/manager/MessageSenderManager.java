package com.xiangxi.message.manager;


import com.xiangxi.message.api.MessageSender;
import com.xiangxi.message.common.exception.MessageSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

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

    private static final Logger log = LoggerFactory.getLogger(MessageSenderManager.class);

    /**
     * 初始化标志，使用 volatile 确保多线程环境下的可见性
     */
    private static volatile boolean initialized = false;

    /**
     * 惰性初始化：通过 SPI 加载所有 {@link MessageSender} 实现并缓存。
     * 注意：此方法应在 synchronized 块中调用，由 {@link #ensureInitialized()} 负责同步。
     */
    private static void init(){
        ServiceLoader<MessageSender> loader = ServiceLoader.load(MessageSender.class);
        loader.forEach(sender -> {
            // routeKey 约定：type:channel，例如 sms:tencent
            String key = sender.routeKey();
            if (senderMap.containsKey(key)) {
                throw new IllegalStateException("Duplicate MessageSender for type: " + key);
            }
            // 包装日志，泛型安全
            senderMap.put(key, sender);
            if (log.isDebugEnabled()) {
                log.debug("Loaded MessageSender: {}", key);
            }
        });
        if (senderMap.isEmpty()) {
            log.warn("No MessageSender implementations found via SPI");
        } else {
            log.info("Initialized {} MessageSender(s) via SPI", senderMap.size());
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
        ensureInitialized();
        if (type == null || type.isBlank() || channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("type/channel must not be blank");
        }
        String key = type + ":" + channel;
        MessageSender<?, ?, ?> sender = senderMap.get(key);
        if (sender == null) {
            throw new IllegalArgumentException("No MessageSender found for type: " + type + ", channel: " + channel);
        }
        return (MessageSender<C, M, R>) sender;
    }
    
    /**
     * 确保已初始化，使用双重检查锁定模式优化性能
     */
    private static void ensureInitialized() {
        if (!initialized) {
            synchronized (MessageSenderManager.class) {
                if (!initialized) {
                    init();
                }
            }
        }
    }

    /**
     * 获取所有已加载的 MessageSender（key 形如 type:channel）。
     *
     * @return senderMap 的只读视图或引用（请勿在外部修改）。
     */
    public static Map<String, MessageSender<?, ?, ?>> getAllSenders() {
        ensureInitialized();
        return Collections.unmodifiableMap(senderMap);
    }

    /**
     * 发送消息的便捷方法：根据 type 与 channel 路由到具体实现完成发送。
     *
     * @param type    消息类型
     * @param channel 渠道
     * @param config  发送配置
     * @param message 发送消息体
     * @param <C>     配置类型
     * @param <M>     消息体类型
     * @param <R>     返回类型
     * @return 发送结果
     * @throws com.xiangxi.message.common.exception.MessageSendException 发送失败时由具体实现抛出
     */
    public static <C, M, R> R send(String type, String channel, C config, M message) throws MessageSendException {
        MessageSender<C, M, R> sender = getSender(type, channel);
        Objects.requireNonNull(config, "config must not be null");
        Objects.requireNonNull(message, "message must not be null");
        // 简单的调试日志
        log.debug("Sending message: type={}, channel={}", type, channel);
        try {
            R result = sender.send(config, message);
            log.debug("Message sent successfully: type={}, channel={}", type, channel);
            return result;
        } catch (MessageSendException e) {
            log.warn("Message send failed: type={}, channel={}, error={}", type, channel, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.warn("Message send failed with runtime exception: type={}, channel={}, error={}", type, channel, e.getMessage());
            throw e;
        }
    }

}

