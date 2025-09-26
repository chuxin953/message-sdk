package com.xiangxi.message.sms.aliyun;

import com.google.gson.annotations.SerializedName;

/** 阿里云短信 API 请求体（简化版，采用新签名接口参数） */
public class AliyunSmsApiRequest {
    @SerializedName("PhoneNumbers")
    private String phoneNumbers; // 逗号分隔
    @SerializedName("SignName")
    private String signName;
    @SerializedName("TemplateCode")
    private String templateCode;
    @SerializedName("TemplateParam")
    private String templateParam; // JSON字符串

    public String getPhoneNumbers() { return phoneNumbers; }
    public String getSignName() { return signName; }
    public String getTemplateCode() { return templateCode; }
    public String getTemplateParam() { return templateParam; }

    public static class Builder {
        private final AliyunSmsApiRequest r = new AliyunSmsApiRequest();
        public Builder phoneNumbers(String[] nums){ r.phoneNumbers = String.join(",", nums); return this; }
        public Builder signName(String v){ r.signName=v; return this; }
        public Builder templateCode(String v){ r.templateCode=v; return this; }
        public Builder templateParam(String json){ r.templateParam=json; return this; }
        public AliyunSmsApiRequest build(){ return r; }
    }
}


