package com.xiangxi.message.sms.tencent;

/**
 * 腾讯短信配置。
 *
 * 必填项：secretId/secretKey/sdkAppId/region/signName
 * 说明：用于腾讯短信签名鉴权、请求路由与短信签名展示。
 */
public class TencentSmsConfig{

    /**
     * 腾讯云服务名，用于计算鉴权签名。
     */
    public static final String SERVICE = "sms";


    /** 访问密钥 ID（必填） */
    private final String secretId;
    
    /** 访问密钥 Key（必填） */
    private final String secretKey;
    
    /** 短信 SDK AppId（必填） */
    private final String sdkAppId;
    
    /** 区域地域，如 ap-beijing（必填） */
    private final String region;


    /** 短信签名（必填），与控制台中的签名保持一致 */
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
