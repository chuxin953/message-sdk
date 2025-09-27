package com.xiangxi.message.events;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 消息事件基类
 * 
 * <p>所有消息相关事件的基类，提供通用的事件属性。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public abstract class MessageEvent {
    
    /**
     * 事件ID
     */
    private final String eventId;
    
    /**
     * 事件时间
     */
    private final LocalDateTime eventTime;
    
    /**
     * 消息类型
     */
    private final String messageType;
    
    /**
     * 渠道标识
     */
    private final String channel;
    
    /**
     * 追踪ID
     */
    private final String traceId;
    
    /**
     * 扩展属性
     */
    private final Map<String, Object> properties;
    
    /**
     * 构造函数
     * 
     * @param eventId 事件ID
     * @param messageType 消息类型
     * @param channel 渠道标识
     * @param traceId 追踪ID
     * @param properties 扩展属性
     */
    protected MessageEvent(String eventId, String messageType, String channel, String traceId, Map<String, Object> properties) {
        this.eventId = eventId;
        this.eventTime = LocalDateTime.now();
        this.messageType = messageType;
        this.channel = channel;
        this.traceId = traceId;
        this.properties = properties;
    }
    
    /**
     * 获取事件ID
     * 
     * @return 事件ID
     */
    public String getEventId() {
        return eventId;
    }
    
    /**
     * 获取事件时间
     * 
     * @return 事件时间
     */
    public LocalDateTime getEventTime() {
        return eventTime;
    }
    
    /**
     * 获取消息类型
     * 
     * @return 消息类型
     */
    public String getMessageType() {
        return messageType;
    }
    
    /**
     * 获取渠道标识
     * 
     * @return 渠道标识
     */
    public String getChannel() {
        return channel;
    }
    
    /**
     * 获取追踪ID
     * 
     * @return 追踪ID
     */
    public String getTraceId() {
        return traceId;
    }
    
    /**
     * 获取扩展属性
     * 
     * @return 扩展属性
     */
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * 获取扩展属性值
     * 
     * @param key 属性键
     * @return 属性值
     */
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }
    
    /**
     * 获取字符串类型的扩展属性
     * 
     * @param key 属性键
     * @return 属性值
     */
    public String getStringProperty(String key) {
        Object value = getProperty(key);
        return value != null ? value.toString() : null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageEvent that = (MessageEvent) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventTime, that.eventTime) &&
                Objects.equals(messageType, that.messageType) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(traceId, that.traceId) &&
                Objects.equals(properties, that.properties);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventTime, messageType, channel, traceId, properties);
    }
    
    @Override
    public String toString() {
        return String.format("%s{eventId='%s', eventTime=%s, messageType='%s', channel='%s', traceId='%s'}", 
                getClass().getSimpleName(), eventId, eventTime, messageType, channel, traceId);
    }
}
