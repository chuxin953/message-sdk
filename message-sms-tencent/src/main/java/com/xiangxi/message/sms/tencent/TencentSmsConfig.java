package com.xiangxi.message.sms.tencent;

import com.xiangxi.message.common.validation.Required;

/**
 * 腾讯短信配置
 * @author 初心
 * Create by on 2025/9/16 15:38 34
 */
public class TencentSmsConfig{

    public static final String SERVICE = "sms";


    @Required(message = "secretId不能为空", fieldName = "secretId")
    private final String secretId;
    
    @Required(message = "secretKey不能为空", fieldName = "secretKey")
    private final String secretKey;
    
    @Required(message = "SDK AppId不能为空", fieldName = "SDKAppId")
    private final String sdkAppId;
    
    @Required(message = "地域不能为空", fieldName = "地域")
    private final String region;


    @Required(message = "signName不能为空", fieldName = "签名信息")
    private final String signName;

    private TencentSmsConfig(Builder builder) {
        this.secretId = builder.secretId;
        this.secretKey = builder.secretKey;
        this.sdkAppId = builder.sdkAppId;
        this.region = builder.region;
        this.signName = builder.signName;
    }

    public String getSecretId() { return secretId; }
    public String getSecretKey() { return secretKey; }
    public String getSdkAppId() { return sdkAppId; }
    public String getRegion() { return region; }
    public String getSignName() { return signName;}
    public static class Builder {
        private String secretId;
        private String secretKey;
        private String sdkAppId;
        private String region;
        private String signName;

        public Builder secretId(String secretId) {
            this.secretId = secretId;
            return this;
        }

        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder sdkAppId(String sdkAppId) {
            this.sdkAppId = sdkAppId;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder signName(String signName) {
            this.signName = signName;
            return this;
        }

        public TencentSmsConfig build() {
            return new TencentSmsConfig(this);
        }
    }
}
