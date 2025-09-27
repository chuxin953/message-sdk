package com.xiangxi.message.push.model;

import com.xiangxi.message.common.annotation.Required;
import com.xiangxi.message.common.util.MessageValidator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 推送消息发送请求
 * 
 * <p>封装推送消息发送的请求参数。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class PushRequest {
    
    /**
     * 设备Token或用户ID
     */
    @Required(message = "设备Token或用户ID不能为空")
    private final String recipient;
    
    /**
     * 消息标题
     */
    @Required(message = "消息标题不能为空")
    private final String title;
    
    /**
     * 消息内容
     */
    @Required(message = "消息内容不能为空")
    private final String content;
    
    /**
     * 消息类型
     */
    private final PushType pushType;
    
    /**
     * 目标平台
     */
    private final List<Platform> platforms;
    
    /**
     * 扩展参数
     */
    private final Map<String, Object> properties;
    
    /**
     * 推送类型枚举
     */
    public enum PushType {
        NOTIFICATION("NOTIFICATION", "通知消息"),
        MESSAGE("MESSAGE", "透传消息"),
        SILENT("SILENT", "静默消息");
        
        private final String code;
        private final String description;
        
        PushType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 目标平台枚举
     */
    public enum Platform {
        ANDROID("ANDROID", "Android平台"),
        IOS("IOS", "iOS平台"),
        WEB("WEB", "Web平台");
        
        private final String code;
        private final String description;
        
        Platform(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 构造函数
     */
    private PushRequest(Builder builder) {
        this.recipient = builder.recipient;
        this.title = builder.title;
        this.content = builder.content;
        this.pushType = builder.pushType;
        this.platforms = builder.platforms;
        this.properties = builder.properties;
    }
    
    /**
     * 创建通知消息请求
     * 
     * @param recipient 设备Token或用户ID
     * @param title 标题
     * @param content 内容
     * @return 推送请求
     */
    public static PushRequest notification(String recipient, String title, String content) {
        return new Builder()
                .recipient(recipient)
                .title(title)
                .content(content)
                .pushType(PushType.NOTIFICATION)
                .build();
    }
    
    /**
     * 创建透传消息请求
     * 
     * @param recipient 设备Token或用户ID
     * @param title 标题
     * @param content 内容
     * @return 推送请求
     */
    public static PushRequest message(String recipient, String title, String content) {
        return new Builder()
                .recipient(recipient)
                .title(title)
                .content(content)
                .pushType(PushType.MESSAGE)
                .build();
    }
    
    /**
     * 创建静默消息请求
     * 
     * @param recipient 设备Token或用户ID
     * @param content 内容
     * @return 推送请求
     */
    public static PushRequest silent(String recipient, String content) {
        return new Builder()
                .recipient(recipient)
                .title("")
                .content(content)
                .pushType(PushType.SILENT)
                .build();
    }
    
    // Getters
    public String getRecipient() {
        return recipient;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getContent() {
        return content;
    }
    
    public PushType getPushType() {
        return pushType;
    }
    
    public List<Platform> getPlatforms() {
        return platforms;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * 获取扩展参数值
     * 
     * @param key 参数键
     * @return 参数值
     */
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }
    
    /**
     * 获取字符串类型的扩展参数
     * 
     * @param key 参数键
     * @return 参数值
     */
    public String getStringProperty(String key) {
        Object value = getProperty(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 验证请求参数
     * 
     * @throws IllegalArgumentException 如果参数无效
     */
    public void validate() {
        MessageValidator.validateNotEmpty(recipient, "设备Token或用户ID");
        MessageValidator.validateNotEmpty(title, "消息标题");
        MessageValidator.validateNotEmpty(content, "消息内容");
        
        if (!MessageValidator.isValidDeviceToken(recipient)) {
            throw new IllegalArgumentException("设备Token格式不正确: " + recipient);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PushRequest that = (PushRequest) o;
        return Objects.equals(recipient, that.recipient) &&
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                pushType == that.pushType &&
                Objects.equals(platforms, that.platforms) &&
                Objects.equals(properties, that.properties);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(recipient, title, content, pushType, platforms, properties);
    }
    
    @Override
    public String toString() {
        return String.format("PushRequest{recipient='%s', title='%s', pushType=%s}", 
                recipient, title, pushType);
    }
    
    /**
     * 构建器
     */
    public static class Builder {
        private String recipient;
        private String title;
        private String content;
        private PushType pushType = PushType.NOTIFICATION;
        private List<Platform> platforms;
        private Map<String, Object> properties;
        
        public Builder recipient(String recipient) {
            this.recipient = recipient;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder content(String content) {
            this.content = content;
            return this;
        }
        
        public Builder pushType(PushType pushType) {
            this.pushType = pushType;
            return this;
        }
        
        public Builder platforms(List<Platform> platforms) {
            this.platforms = platforms;
            return this;
        }
        
        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }
        
        public PushRequest build() {
            return new PushRequest(this);
        }
    }
}
