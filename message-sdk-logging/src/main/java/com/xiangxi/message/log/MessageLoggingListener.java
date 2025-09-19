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
            log.info("Message sent | type={} | channel={} | message={} | result={}",
                    e.type(),
                    e.channel(),
                    e.message(),
                    e.result());
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
