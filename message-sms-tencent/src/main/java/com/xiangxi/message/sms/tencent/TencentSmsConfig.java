package com.xiangxi.message.sms.tencent;

import com.xiangxi.message.sms.config.SmsConfig;
import lombok.Getter;

/**
 * @author 初心
 * Create by on 2025/9/16 15:38 34
 */
@Getter
public class TencentSmsConfig extends SmsConfig {
    public TencentSmsConfig(String sign, String appId, String appKey, String sdkAppId, String region, String templateId) {
        super(sign);
        this.appId = appId;
        this.appKey = appKey;
        this.sdkAppId = sdkAppId;
        this.region = region;
        this.templateId = templateId;
    }

    private final String appId;
    private final String appKey;
    private final String sdkAppId;
    private final String region;
    private final String templateId;
}
