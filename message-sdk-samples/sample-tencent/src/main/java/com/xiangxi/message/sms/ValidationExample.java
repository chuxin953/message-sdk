package com.xiangxi.message.sms;

import com.xiangxi.message.common.validation.ValidationException;
import com.xiangxi.message.common.validation.ValidationResult;
import com.xiangxi.message.common.validation.Validator;
import com.xiangxi.message.sms.tencent.TencentSmsConfig;
import com.xiangxi.message.sms.tencent.TencentSmsRequest;

/**
 * 参数校验示例
 */
public class ValidationExample {
    
    public static void main(String[] args) {
        // 示例1: 正确的参数
        System.out.println("=== 示例1: 正确的参数 ===");
        try {
            TencentSmsConfig config = new TencentSmsConfig(
                "测试签名", "app123", "key456", "sdk789", "ap-beijing", "template001"
            );
            
            TencentSmsRequest message = TencentSmsRequest.builder()
                .addPhone("+8613800138000")
                .addParam("123456")
                .addParam("5")
                .build();
            
            // 校验配置
            Validator.validate(config);
            System.out.println("✓ 配置校验通过");
            
            // 校验消息
            Validator.validate(message);
            System.out.println("✓ 消息校验通过");
            
        } catch (ValidationException e) {
            System.err.println("✗ 校验失败: " + e.getMessage());
        }
        
        // 示例2: 配置参数缺失
        System.out.println("\n=== 示例2: 配置参数缺失 ===");
        try {
            TencentSmsConfig config = new TencentSmsConfig(
                "测试签名", null, "key456", "sdk789", "ap-beijing", "template001"  // appId为null
            );
            
            Validator.validate(config);
            System.out.println("✓ 配置校验通过");
            
        } catch (ValidationException e) {
            System.err.println("✗ 校验失败: " + e.getMessage());
        }
        
        // 示例3: 消息参数缺失
        System.out.println("\n=== 示例3: 消息参数缺失 ===");
        try {
            TencentSmsRequest message = TencentSmsRequest.builder()
                .addPhone("+8613800138000")
                // 没有添加模板参数
                .build();
            
            Validator.validate(message);
            System.out.println("✓ 消息校验通过");
            
        } catch (ValidationException e) {
            System.err.println("✗ 校验失败: " + e.getMessage());
        }
        
        // 示例4: 使用静默校验
        System.out.println("\n=== 示例4: 使用静默校验 ===");
        TencentSmsConfig config = new TencentSmsConfig(
            "测试签名", "", "key456", "sdk789", "ap-beijing", "template001"  // appId为空字符串
        );
        
        ValidationResult result = Validator.validateQuietly(config);
        if (result.isSuccess()) {
            System.out.println("✓ 校验通过");
        } else {
            System.out.println("✗ 校验失败: " + result.getMessage());
            for (ValidationResult.ValidationError error : result.getErrors()) {
                System.out.println("  - " + error.toString());
            }
        }
        
        // 示例5: 空字符串校验
        System.out.println("\n=== 示例5: 空字符串校验 ===");
        try {
            TencentSmsConfig config2 = new TencentSmsConfig(
                "测试签名", "   ", "key456", "sdk789", "ap-beijing", "template001"  // appId为空白字符串
            );
            
            Validator.validate(config2);
            System.out.println("✓ 配置校验通过");
            
        } catch (ValidationException e) {
            System.err.println("✗ 校验失败: " + e.getMessage());
        }
    }
}
