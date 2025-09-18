package com.xiangxi.message.sms;


import com.xiangxi.message.api.MessageSender;
import com.xiangxi.message.sms.model.SmsResponse;

/**
 * @author 初心
 * Create by on 2025/9/16 14:50 31
 */
public interface ISmsSender<C, M> extends MessageSender<C, M, SmsResponse> {
}