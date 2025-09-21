package com.xiangxi.message.sms.tencent;

import com.xiangxi.message.common.validation.Required;
import com.xiangxi.message.sms.config.SmsConfig;

/**
 * 腾讯短信配置
 * @author 初心
 * Create by on 2025/9/16 15:38 34
 */
public class TencentSmsConfig extends SmsConfig {
    public TencentSmsConfig(String sign, String appId, String appKey, String sdkAppId, String region, String templateId) {
        super(sign);
        this.appId = appId;
        this.appKey = appKey;
        this.sdkAppId = sdkAppId;
        this.region = region;
        this.templateId = templateId;
    }

    @Required(message = "AppId不能为空", fieldName = "AppId")
    private final String appId;
    
    @Required(message = "AppKey不能为空", fieldName = "AppKey")
    private final String appKey;
    
    @Required(message = "SDK AppId不能为空", fieldName = "SDK AppId")
    private final String sdkAppId;
    
    @Required(message = "地域不能为空", fieldName = "地域")
    private final String region;
    
    @Required(message = "模板ID不能为空", fieldName = "模板ID")
    private final String templateId;


    public String getAppId() {
        return appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getSdkAppId() {
        return sdkAppId;
    }

    public String getRegion() {
        return region;
    }

    public String getTemplateId() {
        return templateId;
    }
}
