package com.xiangxi.message.events;

import java.util.Map;

/**
 * 消息发送重试事件
 * 
 * <p>当消息发送失败并开始重试时触发此事件。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class MessageSendRetryEvent extends MessageEvent {
    
    /**
     * 重试次数
     */
    private final int retryCount;
    
    /**
     * 错误信息
     */
    private final String errorMessage;
    
    /**
     * 构造函数
     * 
     * @param eventId 事件ID
     * @param messageType 消息类型
     * @param channel 渠道标识
     * @param traceId 追踪ID
     * @param retryCount 重试次数
     * @param errorMessage 错误信息
     * @param properties 扩展属性
     */
    public MessageSendRetryEvent(String eventId, String messageType, String channel, String traceId,
                                int retryCount, String errorMessage, Map<String, Object> properties) {
        super(eventId, messageType, channel, traceId, properties);
        this.retryCount = retryCount;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 获取重试次数
     * 
     * @return 重试次数
     */
    public int getRetryCount() {
        return retryCount;
    }
    
    /**
     * 获取错误信息
     * 
     * @return 错误信息
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    @Override
    public String toString() {
        return String.format("MessageSendRetryEvent{eventId='%s', messageType='%s', channel='%s', traceId='%s', retryCount=%d, errorMessage='%s'}", 
                getEventId(), getMessageType(), getChannel(), getTraceId(), retryCount, errorMessage);
    }
}
