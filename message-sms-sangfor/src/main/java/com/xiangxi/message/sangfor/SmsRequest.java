package com.xiangxi.message.sangfor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 初心
 * Create by on 2025/9/17 14:48 02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsRequest {
    // 请求地址
    private  String apiUrl;
    // 账号
    private String account;
    // 密码
    private String password;
    // 消息内容
    private String msg;
    // 手机号
    private String phone;
    // 是否需要回执状态
    private String  report;
}
