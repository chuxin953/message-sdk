package com.xiangxi.message.common.enums;

/**
 * 消息类型枚举
 * 
 * <p>定义了系统支持的所有消息发送类型，用于消息路由和发送器识别。
 * 每个消息类型对应一类特定的消息发送渠道，如短信、邮件、推送等。</p>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * // 获取短信类型
 * MessageType smsType = MessageType.SMS;
 * String typeName = smsType.getTypeName(); // "SMS"
 * String description = smsType.getDescription(); // "短信消息"
 * 
 * // 根据字符串获取类型
 * MessageType type = MessageType.fromString("SMS");
 * }</pre>
 * 
 * @author 初心
 * @version 1.0.0
 * @since 1.0.0
 */
public enum MessageType {
    
    /**
     * 短信消息类型
     * <p>用于发送短信验证码、通知短信等文本消息</p>
     */
    SMS("SMS", "短信消息", "用于发送短信验证码、通知短信等文本消息"),
    
    /**
     * 邮件消息类型
     * <p>用于发送邮件通知、营销邮件等</p>
     */
    EMAIL("EMAIL", "邮件消息", "用于发送邮件通知、营销邮件等"),
    
    /**
     * 推送消息类型
     * <p>用于发送APP推送、系统通知等</p>
     */
    PUSH("PUSH", "推送消息", "用于发送APP推送、系统通知等");
    
    /**
     * 类型名称（用于路由识别）
     */
    private final String typeName;
    
    /**
     * 类型描述
     */
    private final String description;
    
    /**
     * 详细说明
     */
    private final String detail;
    
    /**
     * 构造函数
     * 
     * @param typeName 类型名称
     * @param description 类型描述
     * @param detail 详细说明
     */
    MessageType(String typeName, String description, String detail) {
        this.typeName = typeName;
        this.description = description;
        this.detail = detail;
    }
    
    /**
     * 获取类型名称
     * 
     * @return 类型名称，用于消息路由
     */
    public String getTypeName() {
        return typeName;
    }
    
    /**
     * 获取类型描述
     * 
     * @return 类型的中文描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 获取详细说明
     * 
     * @return 类型的详细功能说明
     */
    public String getDetail() {
        return detail;
    }
    
    /**
     * 根据类型名称获取枚举实例
     * 
     * @param typeName 类型名称（不区分大小写）
     * @return 对应的枚举实例
     * @throws IllegalArgumentException 如果类型名称不存在
     */
    public static MessageType fromString(String typeName) {
        if (typeName == null || typeName.trim().isEmpty()) {
            throw new IllegalArgumentException("消息类型名称不能为空");
        }
        
        for (MessageType type : values()) {
            if (type.typeName.equalsIgnoreCase(typeName.trim())) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("未知的消息类型: " + typeName);
    }
    
    /**
     * 检查指定的类型名称是否有效
     * 
     * @param typeName 类型名称
     * @return 如果类型名称有效返回true，否则返回false
     */
    public static boolean isValid(String typeName) {
        try {
            fromString(typeName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s(%s)", typeName, description);
    }
}
