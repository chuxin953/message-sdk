package com.xiangxi.message.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Message SDK 配置属性
 * 
 * @author 初心
 */
@ConfigurationProperties(prefix = "message.sdk")
public class MessageSdkProperties {

    /**
     * 是否启用 Message SDK
     */
    private boolean enabled = true;

    /**
     * 默认消息类型
     */
    private String defaultType = "sms";

    /**
     * 默认渠道
     */
    private String defaultChannel = "tencent";

    /**
     * 是否启用事件发布
     */
    private boolean eventsEnabled = true;

    /**
     * 是否启用日志记录
     */
    private boolean loggingEnabled = true;

    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public String getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(String defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public boolean isEventsEnabled() {
        return eventsEnabled;
    }

    public void setEventsEnabled(boolean eventsEnabled) {
        this.eventsEnabled = eventsEnabled;
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }
}
