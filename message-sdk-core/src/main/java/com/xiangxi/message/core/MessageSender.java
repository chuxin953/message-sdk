package com.xiangxi.message.core;

import com.xiangxi.message.common.exception.MessageSendException;

/**
 * 泛型接口：
 *  C - 配置类型（渠道特定）
 *  M - 请求类型（如 SmsRequest）
 *  R - 响应类型（如 SmsResponse）
 *
 *
 * @author 初心
 * Create by on 2025/9/16 14:43 24
*/
public interface MessageSender<C , M, R> {
    String type(); // 返回消息类型，例如 sms, email
    String channel(); // 返回渠道，例如 tencent, ali
    R send(C config, M message) throws MessageSendException;
}
