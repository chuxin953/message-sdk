package com.xiangxi.message.events;

import java.util.Map;
import java.util.UUID;

/**
 * 消息事件工厂
 * 
 * <p>提供消息事件的创建功能，支持各种类型的事件创建。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class MessageEventFactory {
    
    /**
     * 创建消息发送成功事件
     * 
     * @param source 事件源
     * @param messageType 消息类型
     * @param channel 渠道标识
     * @param message 原始消息体
     * @param result 发送结果
     * @param traceId 追踪ID
     * @param responseTime 响应时间
     * @return 消息发送成功事件
     */
    public static MessageSentEvent createSentEvent(Object source, String messageType, String channel, 
                                                  Object message, Object result, String traceId, long responseTime) {
        long timestamp = System.currentTimeMillis();
        return new MessageSentEvent(source, messageType, channel, message, result, timestamp, traceId, responseTime);
    }
    
    /**
     * 创建消息发送失败事件
     * 
     * @param source 事件源
     * @param messageType 消息类型
     * @param channel 渠道标识
     * @param message 原始消息体
     * @param error 异常信息
     * @param traceId 追踪ID
     * @param responseTime 响应时间
     * @return 消息发送失败事件
     */
    public static MessageSendFailedEvent createFailedEvent(Object source, String messageType, String channel,
                                                          Object message, Throwable error, String traceId, long responseTime) {
        long timestamp = System.currentTimeMillis();
        return new MessageSendFailedEvent(source, messageType, channel, message, error, timestamp, traceId, responseTime);
    }
    
    /**
     * 创建消息发送开始事件
     * 
     * @param messageType 消息类型
     * @param channel 渠道标识
     * @param traceId 追踪ID
     * @param properties 扩展属性
     * @return 消息发送开始事件
     */
    public static MessageSendStartedEvent createStartedEvent(String messageType, String channel, String traceId,
                                                            Map<String, Object> properties) {
        String eventId = generateEventId();
        return new MessageSendStartedEvent(eventId, messageType, channel, traceId, properties);
    }
    
    /**
     * 创建消息发送重试事件
     * 
     * @param messageType 消息类型
     * @param channel 渠道标识
     * @param traceId 追踪ID
     * @param retryCount 重试次数
     * @param errorMessage 错误信息
     * @param properties 扩展属性
     * @return 消息发送重试事件
     */
    public static MessageSendRetryEvent createRetryEvent(String messageType, String channel, String traceId,
                                                        int retryCount, String errorMessage, Map<String, Object> properties) {
        String eventId = generateEventId();
        return new MessageSendRetryEvent(eventId, messageType, channel, traceId, retryCount, errorMessage, properties);
    }
    
    /**
     * 生成事件ID
     * 
     * @return 事件ID
     */
    private static String generateEventId() {
        return "event_" + UUID.randomUUID().toString().replace("-", "");
    }
}
