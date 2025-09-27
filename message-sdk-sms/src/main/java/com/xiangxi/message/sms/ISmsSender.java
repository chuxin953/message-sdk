package com.xiangxi.message.sms;

import com.xiangxi.message.api.MessageSender;
import com.xiangxi.message.sms.model.SmsRequest;
import com.xiangxi.message.sms.model.SmsResponse;

/**
 * 短信发送器接口。
 *
 * 语义：所有短信渠道实现均应实现此接口，统一被上层通过 {@link #type()} 与 {@link #channel()} 路由。
 *
 * 约定：
 * - {@link #type()} 必须返回统一的短信类型标识（例如："SMS"）。
 * - {@link #channel()} 返回渠道编码（例如："TENCENT_SMS"、"ALI_SMS"）。
 * - {@link #send(Object, Object)} 抛出的异常应转换为统一的业务异常，由上层捕获并记录。
 *
 * @param <C> 配置类型（含密钥/地域/签名等渠道初始化所需信息）
 */
public interface ISmsSender<C> extends MessageSender<C, SmsRequest, SmsResponse> {
    // 继承父接口的所有方法，无需额外定义
}