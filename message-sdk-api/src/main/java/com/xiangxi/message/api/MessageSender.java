package com.xiangxi.message.api;

import com.xiangxi.message.common.exception.MessageSendException;

/**
 * 泛型接口：
 *  C - 配置类型（渠道特定）
 *  M - 请求类型（如 SmsRequest）
 *  R - 响应类型（如 SmsResponse）
 */
public interface MessageSender<C , M, R> {
    String type();
    String channel();
    R send(C config, M message) throws MessageSendException;
}


