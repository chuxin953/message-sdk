package com.xiangxi.message.events;

import java.time.Instant;
import java.util.Objects;

/**
 * 消息发送失败事件
 *
 * <p>当消息发送出现异常（业务异常或运行时异常）时发布此事件。
 * 包含详细的错误信息，便于问题定位和监控告警。</p>
 *
 * @param source 事件源（通常为具体的 MessageSender 实例）
 * @param type 消息类型，例如 "sms"、"email"、"push"
 * @param channel 发送渠道，例如 "tencent"、"ali"、"huawei"
 * @param message 原始消息体
 * @param error 异常信息（包含错误码等上下文时更易定位问题）
 * @param timestamp 事件产生时间戳（毫秒）
 * @param traceId 链路追踪ID（用于分布式追踪）
 * @param costMs 发送耗时（毫秒）
 *
 * @author 初心
 * @since 1.0.0
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
) {

    /**
     * 构造函数，提供参数验证
     */
    public MessageSendFailedEvent {
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(channel, "channel must not be null");
        Objects.requireNonNull(message, "message must not be null");
        Objects.requireNonNull(error, "error must not be null");

        if (timestamp <= 0) {
            throw new IllegalArgumentException("timestamp must be positive");
        }
        if (costMs < 0) {
            throw new IllegalArgumentException("costMs must not be negative");
        }
    }

    /**
     * 获取事件产生时间
     *
     * @return Instant 对象
     */
    public Instant getEventTime() {
        return Instant.ofEpochMilli(timestamp);
    }

    /**
     * 检查是否有链路追踪ID
     *
     * @return 如果有traceId返回true
     */
    public boolean hasTraceId() {
        return traceId != null && !traceId.trim().isEmpty();
    }

    /**
     * 获取格式化的时间戳字符串
     *
     * @return 格式化的时间戳
     */
    public String getFormattedTimestamp() {
        return getEventTime().toString();
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息字符串
     */
    public String getErrorMessage() {
        return error.getMessage();
    }

    /**
     * 获取错误类型
     *
     * @return 错误类型的简单类名
     */
    public String getErrorType() {
        return error.getClass().getSimpleName();
    }
}