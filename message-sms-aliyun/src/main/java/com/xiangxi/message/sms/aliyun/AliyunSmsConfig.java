package com.xiangxi.message.sms.aliyun;

/** 阿里云短信配置 */
public class AliyunSmsConfig {
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String signName;
    private final String regionId;

    private AliyunSmsConfig(Builder b) {
        this.accessKeyId = b.accessKeyId;
        this.accessKeySecret = b.accessKeySecret;
        this.signName = b.signName;
        this.regionId = b.regionId;
    }

    public String getAccessKeyId() { return accessKeyId; }
    public String getAccessKeySecret() { return accessKeySecret; }
    public String getSignName() { return signName; }
    public String getRegionId() { return regionId; }

    public static class Builder {
        private String accessKeyId, accessKeySecret, signName, regionId;
        public Builder accessKeyId(String v){ this.accessKeyId=v; return this; }
        public Builder accessKeySecret(String v){ this.accessKeySecret=v; return this; }
        public Builder signName(String v){ this.signName=v; return this; }
        public Builder regionId(String v){ this.regionId=v; return this; }
        public AliyunSmsConfig build(){ return new AliyunSmsConfig(this); }
    }
}


