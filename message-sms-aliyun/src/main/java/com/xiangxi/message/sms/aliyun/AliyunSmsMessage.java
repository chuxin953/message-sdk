package com.xiangxi.message.sms.aliyun;

/** 阿里云短信消息 */
public class AliyunSmsMessage {
    private final String templateCode;
    private final String[] phoneNumbers;
    private final String templateParamJson; // {"code":"123456"}

    private AliyunSmsMessage(Builder b) {
        this.templateCode = b.templateCode;
        this.phoneNumbers = b.phoneNumbers;
        this.templateParamJson = b.templateParamJson;
    }

    public String getTemplateCode() { return templateCode; }
    public String[] getPhoneNumbers() { return phoneNumbers; }
    public String getTemplateParamJson() { return templateParamJson; }

    public static class Builder {
        private String templateCode; private String[] phoneNumbers; private String templateParamJson;
        public Builder templateCode(String v){ this.templateCode=v; return this; }
        public Builder phoneNumbers(String[] v){ this.phoneNumbers=v; return this; }
        public Builder templateParamJson(String v){ this.templateParamJson=v; return this; }
        public AliyunSmsMessage build(){ return new AliyunSmsMessage(this); }
    }
}


