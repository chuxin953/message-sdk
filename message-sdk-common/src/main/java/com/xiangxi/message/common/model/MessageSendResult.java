package com.xiangxi.message.common.model;

import java.time.LocalDateTime;

/**
 * @author 初心
 * Create by on 2025/10/16 11:27 21
 */
public class MessageSendResult {
    /** 接收人（手机号 / 邮箱 / openId） */
    private String receiver;

    /** 渠道返回的消息ID */
    private String messageId;

    /** 是否发送成功 */
    private boolean success;

    /** 渠道商返回的错误码 */
    private String errorCode;

    /** 渠道商返回的错误描述 */
    private String errorMsg;

    /** 消息发送时间 */
    private LocalDateTime sendTime;

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    public LocalDateTime getSendTime() { return sendTime; }
    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }
}
