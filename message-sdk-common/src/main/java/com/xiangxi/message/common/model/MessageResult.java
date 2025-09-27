package com.xiangxi.message.common.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 统一消息发送结果
 * 
 * <p>封装消息发送的结果信息，包括成功状态、消息ID、错误信息等。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class MessageResult {
    
    /**
     * 是否发送成功
     */
    private final boolean success;
    
    /**
     * 消息ID（由第三方服务返回）
     */
    private final String messageId;
    
    /**
     * 错误信息（发送失败时）
     */
    private final String errorMessage;
    
    /**
     * 错误代码（发送失败时）
     */
    private final String errorCode;
    
    /**
     * 发送时间
     */
    private final LocalDateTime sendTime;
    
    /**
     * 响应时间（毫秒）
     */
    private final long responseTime;
    
    /**
     * 渠道标识
     */
    private final String channel;
    
    /**
     * 消息类型
     */
    private final String messageType;
    
    /**
     * 扩展属性
     */
    private final Map<String, Object> properties;
    
    /**
     * 构造函数
     */
    private MessageResult(Builder builder) {
        this.success = builder.success;
        this.messageId = builder.messageId;
        this.errorMessage = builder.errorMessage;
        this.errorCode = builder.errorCode;
        this.sendTime = builder.sendTime;
        this.responseTime = builder.responseTime;
        this.channel = builder.channel;
        this.messageType = builder.messageType;
        this.properties = builder.properties;
    }
    
    /**
     * 受保护的构造函数，供子类使用
     */
    protected MessageResult(boolean success, String messageId, String errorMessage, String errorCode,
                           LocalDateTime sendTime, long responseTime, String channel, String messageType,
                           Map<String, Object> properties) {
        this.success = success;
        this.messageId = messageId;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.sendTime = sendTime;
        this.responseTime = responseTime;
        this.channel = channel;
        this.messageType = messageType;
        this.properties = properties;
    }
    
    /**
     * 创建成功结果
     * 
     * @param messageId 消息ID
     * @param channel 渠道标识
     * @param messageType 消息类型
     * @param responseTime 响应时间
     * @return 成功结果
     */
    public static MessageResult success(String messageId, String channel, String messageType, long responseTime) {
        return new Builder()
                .success(true)
                .messageId(messageId)
                .channel(channel)
                .messageType(messageType)
                .responseTime(responseTime)
                .sendTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建失败结果
     * 
     * @param errorMessage 错误信息
     * @param errorCode 错误代码
     * @param channel 渠道标识
     * @param messageType 消息类型
     * @param responseTime 响应时间
     * @return 失败结果
     */
    public static MessageResult failure(String errorMessage, String errorCode, String channel, String messageType, long responseTime) {
        return new Builder()
                .success(false)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .channel(channel)
                .messageType(messageType)
                .responseTime(responseTime)
                .sendTime(LocalDateTime.now())
                .build();
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public LocalDateTime getSendTime() {
        return sendTime;
    }
    
    public long getResponseTime() {
        return responseTime;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * 获取扩展属性
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
        MessageResult that = (MessageResult) o;
        return success == that.success &&
                responseTime == that.responseTime &&
                Objects.equals(messageId, that.messageId) &&
                Objects.equals(errorMessage, that.errorMessage) &&
                Objects.equals(errorCode, that.errorCode) &&
                Objects.equals(sendTime, that.sendTime) &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(messageType, that.messageType) &&
                Objects.equals(properties, that.properties);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(success, messageId, errorMessage, errorCode, sendTime, responseTime, channel, messageType, properties);
    }
    
    @Override
    public String toString() {
        return String.format("MessageResult{success=%s, messageId='%s', errorMessage='%s', errorCode='%s', channel='%s', messageType='%s', responseTime=%dms}",
                success, messageId, errorMessage, errorCode, channel, messageType, responseTime);
    }
    
    /**
     * 构建器
     */
    public static class Builder {
        private boolean success;
        private String messageId;
        private String errorMessage;
        private String errorCode;
        private LocalDateTime sendTime;
        private long responseTime;
        private String channel;
        private String messageType;
        private Map<String, Object> properties;
        
        // 受保护的访问器，供子类使用
        protected boolean getSuccess() { return success; }
        protected String getMessageId() { return messageId; }
        protected String getErrorMessage() { return errorMessage; }
        protected String getErrorCode() { return errorCode; }
        protected LocalDateTime getSendTime() { return sendTime; }
        protected long getResponseTime() { return responseTime; }
        protected String getChannel() { return channel; }
        protected String getMessageType() { return messageType; }
        protected Map<String, Object> getProperties() { return properties; }
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }
        
        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }
        
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder sendTime(LocalDateTime sendTime) {
            this.sendTime = sendTime;
            return this;
        }
        
        public Builder responseTime(long responseTime) {
            this.responseTime = responseTime;
            return this;
        }
        
        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }
        
        public Builder messageType(String messageType) {
            this.messageType = messageType;
            return this;
        }
        
        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }
        
        public MessageResult build() {
            return new MessageResult(this);
        }
    }
}
