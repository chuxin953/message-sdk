package com.xiangxi.message.sms.config;


/**
 * @author 初心
 * Create by on 2025/9/16 14:51 00
 */

public abstract class SmsConfig  {
    private final String sign;
    protected SmsConfig(String sign) { this.sign = sign; }

    public String getSign() { return this.sign; }
}
