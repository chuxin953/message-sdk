package com.xiangxi.message.events;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 初心
 * Create by on 2025/9/19 12:51 31
 */
public class MessageLoggingListener implements EventListener<Object> {
    private static final Logger log = LoggerFactory.getLogger(MessageLoggingListener.class);

    // 是否打印日志开关
    private final boolean enabled;

    // 默认构造器，enable=false
    public MessageLoggingListener() {
        this(false);
    }

    public MessageLoggingListener(boolean enable) {
        this.enabled = enable;
    }


    @Override
    public void onEvent(Object event) {
        if (!enabled) {
            return;
        }

        try {
            if (event instanceof MessageSentEvent sentEvent) {
                logMessageSent(sentEvent);
            } else if (event instanceof MessageSendFailedEvent failedEvent) {
                logMessageSendFailed(failedEvent);
            } else {
                log.debug("Unknown event type received: {}", event.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.warn("Failed to process event {}: {}", event.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * 记录消息发送成功日志
     *
     * @param event 发送成功事件
     */
    private void logMessageSent(MessageSentEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("Message sent | type={} | channel={} | ts={} | traceId={} | costMs={} | message={} | result={}",
                    event.type(),
                    event.channel(),
                    event.getFormattedTimestamp(),
                    event.hasTraceId() ? event.traceId() : "N/A",
                    event.costMs(),
                    formatMessage(event.message()),
                    formatResult(event.result()));
        }
    }

    /**
     * 记录消息发送失败日志
     *
     * @param event 发送失败事件
     */
    private void logMessageSendFailed(MessageSendFailedEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("Message send failed | type={} | channel={} | ts={} | traceId={} | costMs={} | message={} | error={}",
                    event.type(),
                    event.channel(),
                    event.getFormattedTimestamp(),
                    event.hasTraceId() ? event.traceId() : "N/A",
                    event.costMs(),
                    formatMessage(event.message()),
                    formatError(event.error()));
        }
    }

    /**
     * 格式化消息对象
     *
     * @param message 消息对象
     * @return 格式化后的字符串
     */
    private String formatMessage(Object message) {
        if (message == null) {
            return "null";
        }

        // 如果消息太长，截断显示
        String messageStr = message.toString();
        if (messageStr.length() > 200) {
            return messageStr.substring(0, 200) + "...";
        }
        return messageStr;
    }

    /**
     * 格式化错误对象
     *
     * @param error 错误对象
     * @return 格式化后的字符串
     */
    private String formatError(Throwable error) {
        if (error == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(error.getClass().getSimpleName());
        if (error.getMessage() != null) {
            sb.append(": ").append(error.getMessage());
        }
        return sb.toString();
    }

    /**
     * 格式化结果对象
     *
     * @param result 结果对象
     * @return 格式化后的字符串
     */
    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        return result.toString();
    }


    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return MessageLoggingListener.class.hashCode();
    }

    public boolean isEnable() {
        return enabled;
    }
}
