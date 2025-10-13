package com.xiangxi.message.autoconfigure;

import com.xiangxi.message.manager.MessageSenderManager;
import com.xiangxi.message.service.MessageSdkService;
import com.xiangxi.message.config.SmsConfigManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Message SDK 自动配置类
 * 
 * @author 初心
 */
@Configuration
@ConditionalOnClass(MessageSenderManager.class)
@EnableConfigurationProperties({MessageSdkProperties.class, SmsVendorProperties.class})
public class MessageSdkAutoConfiguration {


    /**
     * 配置短信配置管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SmsConfigManager smsConfigManager(SmsVendorProperties smsVendorProperties) {
        return new SmsConfigManager(smsVendorProperties);
    }

    /**
     * 配置 MessageSdkService Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageSdkService messageSdkService(SmsConfigManager smsConfigManager) {
        return new MessageSdkService(smsConfigManager);
    }
}