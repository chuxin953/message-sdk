package com.xiangxi.message.log;

/**
 * 发送失败事件：当消息发送出现异常（业务异常或运行时异常）时发布。
 *
 * @param source   事件源（通常为具体的 MessageSender 实例）
 * @param type     消息类型，例如 sms/email/push
 * @param channel  渠道，例如 tencent/ali
 * @param message  原始消息体
 * @param error    异常信息（包含错误码等上下文时更易定位问题）
 * @param timestamp 事件产生时间（毫秒）
 * @param traceId  链路追踪ID（如果存在）
 * @param costMs   发送耗时（毫秒）
 */
public record MessageSendFailedEvent(
        Object source,
        String type,
        String channel,
        Object message,
        Throwable error,
        long timestamp,
        String traceId,
        long costMs
) {}