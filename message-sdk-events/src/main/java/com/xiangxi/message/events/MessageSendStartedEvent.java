package com.xiangxi.message.events;

import java.util.Map;

/**
 * 消息发送开始事件
 * 
 * <p>当消息开始发送时触发此事件。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class MessageSendStartedEvent extends MessageEvent {
    
    /**
     * 构造函数
     * 
     * @param eventId 事件ID
     * @param messageType 消息类型
     * @param channel 渠道标识
     * @param traceId 追踪ID
     * @param properties 扩展属性
     */
    public MessageSendStartedEvent(String eventId, String messageType, String channel, String traceId, Map<String, Object> properties) {
        super(eventId, messageType, channel, traceId, properties);
    }
    
    @Override
    public String toString() {
        return String.format("MessageSendStartedEvent{eventId='%s', messageType='%s', channel='%s', traceId='%s'}", 
                getEventId(), getMessageType(), getChannel(), getTraceId());
    }
}
