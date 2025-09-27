package com.xiangxi.message.sms.model;

import com.xiangxi.message.common.model.MessageResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 短信发送响应
 * 
 * <p>封装短信发送的结果信息。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class SmsResponse extends MessageResult {
    
    /**
     * 手机号列表
     */
    private final List<String> phoneNumbers;

    /**
     * 模板ID
     */
    private final String templateId;

    /**
     * 构造函数
     */
    private SmsResponse(Builder builder) {
        super(
                builder.getSuccessValue(),
                builder.getMessageIdValue(),
                builder.getErrorMessageValue(),
                builder.getErrorCodeValue(),
                builder.getSendTimeValue(),
                builder.getResponseTimeValue(),
                builder.getChannelValue(),
                builder.getMessageTypeValue(),
                builder.getPropertiesValue()
        );
        this.phoneNumbers = builder.phoneNumbers;
        this.templateId = builder.templateId;
    }
    
    /**
     * 创建成功响应
     * 
     * @param messageId 消息ID
     * @param phoneNumbers 手机号列表
     * @param templateId 模板ID
     * @param templateParams 模板参数
     * @param channel 渠道标识
     * @param responseTime 响应时间
     * @return 成功响应
     */
    public static SmsResponse success(String messageId, List<String> phoneNumbers, String templateId, 
                                    Map<String, String> templateParams, String channel, long responseTime) {
        return new Builder()
                .success(true)
                .messageId(messageId)
                .phoneNumbers(phoneNumbers)
                .templateId(templateId)
                .channel(channel)
                .messageType("SMS")
                .responseTime(responseTime)
                .sendTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 创建失败响应
     * 
     * @param errorMessage 错误信息
     * @param errorCode 错误代码
     * @param phoneNumbers 手机号列表
     * @param templateId 模板ID
     * @param channel 渠道标识
     * @param responseTime 响应时间
     * @return 失败响应
     */
    public static SmsResponse failure(String errorMessage, String errorCode, List<String> phoneNumbers, 
                                    String templateId, String channel, long responseTime) {
        return new Builder()
                .success(false)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .phoneNumbers(phoneNumbers)
                .templateId(templateId)
                .channel(channel)
                .messageType("SMS")
                .responseTime(responseTime)
                .sendTime(LocalDateTime.now())
                .build();
    }
    
    /**
     * 获取手机号列表
     * 
     * @return 手机号列表（如果为null则返回空列表）
     */
    public List<String> getPhoneNumbers() {
        return phoneNumbers != null ? phoneNumbers : new ArrayList<>();
    }
    
    /**
     * 获取第一个手机号（兼容单个接收人的场景）
     * 
     * @return 第一个手机号
     */
    public String getPhoneNumber() {
        return phoneNumbers != null && !phoneNumbers.isEmpty() ? phoneNumbers.get(0) : null;
    }
    
    /**
     * 获取接收人数量
     * 
     * @return 接收人数量
     */
    public int getRecipientCount() {
        return phoneNumbers != null ? phoneNumbers.size() : 0;
    }
    
    /**
     * 获取模板ID
     * 
     * @return 模板ID
     */
    public String getTemplateId() {
        return templateId;
    }

    

    
    /**
     * 检查是否有手机号
     * 
     * @return 如果有手机号返回true
     */
    public boolean hasPhoneNumbers() {
        return phoneNumbers != null && !phoneNumbers.isEmpty();
    }

    
    /**
     * 检查是否包含指定手机号
     * 
     * @param phoneNumber 手机号
     * @return 如果包含返回true
     */
    public boolean containsPhoneNumber(String phoneNumber) {
        return phoneNumbers != null && phoneNumbers.contains(phoneNumber);
    }
    
    @Override
    public String toString() {
        return String.format("SmsResponse{success=%s, messageId='%s', phoneNumbers=%s, templateId='%s', channel='%s', responseTime=%dms}", 
                isSuccess(), getMessageId(), phoneNumbers, templateId, getChannel(), getResponseTime());
    }
    
    /**
     * 构建器
     */
    public static class Builder extends MessageResult.Builder {
        private List<String> phoneNumbers = new ArrayList<>();
        private String templateId;

        // 对父类受保护字段提供公开桥接访问器，供外部类构造函数调用
        public boolean getSuccessValue() { return super.getSuccess(); }
        public String getMessageIdValue() { return super.getMessageId(); }
        public String getErrorMessageValue() { return super.getErrorMessage(); }
        public String getErrorCodeValue() { return super.getErrorCode(); }
        public LocalDateTime getSendTimeValue() { return super.getSendTime(); }
        public long getResponseTimeValue() { return super.getResponseTime(); }
        public String getChannelValue() { return super.getChannel(); }
        public String getMessageTypeValue() { return super.getMessageType(); }
        public Map<String, Object> getPropertiesValue() { return super.getProperties(); }

        public Builder addPhoneNumber(String phoneNumber) {
            this.phoneNumbers.add(phoneNumber);
            return this;
        }

        public Builder phoneNumbers(List<String> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        @Override
        public Builder success(boolean success) {
            super.success(success);
            return this;
        }

        @Override
        public Builder messageId(String messageId) {
            super.messageId(messageId);
            return this;
        }

        // 重写所有父类方法以返回SmsResponse.Builder类型
        @Override
        public Builder errorMessage(String errorMessage) {
            super.errorMessage(errorMessage);
            return this;
        }

        @Override
        public Builder errorCode(String errorCode) {
            super.errorCode(errorCode);
            return this;
        }

        @Override
        public Builder sendTime(LocalDateTime sendTime) {
            super.sendTime(sendTime);
            return this;
        }

        @Override
        public Builder responseTime(long responseTime) {
            super.responseTime(responseTime);
            return this;
        }

        @Override
        public Builder channel(String channel) {
            super.channel(channel);
            return this;
        }

        @Override
        public Builder messageType(String messageType) {
            super.messageType(messageType);
            return this;
        }

        @Override
        public Builder properties(Map<String, Object> properties) {
            super.properties(properties);
            return this;
        }

        @Override
        public SmsResponse build() {
            return new SmsResponse(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SmsResponse that = (SmsResponse) o;
        return Objects.equals(phoneNumbers, that.phoneNumbers) &&
                Objects.equals(templateId, that.templateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), phoneNumbers, templateId);
    }

}
