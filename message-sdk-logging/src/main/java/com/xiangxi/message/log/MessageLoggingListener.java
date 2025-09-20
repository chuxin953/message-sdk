package com.xiangxi.message.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 初心
 * Create by on 2025/9/19 12:51 31
 */
public class MessageLoggingListener implements EventListener<Object> {
    private static final Logger log = LoggerFactory.getLogger(MessageLoggingListener.class);

    // 是否打印日志开关
    private final boolean enable;

    // 默认构造器，enable=false
    public MessageLoggingListener() {
        this(false);
    }

    public MessageLoggingListener(boolean enable) {
        this.enable = enable;
    }


    @Override
    public void onEvent(Object event) {
        if (!enable) return;

        if (event instanceof MessageSentEvent e) {
            log.debug("Message sent | type={} | channel={} | ts={} | traceId={} | costMs={} | message={} | result={}",
                    e.type(), e.channel(), e.timestamp(), e.traceId(), e.costMs(), e.message(), e.result());
        } else if (event instanceof MessageSendFailedEvent e) {
            log.debug("Message send failed | type={} | channel={} | ts={} | traceId={} | costMs={} | message={} | error={}",
                    e.type(), e.channel(), e.timestamp(), e.traceId(), e.costMs(), e.message(), e.error().toString());
        }
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
        return enable;
    }
}
