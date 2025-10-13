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
}
