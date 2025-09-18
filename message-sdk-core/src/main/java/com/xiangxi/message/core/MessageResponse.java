package com.xiangxi.message.core;

/**
 * @author 初心
 * Create by on 2025/9/16 14:43 41
 */

public class MessageResponse {
    private boolean success;
    private String messageId;
    private String errorMessage;

    public MessageResponse() {}

    public MessageResponse(boolean success, String messageId, String errorMessage) {
        this.success = success;
        this.messageId = messageId;
        this.errorMessage = errorMessage;
    }

    public static MessageResponse success(String messageId) {
        return new MessageResponse(true, messageId, null);
    }

    public static MessageResponse failure(String errorMessage) {
        return new MessageResponse(false, null, errorMessage);
    }

    // ----------- 手动 getter/setter -----------

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "success=" + success +
                ", messageId='" + messageId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

}
