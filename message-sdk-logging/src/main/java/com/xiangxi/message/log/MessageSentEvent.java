package com.xiangxi.message.log;

/**
 * @author 初心
 * Create by on 2025/9/19 12:47 26
 */
public record MessageSentEvent(
        Object source,
        String type,
        String channel,
        Object message,
        Object result,
        long timestamp,
        String traceId,
        long costMs
) {}
