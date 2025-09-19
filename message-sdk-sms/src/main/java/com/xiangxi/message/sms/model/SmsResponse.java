package com.xiangxi.message.sms.model;

/**
 * 短信发送响应结果
 * <p>
 * 使用Java 14+的record语法定义的不可变数据类，表示短信发送操作的结果。
 * 该类包含了短信发送的核心状态信息，便于上层应用判断发送结果和进行后续处理。
 * </p>
 * 
 * <p>
 * 使用示例：
 * </p>
 * <pre>{@code
 * // 成功响应
 * SmsResponse success = SmsResponse.success("msg_12345");
 * 
 * // 失败响应
 * SmsResponse failure = SmsResponse.failure("Invalid phone number");
 * 
 * // 检查发送结果
 * if (response.success()) {
 *     System.out.println("短信发送成功，消息ID: " + response.messageId());
 * } else {
 *     System.err.println("短信发送失败: " + response.error());
 * }
 * }</pre>
 * 
 * @param success 发送是否成功，true表示成功，false表示失败
 * @param messageId 消息唯一标识符，成功时由第三方服务返回，失败时可能为null
 * @param error 错误信息，发送失败时包含具体的错误描述，成功时为null
 * 
 * @author 初心
 * @since 1.0.0
 * @see com.xiangxi.message.sms.ISmsSender
 */
public record SmsResponse(
    /**
     * 发送是否成功
     * <p>
     * true表示短信已成功提交到第三方服务商并获得确认，
     * false表示发送过程中出现错误（如参数验证失败、网络异常、服务商拒绝等）。
     * </p>
     */
    boolean success,
    
    /**
     * 消息唯一标识符
     * <p>
     * 当发送成功时，第三方服务商返回的消息ID，可用于后续的状态查询、回执匹配等操作。
     * 当发送失败时，此字段通常为null。
     * </p>
     */
    String messageId,
    
    /**
     * 错误信息
     * <p>
     * 当发送失败时，包含具体的错误描述信息，帮助开发者定位问题。
     * 当发送成功时，此字段为null。
     * 错误信息应当尽可能详细和友好，便于问题排查。
     * </p>
     */
    String error
) {
    /**
     * 成功工厂方法
     */
    public static SmsResponse success(String messageId) {
        return new SmsResponse(true, messageId, null);
    }

    /**
     * 失败工厂方法
     */
    public static SmsResponse failure(String error) {
        return new SmsResponse(false, null, error);
    }

    /**
     * 约束校验：
     * - 成功时不应包含错误信息
     * - 失败时必须包含错误信息
     */
    public SmsResponse {
        if (success && error != null) {
            throw new IllegalArgumentException("success=true 时 error 必须为 null");
        }
        if (!success && (error == null || error.isBlank())) {
            throw new IllegalArgumentException("success=false 时必须提供错误信息");
        }
    }
}
