package com.xiangxi.message.manager;

import com.alibaba.fastjson.JSON;
import com.xiangxi.message.api.MessageSender;
import com.xiangxi.message.common.exception.MessageSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 初心
 * Create by on 2025/9/19 11:39 52
 */
public class LoggingSenderDecorator <C, M, R> implements MessageSender<C, M, R> {
    private static final Logger log = LoggerFactory.getLogger(LoggingSenderDecorator.class);

    private final MessageSender<C, M, R> delegate;

    public LoggingSenderDecorator(MessageSender<C, M, R> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String type() {
        return delegate.type();
    }

    @Override
    public String channel() {
        return delegate.channel();
    }

    @Override
    public R send(C config, M message) throws MessageSendException {
        try {
            log.debug("[{}] Sending {} message via {}: {}", type(), type(), channel(), JSON.toJSON(message));
            R result = delegate.send(config, message);
            log.debug("[{}] Result from {} message via {}: {}",type(), type(), channel(), JSON.toJSON(message));
            return result;
        } catch (Exception e) {
            log.error("[{}] Error sending {} message via {}: {}", type(), type(), channel(), e.getMessage(), e);
            throw e;
        }
    }
}
