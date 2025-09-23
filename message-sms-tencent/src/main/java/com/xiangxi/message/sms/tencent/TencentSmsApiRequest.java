package com.xiangxi.message.sms.tencent;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 腾讯云短信 API 请求参数对象
 *
 * <p>对应腾讯云 SMS {@code SendSms} 接口的请求体字段。
 * 文档参考：https://cloud.tencent.com/document/product/382/55981</p>
 *
 * <p>核心必填字段：</p>
 * <ul>
 *   <li>{@link #smsSdkAppId} 短信应用 ID</li>
 *   <li>{@link #templateId} 模板 ID</li>
 *   <li>{@link #phoneNumberSet} 接收方号码（E.164 格式）</li>
 *   <li>{@link #signName} 短信签名</li>
 *   <li>{@link #templateParamSet} 模板参数</li>
 * </ul>
 *
 *
 * @author 初心
 * @since 2025-09-21
 */
public class TencentSmsApiRequest {
    /** 短信 SdkAppId（在控制台获取） */
    @SerializedName("SmsSdkAppId")
    private String smsSdkAppId;

    /** 短信模板 ID */
    @SerializedName("TemplateId")
    private String templateId;

    /** 接收方号码列表，需符合 E.164 标准格式，例如 +8613711112222 */
    @SerializedName("PhoneNumberSet")
    private String[] phoneNumberSet;

    /** 短信签名 */
    @SerializedName("SignName")
    private String signName;

    /** 模板参数列表，顺序对应模板占位符 */
    @SerializedName("TemplateParamSet")
    private String[] templateParamSet;

    /** 扩展码，可选 */
    @SerializedName("ExtendCode")
    private String extendCode;

    /** 用户自定义上下文，回执会原样返回，可选 */
    @SerializedName("SessionContext")
    private String sessionContext;

    /** 国际/港澳台短信 SenderId，可选 */
    @SerializedName("SenderId")
    private String senderId;

    /** 无参构造器（用于 JSON 反序列化） */
    public TencentSmsApiRequest() {}

    private TencentSmsApiRequest(Builder builder) {
        this.smsSdkAppId = builder.smsSdkAppId;
        this.templateId = builder.templateId;
        this.phoneNumberSet = builder.phoneNumberSet;
        this.signName = builder.signName;
        this.templateParamSet = builder.templateParamSet;
        this.extendCode = builder.extendCode;
        this.sessionContext = builder.sessionContext;
        this.senderId = builder.senderId;
    }

    public String getSmsSdkAppId() { return smsSdkAppId; }
    public String getTemplateId() { return templateId; }
    public String[] getPhoneNumberSet() { return phoneNumberSet; }
    public String getSignName() { return signName; }
    public String[] getTemplateParamSet() { return templateParamSet; }
    public String getExtendCode() { return extendCode; }
    public String getSessionContext() { return sessionContext; }
    public String getSenderId() { return senderId; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private String smsSdkAppId, templateId, signName, extendCode, sessionContext, senderId;
        private String[] phoneNumberSet, templateParamSet;
        public Builder smsSdkAppId(String v) { this.smsSdkAppId = v; return this; }
        public Builder templateId(String v) { this.templateId = v; return this; }
        public Builder phoneNumberSet(String[] v) { this.phoneNumberSet = v; return this; }
        public Builder signName(String v) { this.signName = v; return this; }
        public Builder templateParamSet(String[] v) { this.templateParamSet = v; return this; }
        public Builder extendCode(String v) { this.extendCode = v; return this; }
        public Builder sessionContext(String v) { this.sessionContext = v; return this; }
        public Builder senderId(String v) { this.senderId = v; return this; }
        public TencentSmsApiRequest build() { return new TencentSmsApiRequest(this); }
    }

    @Override
    public String toString() {
        return "TencentSmsApiRequest{" +
                "smsSdkAppId='" + smsSdkAppId + '\'' +
                ", templateId='" + templateId + '\'' +
                ", phoneNumberSet=" + phoneNumberSet +
                ", signName='" + signName + '\'' +
                ", templateParamSet=" + templateParamSet +
                ", extendCode='" + extendCode + '\'' +
                ", sessionContext='" + sessionContext + '\'' +
                ", senderId='" + senderId + '\'' +
                '}';
    }
}
