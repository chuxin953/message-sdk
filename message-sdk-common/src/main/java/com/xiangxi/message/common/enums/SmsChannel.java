package com.xiangxi.message.common.enums;

/**
 * 短信渠道枚举
 * 
 * <p>定义了系统支持的所有短信发送渠道，用于短信发送器的识别和路由。
 * 每个渠道对应一个具体的短信服务提供商实现。</p>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * // 获取腾讯云短信渠道
 * SmsChannel channel = SmsChannel.TENCENT_SMS;
 * String channelName = channel.getChannelName(); // "TENCENT_SMS"
 * String provider = channel.getProvider(); // "腾讯云"
 * 
 * // 根据字符串获取渠道
 * SmsChannel channel = SmsChannel.fromString("TENCENT_SMS");
 * 
 * // 检查渠道是否可用
 * boolean available = SmsChannel.TENCENT_SMS.isAvailable();
 * }</pre>
 * 
 * @author 初心
 * @version 1.0.0
 * @since 1.0.0
 */
public enum SmsChannel {
    
    /**
     * 腾讯云短信服务
     * <p>腾讯云SMS服务，支持国内外短信发送</p>
     */
    TENCENT_SMS("TENCENT_SMS", "腾讯云", "腾讯云SMS服务，支持国内外短信发送", true);
    /**
     * 渠道名称（用于路由识别）
     */
    private final String channelName;
    
    /**
     * 服务提供商名称
     */
    private final String provider;
    
    /**
     * 渠道描述
     */
    private final String description;
    
    /**
     * 是否可用
     */
    private final boolean available;
    
    /**
     * 构造函数
     * 
     * @param channelName 渠道名称
     * @param provider 服务提供商
     * @param description 渠道描述
     * @param available 是否可用
     */
    SmsChannel(String channelName, String provider, String description, boolean available) {
        this.channelName = channelName;
        this.provider = provider;
        this.description = description;
        this.available = available;
    }
    
    /**
     * 获取渠道名称
     * 
     * @return 渠道名称，用于消息路由
     */
    public String getChannelName() {
        return channelName;
    }
    
    /**
     * 获取服务提供商名称
     * 
     * @return 服务提供商名称
     */
    public String getProvider() {
        return provider;
    }
    
    /**
     * 获取渠道描述
     * 
     * @return 渠道的详细描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查渠道是否可用
     * 
     * @return 如果渠道可用返回true，否则返回false
     */
    public boolean isAvailable() {
        return available;
    }
    
    /**
     * 根据渠道名称获取枚举实例
     * 
     * @param channelName 渠道名称（不区分大小写）
     * @return 对应的枚举实例
     * @throws IllegalArgumentException 如果渠道名称不存在
     */
    public static SmsChannel fromString(String channelName) {
        if (channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("短信渠道名称不能为空");
        }
        
        for (SmsChannel channel : values()) {
            if (channel.channelName.equalsIgnoreCase(channelName.trim())) {
                return channel;
            }
        }
        
        throw new IllegalArgumentException("未知的短信渠道: " + channelName);
    }
    
    /**
     * 检查指定的渠道名称是否有效
     * 
     * @param channelName 渠道名称
     * @return 如果渠道名称有效返回true，否则返回false
     */
    public static boolean isValid(String channelName) {
        try {
            fromString(channelName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 获取所有可用的短信渠道
     * 
     * @return 可用渠道数组
     */
    public static SmsChannel[] getAvailableChannels() {
        return java.util.Arrays.stream(values())
                .filter(SmsChannel::isAvailable)
                .toArray(SmsChannel[]::new);
    }
    
    @Override
    public String toString() {
        return String.format("%s(%s) - %s", channelName, provider, available ? "可用" : "不可用");
    }
}
