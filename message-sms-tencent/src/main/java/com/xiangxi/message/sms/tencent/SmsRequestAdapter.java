package com.xiangxi.message.sms.tencent;

import com.xiangxi.message.common.validation.ValidationException;
import com.xiangxi.message.common.validation.Validator;
import com.xiangxi.message.sms.model.SmsRequest;

import java.util.List;
import java.util.Map;

/**
 * 短信请求适配器
 * 
 * <p>将新的 SmsRequest 转换为腾讯云的 TencentSmsMessage。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class SmsRequestAdapter {

    /**
     * 单次发送最大手机号数量限制
     */
    private static final int MAX_PHONE_COUNT = 200;
    
    /**
     * 将 SmsRequest 转换为 TencentSmsMessage
     * 
     * @param smsRequest 短信请求
     * @return 腾讯云短信消息
     */
    public static TencentSmsMessage toTencentSmsMessage(SmsRequest smsRequest) {
        if (smsRequest == null) {
            throw new IllegalArgumentException("SmsRequest cannot be null");
        }
        // 验证请求
        smsRequest.validate();
        
        // 转换手机号格式（添加国际区号）
        List<String> phoneNumbers = smsRequest.phoneNumbers();
        if (phoneNumbers.size() > MAX_PHONE_COUNT){
            throw new ValidationException("手机号一次发送最多支持200个");
        }

        List<String> tencentPhones = phoneNumbers.stream()
                .map(SmsRequestAdapter::formatPhoneNumber)
                .toList();
        
        // 构建腾讯云短信消息
        TencentSmsMessage.Builder builder = TencentSmsMessage.builder()
                .addPhones(tencentPhones)
                .templateId(smsRequest.templateId())
                .action(TencentSmsAction.SendSms.toString());
        
        // 处理模板参数（所有接收人使用相同参数）
        Map<String, String> params = smsRequest.templateParams();
        if (params != null && !params.isEmpty()) {
            // 按参数名排序，确保顺序一致
            List<String> paramValues = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .toList();
            builder.addParams(paramValues);
        }
        
        return builder.build();
    }
    
    
    /**
     * 格式化手机号（添加国际区号）
     * 
     * @param phoneNumber 原始手机号
     * @return 格式化后的手机号
     */
    private static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        
        String phone = phoneNumber.trim();
        
        // 如果已经包含国际区号，直接返回
        if (phone.startsWith("+")) {
            return phone;
        }
        
        // 如果是中国手机号，添加+86
        if (phone.startsWith("1") && phone.length() == 11) {
            return "+86" + phone;
        }
        
        // 其他情况，假设是中国手机号
        if (phone.length() == 11) {
            return "+86" + phone;
        }
        
        // 如果格式不符合预期，抛出异常
        throw new IllegalArgumentException("无法识别的手机号格式: " + phoneNumber);
    }
}
