package com.xiangxi.message.service;

import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.exception.MessageSendException;
import com.xiangxi.message.common.model.MessageResult;
import com.xiangxi.message.config.SmsConfigManager;
import com.xiangxi.message.manager.MessageSenderManager;
import com.xiangxi.message.sms.tencent.TencentSmsConfig;
import com.xiangxi.message.sms.tencent.TencentSmsMessage;
import org.springframework.stereotype.Service;

/**
 * Message SDK 核心服务类
 * 提供简单易用的消息发送接口
 * 
 * @author 初心
 */
@Service
public class MessageSdkService {

    private final SmsConfigManager smsConfigManager;
    
    // 常量定义
    private static final String SMS_TYPE = MessageType.SMS.getTypeName().toLowerCase();
    private static final String TENCENT_CHANNEL = SmsChannel.TENCENT_SMS.getChannelName().toLowerCase();
    private static final String ALIYUN_CHANNEL = SmsChannel.ALI_SMS.getChannelName().toLowerCase();

    public MessageSdkService(SmsConfigManager smsConfigManager) {
        this.smsConfigManager = smsConfigManager;
    }

    /**
     * 获取支持的短信渠道
     * 
     * @return 支持的渠道数组
     */
    public String[] getSupportedVendors() {
        return new String[]{TENCENT_CHANNEL, ALIYUN_CHANNEL};
    }

    /**
     * 检查厂商是否支持
     * 
     * @param vendor 厂商名称
     * @return 是否支持
     */
    public boolean isVendorSupported(String vendor) {
        if (vendor == null) {
            return false;
        }
        String lowerVendor = vendor.toLowerCase();
        return TENCENT_CHANNEL.contains(lowerVendor) || ALIYUN_CHANNEL.contains(lowerVendor);
    }

    /**
     * 发送短信（使用默认配置）
     * 
     * @param phoneNumber 手机号
     * @param templateId 模板ID
     * @param templateParams 模板参数
     * @return 发送结果
     * @throws MessageSendException 发送失败
     */
    public MessageResult sendSms(String phoneNumber, String templateId, String[] templateParams) throws MessageSendException {
        String[] enabledVendors = smsConfigManager.getEnabledVendors();
        if (enabledVendors.length == 0) {
            throw new IllegalStateException("没有启用的短信厂商，请检查配置");
        }
        String defaultVendor = enabledVendors[0]; // 使用第一个启用的厂商
        return sendSms(defaultVendor, phoneNumber, templateId, templateParams);
    }

    /**
     * 发送短信（指定厂商）
     * 
     * @param vendor 厂商（tencent/aliyun）
     * @param phoneNumber 手机号
     * @param templateId 模板ID
     * @param templateParams 模板参数
     * @return 发送结果
     * @throws MessageSendException 发送失败
     */
    public MessageResult sendSms(String vendor, String phoneNumber, String templateId, String[] templateParams) throws MessageSendException {
        if (!isVendorSupported(vendor)) {
            throw new IllegalArgumentException("Unsupported vendor: " + vendor + ". Supported vendors: " + String.join(", ", getSupportedVendors()));
        }
        if (TENCENT_CHANNEL.contains(vendor.toLowerCase())) {
            return sendTencentSms(phoneNumber, templateId, templateParams);
        } else if (ALIYUN_CHANNEL.contains(vendor.toLowerCase())) {
            throw new UnsupportedOperationException("阿里云短信暂未实现");
        }
        
        throw new IllegalStateException("Unexpected vendor: " + vendor);
    }

    /**
     * 发送腾讯云短信
     * 
     * @param phoneNumber 手机号
     * @param templateId 模板ID
     * @param templateParams 模板参数
     * @return 发送结果
     * @throws MessageSendException 发送失败
     */
    public MessageResult sendTencentSms(String phoneNumber, String templateId, String[] templateParams) throws MessageSendException {
        if (!smsConfigManager.isVendorEnabled(TENCENT_CHANNEL)) {
            throw new IllegalStateException("腾讯云 SMS 未启用，请在配置中启用");
        }
        TencentSmsConfig config = smsConfigManager.getTencentSmsConfig();
        TencentSmsMessage message = TencentSmsMessage.builder()
                .addPhone(phoneNumber)
                .templateId(templateId)
                .addParams(templateParams)
                .build();

        return MessageSenderManager.send(SMS_TYPE, TENCENT_CHANNEL, config, message);
    }
}
