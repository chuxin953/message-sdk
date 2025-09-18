package com.xiangxi.message.sms.model;

/**
 * @author 初心
 * Create by on 2025/9/17 15:13 10
 */
public record SmsResponse(boolean success, String messageId, String error) {

}
