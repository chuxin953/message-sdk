package com.xiangxi.message.common.util;

import java.util.regex.Pattern;

/**
 * 消息验证工具类
 * 
 * <p>提供各种消息类型的验证功能，包括手机号、邮箱、消息内容等。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class MessageValidator {
    
    // 中国手机号正则表达式
    private static final Pattern CHINA_MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // 国际手机号正则表达式（简化版）
    private static final Pattern INTERNATIONAL_MOBILE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    // 设备Token正则表达式（简化版）
    private static final Pattern DEVICE_TOKEN_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{20,}$");
    
    // 消息内容最大长度
    private static final int MAX_CONTENT_LENGTH = 10000;
    
    // 模板ID正则表达式
    private static final Pattern TEMPLATE_ID_PATTERN = Pattern.compile("^[A-Z0-9_]{1,50}$");
    
    /**
     * 验证中国手机号
     * 
     * @param phoneNumber 手机号
     * @return 如果格式正确返回true
     */
    public static boolean isValidChinaMobile(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return CHINA_MOBILE_PATTERN.matcher(phoneNumber.trim()).matches();
    }
    
    /**
     * 验证国际手机号
     * 
     * @param phoneNumber 手机号
     * @return 如果格式正确返回true
     */
    public static boolean isValidInternationalMobile(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return INTERNATIONAL_MOBILE_PATTERN.matcher(phoneNumber.trim()).matches();
    }
    
    /**
     * 验证手机号（支持中国和国际格式）
     * 
     * @param phoneNumber 手机号
     * @return 如果格式正确返回true
     */
    public static boolean isValidMobile(String phoneNumber) {
        return isValidChinaMobile(phoneNumber) || isValidInternationalMobile(phoneNumber);
    }
    
    /**
     * 验证邮箱地址
     * 
     * @param email 邮箱地址
     * @return 如果格式正确返回true
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * 验证设备Token
     * 
     * @param deviceToken 设备Token
     * @return 如果格式正确返回true
     */
    public static boolean isValidDeviceToken(String deviceToken) {
        if (deviceToken == null || deviceToken.trim().isEmpty()) {
            return false;
        }
        return DEVICE_TOKEN_PATTERN.matcher(deviceToken.trim()).matches();
    }
    
    /**
     * 验证消息内容
     * 
     * @param content 消息内容
     * @return 如果内容有效返回true
     */
    public static boolean isValidContent(String content) {
        if (content == null) {
            return false;
        }
        return content.length() <= MAX_CONTENT_LENGTH;
    }
    
    /**
     * 验证模板ID
     * 
     * @param templateId 模板ID
     * @return 如果格式正确返回true
     */
    public static boolean isValidTemplateId(String templateId) {
        if (templateId == null || templateId.trim().isEmpty()) {
            return false;
        }
        return TEMPLATE_ID_PATTERN.matcher(templateId.trim()).matches();
    }
    
    /**
     * 验证消息类型
     * 
     * @param messageType 消息类型
     * @return 如果类型有效返回true
     */
    public static boolean isValidMessageType(String messageType) {
        if (messageType == null || messageType.trim().isEmpty()) {
            return false;
        }
        String type = messageType.trim().toUpperCase();
        return "SMS".equals(type) || "EMAIL".equals(type) || "PUSH".equals(type);
    }
    
    /**
     * 验证渠道标识
     * 
     * @param channel 渠道标识
     * @return 如果格式正确返回true
     */
    public static boolean isValidChannel(String channel) {
        if (channel == null || channel.trim().isEmpty()) {
            return false;
        }
        String ch = channel.trim();
        return ch.length() >= 2 && ch.length() <= 50 && ch.matches("^[A-Z0-9_]+$");
    }
    
    /**
     * 验证路由键（type:channel格式）
     * 
     * @param routeKey 路由键
     * @return 如果格式正确返回true
     */
    public static boolean isValidRouteKey(String routeKey) {
        if (routeKey == null || routeKey.trim().isEmpty()) {
            return false;
        }
        
        String[] parts = routeKey.trim().split(":");
        if (parts.length != 2) {
            return false;
        }
        
        return isValidMessageType(parts[0]) && isValidChannel(parts[1]);
    }
    
    /**
     * 验证非空字符串
     * 
     * @param value 要验证的值
     * @param fieldName 字段名称（用于错误信息）
     * @throws IllegalArgumentException 如果值为空
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    /**
     * 验证非空对象
     * 
     * @param value 要验证的值
     * @param fieldName 字段名称（用于错误信息）
     * @throws IllegalArgumentException 如果值为null
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }
    
    /**
     * 验证数值范围
     * 
     * @param value 要验证的值
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @param fieldName 字段名称（用于错误信息）
     * @throws IllegalArgumentException 如果值超出范围
     */
    public static void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max + ", got: " + value);
        }
    }
    
    /**
     * 验证字符串长度
     * 
     * @param value 要验证的值
     * @param minLength 最小长度（包含）
     * @param maxLength 最大长度（包含）
     * @param fieldName 字段名称（用于错误信息）
     * @throws IllegalArgumentException 如果长度超出范围
     */
    public static void validateLength(String value, int minLength, int maxLength, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        int length = value.length();
        if (length < minLength || length > maxLength) {
            throw new IllegalArgumentException(fieldName + " length must be between " + minLength + " and " + maxLength + ", got: " + length);
        }
    }
}
