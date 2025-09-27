package com.xiangxi.message.samples;

import com.xiangxi.message.sms.model.SmsRequest;
import com.xiangxi.message.sms.model.SmsResponse;

import java.util.List;
import java.util.Map;

/**
 * 多接收人短信发送示例
 * 
 * <p>展示如何使用新的多接收人功能发送短信。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class MultiRecipientSmsExample {
    
    public static void main(String[] args) {
        // 1. 单个接收人示例
        singleRecipientExample();
        
        // 2. 多个接收人示例（相同模板参数）
        multipleRecipientsExample();
        
    }
    
    /**
     * 单个接收人示例
     */
    private static void singleRecipientExample() {
        System.out.println("=== 单个接收人示例 ===");
        
        // 创建单个接收人的短信请求
        SmsRequest request = SmsRequest.of(
            "13800138000",
            "SMS_123456",
            Map.of("code", "1234", "product", "SDK")
        );
        
        System.out.println("请求: " + request);
        System.out.println("接收人数量: " + request.getRecipientCount());
        System.out.println("是否为单个接收人: " + request.isSingleRecipient());
        System.out.println("是否为多个接收人: " + request.isMultipleRecipients());
        System.out.println("第一个手机号: " + request.getPhoneNumber());
    }
    
    /**
     * 多个接收人示例（相同模板参数）
     */
    private static void multipleRecipientsExample() {
        System.out.println("\n=== 多个接收人示例（相同模板参数） ===");
        
        // 创建多个接收人的短信请求
        List<String> phoneNumbers = List.of(
            "13800138000",
            "13800138001", 
            "13800138002"
        );
        
        SmsRequest request = SmsRequest.of(
            phoneNumbers,
            "SMS_123456",
            Map.of("code", "1234", "product", "SDK")
        );
        
        System.out.println("请求: " + request);
        System.out.println("接收人数量: " + request.getRecipientCount());
        System.out.println("是否为单个接收人: " + request.isSingleRecipient());
        System.out.println("是否为多个接收人: " + request.isMultipleRecipients());
        System.out.println("手机号列表: " + request.getPhoneNumbers());
        
        // 验证请求
        try {
            request.validate();
            System.out.println("请求验证通过");
        } catch (IllegalArgumentException e) {
            System.err.println("请求验证失败: " + e.getMessage());
        }
    }
    
    
    /**
     * 创建响应示例
     */
    private static void createResponseExample() {
        System.out.println("\n=== 创建响应示例 ===");
        
        // 创建成功响应
        List<String> phoneNumbers = List.of("13800138000", "13800138001", "13800138002");
        SmsResponse successResponse = SmsResponse.success(
            "msg_123456789",
            phoneNumbers,
            "SMS_123456",
            Map.of("code", "1234", "product", "SDK"),
            "TENCENT_SMS",
            1500
        );
        
        System.out.println("成功响应: " + successResponse);
        System.out.println("接收人数量: " + successResponse.getRecipientCount());
        System.out.println("第一个手机号: " + successResponse.getPhoneNumber());
        
        // 创建失败响应
        SmsResponse failureResponse = SmsResponse.failure(
            "发送失败",
            "SEND_FAILED",
            phoneNumbers,
            "SMS_123456",
            "TENCENT_SMS",
            500
        );
        
        System.out.println("失败响应: " + failureResponse);
    }
}
