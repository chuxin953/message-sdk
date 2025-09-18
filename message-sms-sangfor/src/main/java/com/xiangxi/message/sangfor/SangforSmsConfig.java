package com.xiangxi.message.sangfor;

import com.xiangxi.message.sms.config.SmsConfig;
import lombok.Getter;

/**
 * @author 初心
 * Create by on 2025/9/17 14:47 55
 */
@Getter
public class SangforSmsConfig extends SmsConfig {
    // 请求地址
    private final String apiUrl;
    // 账号
    private final String account;
    // 密码
    private final String password;
    // 消息内容
    private final String msg;
    // 手机号
    private final String phone;
    // 是否需要回执状态
    private final String  report;

    public SangforSmsConfig(String sign, String apiUrl, String account, String password, String msg, String phone, String report) {
        super(sign);
        this.apiUrl = apiUrl;
        this.account = account;
        this.password = password;
        this.msg = msg;
        this.phone = phone;
        this.report = report;
    }
}
