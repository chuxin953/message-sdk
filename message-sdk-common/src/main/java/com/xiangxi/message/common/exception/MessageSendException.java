package com.xiangxi.message.common.exception;

import java.io.Serial;

/**
 * 消息发送异常。
 *
 * <p>当消息发送过程中出现错误时抛出此异常。这是一个受检异常，
 * 调用方必须显式处理或声明抛出。</p>
 *
 * <p>错误上下文约定：</p>
 * <ul>
 *   <li>errorCode：统一错误码，建议使用"供应商域前缀 + 语义"，如 TENCENT_SDK_ERROR / VALIDATION_ERROR / UNEXPECTED_ERROR；</li>
 *   <li>messageType：消息类型（如 SMS/EMAIL/PUSH），便于聚合统计；</li>
 *   <li>channel：渠道编码（如 TENCENT_SMS/ALI_SMS），便于快速定位实现与配置；</li>
 * </ul>
 *
 * <p>构造器使用建议：</p>
 * <ol>
 *   <li>优先使用包含 errorCode、messageType、channel 的重载，便于统一日志与告警聚合；</li>
 *   <li>仅当无法提供上下文时，使用基础构造器；</li>
 *   <li>toString() 与 getFullErrorMessage() 会拼装上下文，便于排查。</li>
 * </ol>
 * 
 * <p>异常场景包括但不限于：</p>
 * <ul>
 *   <li>网络连接失败</li>
 *   <li>第三方服务API调用失败</li>
 *   <li>认证信息错误</li>
 *   <li>配置参数无效</li>
 *   <li>消息格式不正确</li>
 *   <li>发送频率限制</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * try {
 *     SmsResponse response = messageSender.send(config, request);
 * } catch (MessageSendException e) {
 *     log.error("消息发送失败: {}", e.getMessage(), e);
 *     // 处理异常，如重试、降级等
 * }
 * }</pre>
 * 
 * @author 初心
 * @version 1.0.0
 * @since 1.0.0
 */
public class MessageSendException extends Exception {
    
    @Serial
    private static final long serialVersionUID = -5313382795958085062L;
    
    /**
     * 错误代码（可选）
     */
    private final String errorCode;
    
    /**
     * 消息类型
     */
    private final String messageType;
    
    /**
     * 渠道名称
     */
    private final String channel;
    
    /**
     * 构造一个消息发送异常
     * 
     * @param message 异常消息
     */
    public MessageSendException(String message) {
        super(message);
        this.errorCode = null;
        this.messageType = null;
        this.channel = null;
    }
    
    /**
     * 构造一个消息发送异常
     * 
     * @param message 异常消息
     * @param cause 原始异常
     */
    public MessageSendException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.messageType = null;
        this.channel = null;
    }
    
    /**
     * 构造一个带有错误代码的消息发送异常
     * 
     * @param message 异常消息
     * @param errorCode 错误代码
     */
    public MessageSendException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.messageType = null;
        this.channel = null;
    }
    
    /**
     * 构造一个带有完整上下文信息的消息发送异常
     * 
     * @param message 异常消息
     * @param errorCode 错误代码
     * @param messageType 消息类型
     * @param channel 渠道名称
     */
    public MessageSendException(String message, String errorCode, String messageType, String channel) {
        super(message);
        this.errorCode = errorCode;
        this.messageType = messageType;
        this.channel = channel;
    }
    
    /**
     * 构造一个带有完整上下文信息的消息发送异常
     * 
     * @param message 异常消息
     * @param cause 原始异常
     * @param errorCode 错误代码
     * @param messageType 消息类型
     * @param channel 渠道名称
     */
    public MessageSendException(String message, Throwable cause, String errorCode, String messageType, String channel) {
        super(message, cause);
        this.errorCode = errorCode;
        this.messageType = messageType;
        this.channel = channel;
    }
    
    /**
     * 获取错误代码
     * 
     * @return 错误代码，可能为null
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取消息类型
     * 
     * @return 消息类型，可能为null
     */
    public String getMessageType() {
        return messageType;
    }
    
    /**
     * 获取渠道名称
     * 
     * @return 渠道名称，可能为null
     */
    public String getChannel() {
        return channel;
    }
    
    /**
     * 检查是否有错误代码
     * 
     * @return 如果有错误代码返回true，否则返回false
     */
    public boolean hasErrorCode() {
        return errorCode != null && !errorCode.trim().isEmpty();
    }
    
    /**
     * 获取完整的错误信息
     * 
     * @return 包含上下文信息的完整错误描述
     */
    public String getFullErrorMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage());
        
        if (hasErrorCode()) {
            sb.append(" [错误代码: ").append(errorCode).append("]");
        }
        
        if (messageType != null || channel != null) {
            sb.append(" [上下文: ");
            if (messageType != null) {
                sb.append("类型=").append(messageType);
            }
            if (channel != null) {
                if (messageType != null) {
                    sb.append(", ");
                }
                sb.append("渠道=").append(channel);
            }
            sb.append("]");
        }
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getFullErrorMessage();
    }
}
