package com.xiangxi.message.config;

import com.xiangxi.message.autoconfigure.SmsVendorProperties;
import com.xiangxi.message.sms.tencent.TencentSmsConfig;
import com.xiangxi.message.sms.aliyun.AliyunSmsConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信配置管理器
 * 
 * @author 初心
 */
@Component
public class SmsConfigManager {

    private final SmsVendorProperties smsVendorProperties;
    private final Map<String, Object> configCache = new HashMap<>();

    public SmsConfigManager(SmsVendorProperties smsVendorProperties) {
        this.smsVendorProperties = smsVendorProperties;
    }

    /**
     * 获取腾讯云 SMS 配置
     */
    public TencentSmsConfig getTencentSmsConfig() {
        String key = "tencent-sms-config";
        if (!configCache.containsKey(key)) {
            SmsVendorProperties.TencentSmsProperties props = smsVendorProperties.getTencent();
            if (props.getSecretId() == null || props.getSecretKey() == null || props.getSdkAppId() == null) {
                throw new IllegalStateException("腾讯云 SMS 配置不完整，请检查 secretId、secretKey、sdkAppId 配置");
            }
            
            TencentSmsConfig config = new TencentSmsConfig.Builder()
                    .secretId(props.getSecretId())
                    .secretKey(props.getSecretKey())
                    .sdkAppId(props.getSdkAppId())
                    .region(props.getRegion())
                    .signName(props.getSignName())
                    .build();
            configCache.put(key, config);
        }
        return (TencentSmsConfig) configCache.get(key);
    }

    /**
     * 获取阿里云 SMS 配置
     */
    public AliyunSmsConfig getAliyunSmsConfig() {
        String key = "aliyun-sms-config";
        if (!configCache.containsKey(key)) {
            SmsVendorProperties.AliyunSmsProperties props = smsVendorProperties.getAliyun();
            if (props.getAccessKeyId() == null || props.getAccessKeySecret() == null) {
                throw new IllegalStateException("阿里云 SMS 配置不完整，请检查 accessKeyId、accessKeySecret 配置");
            }
            
            AliyunSmsConfig config = new AliyunSmsConfig.Builder()
                    .accessKeyId(props.getAccessKeyId())
                    .accessKeySecret(props.getAccessKeySecret())
                    .signName(props.getSignName())
                    .regionId(props.getRegionId())
                    .build();
            configCache.put(key, config);
        }
        return (AliyunSmsConfig) configCache.get(key);
    }

    /**
     * 获取默认厂商配置
     */
    public Object getDefaultSmsConfig() {
        String defaultVendor = smsVendorProperties.getDefaultVendor();
        return switch (defaultVendor.toLowerCase()) {
            case "tencent" -> getTencentSmsConfig();
            case "aliyun" -> getAliyunSmsConfig();
            default -> throw new IllegalArgumentException("不支持的默认厂商: " + defaultVendor);
        };
    }

    /**
     * 检查厂商是否启用
     */
    public boolean isVendorEnabled(String vendor) {
        return switch (vendor.toLowerCase()) {
            case "tencent" -> smsVendorProperties.getTencent().isEnabled();
            case "aliyun" -> smsVendorProperties.getAliyun().isEnabled();
            default -> false;
        };
    }

    /**
     * 获取所有启用的厂商
     */
    public String[] getEnabledVendors() {
        return smsVendorProperties.getTencent().isEnabled() && smsVendorProperties.getAliyun().isEnabled() 
            ? new String[]{"tencent", "aliyun"}
            : smsVendorProperties.getTencent().isEnabled() 
                ? new String[]{"tencent"}
                : smsVendorProperties.getAliyun().isEnabled() 
                    ? new String[]{"aliyun"}
                    : new String[0];
    }
}