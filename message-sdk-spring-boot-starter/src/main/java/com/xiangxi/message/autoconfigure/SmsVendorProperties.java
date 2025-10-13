package com.xiangxi.message.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 短信厂商配置属性
 * 
 * @author 初心
 */
@ConfigurationProperties(prefix = "message.sms")
public class SmsVendorProperties {

    /**
     * 腾讯云 SMS 配置
     */
    @NestedConfigurationProperty
    private TencentSmsProperties tencent = new TencentSmsProperties();

    /**
     * 阿里云 SMS 配置
     */
    @NestedConfigurationProperty
    private AliyunSmsProperties aliyun = new AliyunSmsProperties();

    /**
     * 默认厂商
     */
    private String defaultVendor = "tencent";

    // Getters and Setters
    public TencentSmsProperties getTencent() {
        return tencent;
    }

    public void setTencent(TencentSmsProperties tencent) {
        this.tencent = tencent;
    }

    public AliyunSmsProperties getAliyun() {
        return aliyun;
    }

    public void setAliyun(AliyunSmsProperties aliyun) {
        this.aliyun = aliyun;
    }

    public String getDefaultVendor() {
        return defaultVendor;
    }

    public void setDefaultVendor(String defaultVendor) {
        this.defaultVendor = defaultVendor;
    }

    /**
     * 腾讯云 SMS 配置
     */
    public static class TencentSmsProperties {
        private String secretId;
        private String secretKey;
        private String sdkAppId;
        private String region = "ap-beijing";
        private String signName;
        private boolean enabled = true;

        // Getters and Setters
        public String getSecretId() { return secretId; }
        public void setSecretId(String secretId) { this.secretId = secretId; }
        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
        public String getSdkAppId() { return sdkAppId; }
        public void setSdkAppId(String sdkAppId) { this.sdkAppId = sdkAppId; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public String getSignName() { return signName; }
        public void setSignName(String signName) { this.signName = signName; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    /**
     * 阿里云 SMS 配置
     */
    public static class AliyunSmsProperties {
        private String accessKeyId;
        private String accessKeySecret;
        private String signName;
        private String regionId = "cn-hangzhou";
        private boolean enabled = true;

        // Getters and Setters
        public String getAccessKeyId() { return accessKeyId; }
        public void setAccessKeyId(String accessKeyId) { this.accessKeyId = accessKeyId; }
        public String getAccessKeySecret() { return accessKeySecret; }
        public void setAccessKeySecret(String accessKeySecret) { this.accessKeySecret = accessKeySecret; }
        public String getSignName() { return signName; }
        public void setSignName(String signName) { this.signName = signName; }
        public String getRegionId() { return regionId; }
        public void setRegionId(String regionId) { this.regionId = regionId; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
}