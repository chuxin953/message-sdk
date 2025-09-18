package com.xiangxi.message.common.exception;

import java.io.Serial;

/**
 * @author 初心
 * Create by on 2025/9/16 14:59 05
 */
public class MessageSendException extends Exception {
    @Serial
    private static final long serialVersionUID = -5313382795958085062L;

    public MessageSendException(String message) { super(message); }
    public MessageSendException(String message, Throwable cause) { super(message, cause); }
}
