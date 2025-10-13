# Message SDK

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0+-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

一个基于 Java SPI 的多渠道消息发送 SDK，提供统一的消息发送接口，支持短信等多种消息类型的发送。支持 Spring Boot 自动配置，开箱即用。

> **注意**: 当前版本专注于短信发送功能，暂不支持消息厂商的其他操作（如修改短信模板、签名等），仅提供消息发送能力。

## ✨ 核心特性

- **🚀 统一接口**：提供统一的消息发送入口，按 `type:channel` 自动路由
- **🔌 插件化架构**：基于 Java SPI 机制，支持零侵入式扩展第三方渠道
- **🛡️ 线程安全**：全面的线程安全设计，支持高并发场景
- **🌱 Spring Boot 集成**：提供 Spring Boot Starter，支持自动配置
- **📝 完整文档**：详细的 JavaDoc 注释和使用示例
- **🏗️ 模块化设计**：清晰的分层架构，易于维护和扩展
- **📱 多厂商支持**：支持腾讯云、阿里云等主流短信服务商

## 🏗️ 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    应用层 (Application)                      │
├─────────────────────────────────────────────────────────────┤
│              Spring Boot Starter / MessageSdkService        │
│                   (统一服务入口)                             │
├─────────────────────────────────────────────────────────────┤
│                MessageSenderManager                         │
│                   (统一管理入口)                             │
├─────────────────────────────────────────────────────────────┤
│  MessageSender API  │  Common Utilities   │
│    (核心接口)        │    (通用工具)        │
├─────────────────────────────────────────────────────────────┤
│                    SMS Module (短信抽象层)                    │
│              SmsRequest / SmsResponse (数据模型)              │
├─────────────────────────────────────────────────────────────┤
│ Tencent SMS │ Aliyun SMS │ Custom SMS │ ... (扩展)         │
│  (具体实现)  │  (具体实现)  │  (具体实现) │                    │
└─────────────────────────────────────────────────────────────┘
```

### 模块说明

| 模块                              | 说明         | 职责 |
|----------------------------------|-------------|------|
| `message-dependencies`           | 依赖管理       | 统一版本管理和依赖声明 |
| `message-sdk-api`               | 核心接口       | 定义消息发送的核心抽象接口 |
| `message-sdk-common`            | 通用组件       | 提供异常、枚举、工具类等通用功能 |
| `message-sdk-sms`               | 短信抽象层      | 短信请求和响应的数据模型 |
| `message-sdk-manager`           | 管理器        | 统一的消息发送调度和路由管理 |
| `message-sdk-client`            | HTTP客户端     | HTTP请求封装和工具类 |
| `message-sms-tencent`           | 腾讯云短信      | 腾讯云短信服务的具体实现 |
| `message-sms-aliyun`            | 阿里云短信      | 阿里云短信服务的具体实现 |
| `message-sdk-spring-boot-starter` | Spring Boot集成 | Spring Boot自动配置和集成 |
| `message-sdk-samples`           | 示例代码       | 使用示例和最佳实践 |

## 🚀 快速开始

### 方式一：Spring Boot 集成（推荐）

在你的 `pom.xml` 中添加 Spring Boot Starter：

```xml
<dependencies>
    <!-- Message SDK Spring Boot Starter -->
    <dependency>
        <groupId>com.xiangxi.message</groupId>
        <artifactId>message-sdk-spring-boot-starter</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 方式二：手动集成

```xml
<dependencies>
    <!-- 核心依赖 - 自动传递其他必要模块 -->
    <dependency>
        <groupId>com.xiangxi.message</groupId>
        <artifactId>message-sdk-manager</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    
    <!-- 短信厂商实现 (按需选择) -->
    <dependency>
        <groupId>com.xiangxi.message</groupId>
        <artifactId>message-sms-tencent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    
    <dependency>
        <groupId>com.xiangxi.message</groupId>
        <artifactId>message-sms-aliyun</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### 2. 配置

#### Spring Boot 配置

在 `application.yml` 中配置：

```yaml
message:
  sdk:
    enabled: true
    default-type: sms
    default-channel: tencent
  sms:
    default-vendor: tencent
    tencent:
      enabled: true
      secret-id: your-secret-id
      secret-key: your-secret-key
      sdk-app-id: your-sdk-app-id
      region: ap-beijing
      sign-name: 您的签名
    aliyun:
      enabled: false
      access-key-id: your-access-key-id
      access-key-secret: your-access-key-secret
      region-id: cn-hangzhou
      sign-name: 您的签名
```

### 3. 使用示例

#### Spring Boot 方式（推荐）

```java
import com.xiangxi.message.service.MessageSdkService;
import com.xiangxi.message.common.model.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    
    @Autowired
    private MessageSdkService messageSdkService;
    
    public void sendSms() {
        try {
            // 发送短信（使用默认厂商）
            MessageResult result = messageSdkService.sendSms(
                "+8613800138000", 
                "123456", 
                new String[]{"1234", "5"}
            );
            
            if (result.isSuccess()) {
                System.out.println("短信发送成功，消息ID: " + result.getMessageId());
            } else {
                System.err.println("短信发送失败: " + result.getErrorMessage());
            }
        } catch (Exception e) {
            System.err.println("发送异常: " + e.getMessage());
        }
    }
    
    public void sendTencentSms() {
        try {
            // 指定厂商发送
            MessageResult result = messageSdkService.sendSms(
                "tencent", 
                "+8613800138000", 
                "123456", 
                new String[]{"1234"}
            );
            
            System.out.println("发送结果: " + result);
        } catch (Exception e) {
            System.err.println("发送异常: " + e.getMessage());
        }
    }
}
```

#### 手动集成方式

```java
import com.xiangxi.message.manager.MessageSenderManager;
import com.xiangxi.message.common.model.MessageResult;
import com.xiangxi.message.sms.tencent.TencentSmsConfig;
import com.xiangxi.message.sms.tencent.TencentSmsMessage;
import com.xiangxi.message.exception.MessageSendException;

public class SmsExample {
    public static void main(String[] args) {
        // 1. 创建配置
        TencentSmsConfig config = new TencentSmsConfig.Builder()
            .secretId("your-secret-id")
            .secretKey("your-secret-key")
            .sdkAppId("your-sdk-app-id")
            .region("ap-beijing")
            .signName("您的签名")
            .build();
        
        // 2. 创建消息
        TencentSmsMessage message = TencentSmsMessage.builder()
            .addPhone("+8613800138000")
            .templateId("123456")
            .addParams("1234", "5")
            .build();
        
        // 3. 发送消息
        try {
            MessageResult result = MessageSenderManager.send(
                "sms", "tencent", config, message
            );
            
            if (result.isSuccess()) {
                System.out.println("短信发送成功，消息ID: " + result.getMessageId());
            } else {
                System.err.println("短信发送失败: " + result.getErrorMessage());
            }
        } catch (MessageSendException e) {
            System.err.println("发送异常: " + e.getMessage());
        }
    }
}
```

### 4. 获取可用的发送器

```java
import com.xiangxi.message.manager.MessageSenderManager;
import com.xiangxi.message.api.MessageSender;

// 获取所有已注册的发送器
Map<String, MessageSender<?, ?, ?>> allSenders = MessageSenderManager.getAllSenders();
System.out.println("可用的消息发送器:");
allSenders.keySet().forEach(key -> {
    System.out.println("- " + key);
});

// 获取特定的发送器
MessageSender<Object, Object, MessageResult> sender = 
    MessageSenderManager.getSender("sms", "tencent");
```


## 🔧 扩展开发

### 实现自定义短信发送器

1. **实现 MessageSender 接口**

```java
import com.xiangxi.message.api.MessageSender;
import com.xiangxi.message.common.model.MessageResult;
import com.xiangxi.message.exception.MessageSendException;

@Component
public class CustomSmsSender implements MessageSender<CustomSmsConfig, CustomSmsMessage, MessageResult> {
    
    @Override
    public String type() {
        return "sms";
    }
    
    @Override
    public String channel() {
        return "custom";
    }
    
    @Override
    public MessageResult send(CustomSmsConfig config, CustomSmsMessage message) throws MessageSendException {
        // 实现具体的发送逻辑
        try {
            // 调用第三方API
            String messageId = callThirdPartyApi(config, message);
            return MessageResult.success(messageId, channel(), type(), System.currentTimeMillis());
        } catch (Exception e) {
            return MessageResult.failure(e.getMessage(), "CUSTOM_ERROR", channel(), type(), System.currentTimeMillis());
        }
    }
    
    private String callThirdPartyApi(CustomSmsConfig config, CustomSmsMessage message) {
        // 调用第三方短信API
        return "custom-message-id";
    }
}
```

2. **注册 SPI 服务**

在 `src/main/resources/META-INF/services/com.xiangxi.message.api.MessageSender` 文件中添加：

```
com.example.CustomSmsSender
```

3. **创建配置和消息模型**

```java
// 配置类
public class CustomSmsConfig {
    private final String apiKey;
    private final String apiSecret;
    private final String signName;
    private final String region;
    
    public CustomSmsConfig(String apiKey, String apiSecret, String signName, String region) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.signName = signName;
        this.region = region;
    }
    
    // getters...
    public String getApiKey() { return apiKey; }
    public String getApiSecret() { return apiSecret; }
    public String getSignName() { return signName; }
    public String getRegion() { return region; }
}

// 消息类
public class CustomSmsMessage {
    private final String phoneNumber;
    private final String templateId;
    private final String[] templateParams;
    
    public CustomSmsMessage(String phoneNumber, String templateId, String[] templateParams) {
        this.phoneNumber = phoneNumber;
        this.templateId = templateId;
        this.templateParams = templateParams;
    }
    
    // getters...
    public String getPhoneNumber() { return phoneNumber; }
    public String getTemplateId() { return templateId; }
    public String[] getTemplateParams() { return templateParams; }
}
```


## 📋 最佳实践

### 1. 配置管理

#### Spring Boot 配置（推荐）

```yaml
# application.yml
message:
  sdk:
    enabled: true
    default-type: sms
    default-channel: tencent
  sms:
    default-vendor: tencent
    tencent:
      enabled: true
      secret-id: ${TENCENT_SECRET_ID:your-secret-id}
      secret-key: ${TENCENT_SECRET_KEY:your-secret-key}
      sdk-app-id: ${TENCENT_SDK_APP_ID:your-sdk-app-id}
      region: ${TENCENT_REGION:ap-beijing}
      sign-name: ${TENCENT_SIGN_NAME:您的签名}
    aliyun:
      enabled: false
      access-key-id: ${ALIYUN_ACCESS_KEY_ID:your-access-key-id}
      access-key-secret: ${ALIYUN_ACCESS_KEY_SECRET:your-access-key-secret}
      region-id: ${ALIYUN_REGION_ID:cn-hangzhou}
      sign-name: ${ALIYUN_SIGN_NAME:您的签名}
```

#### 手动配置管理

```java
// 推荐使用配置类统一管理
@Configuration
public class MessageConfig {
    
    @Bean
    @ConditionalOnProperty(name = "message.sms.tencent.enabled", havingValue = "true")
    public TencentSmsConfig tencentSmsConfig(
            @Value("${message.sms.tencent.secret-id}") String secretId,
            @Value("${message.sms.tencent.secret-key}") String secretKey,
            @Value("${message.sms.tencent.sdk-app-id}") String sdkAppId,
            @Value("${message.sms.tencent.region:ap-beijing}") String region,
            @Value("${message.sms.tencent.sign-name}") String signName) {
        return new TencentSmsConfig.Builder()
            .secretId(secretId)
            .secretKey(secretKey)
            .sdkAppId(sdkAppId)
            .region(region)
            .signName(signName)
            .build();
    }
}
```

### 2. 异常处理

```java
@Service
public class MessageService {
    
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    
    @Autowired
    private MessageSdkService messageSdkService;
    
    public boolean sendSms(String phone, String code) {
        try {
            MessageResult result = messageSdkService.sendSms(phone, "VERIFICATION_CODE", new String[]{code, "5"});
            return result.isSuccess();
        } catch (Exception e) {
            // 记录日志
            log.error("短信发送失败: phone={}, error={}", phone, e.getMessage(), e);
            
            // 可以实现重试机制
            return retryWithBackoff(phone, code);
        }
    }
    
    private boolean retryWithBackoff(String phone, String code) {
        // 实现指数退避重试
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(1000 * (i + 1)); // 1s, 2s, 3s
                MessageResult result = messageSdkService.sendSms(phone, "VERIFICATION_CODE", new String[]{code, "5"});
                if (result.isSuccess()) {
                    return true;
                }
            } catch (Exception e) {
                log.warn("重试发送失败: attempt={}, error={}", i + 1, e.getMessage());
            }
        }
        return false;
    }
}
```

### 3. 性能优化

```java
@Service
public class OptimizedMessageService {
    
    @Autowired
    private MessageSdkService messageSdkService;
    
    // 预热发送器，避免首次调用延迟
    @PostConstruct
    public void warmUp() {
        try {
            messageSdkService.getAllSenders();
            log.info("Message SDK 预热完成");
        } catch (Exception e) {
            log.warn("Message SDK 预热失败", e);
        }
    }
    
    // 异步发送
    @Async
    public CompletableFuture<MessageResult> sendSmsAsync(String phone, String code) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return messageSdkService.sendSms(phone, "VERIFICATION_CODE", new String[]{code, "5"});
            } catch (Exception e) {
                log.error("异步发送失败", e);
                return MessageResult.failure(e.getMessage(), "ASYNC_ERROR", "sms", "async", System.currentTimeMillis());
            }
        });
    }
    
    // 批量发送
    public List<MessageResult> sendBatchSms(List<String> phones, String templateId, String[] params) {
        return phones.parallelStream()
            .map(phone -> {
                try {
                    return messageSdkService.sendSms(phone, templateId, params);
                } catch (Exception e) {
                    log.error("批量发送失败: phone={}", phone, e);
                    return MessageResult.failure(e.getMessage(), "BATCH_ERROR", "sms", "batch", System.currentTimeMillis());
                }
            })
            .collect(Collectors.toList());
    }
}
```

## 🔍 监控和调试

### 日志配置

在 `logback-spring.xml` 中配置：

```xml
<configuration>
    <!-- 消息发送相关日志 -->
    <logger name="com.xiangxi.message" level="INFO" />
    
    <!-- 调试模式 -->
    <logger name="com.xiangxi.message.manager.MessageSenderManager" level="DEBUG" />
    <logger name="com.xiangxi.message.service.MessageSdkService" level="DEBUG" />
    
    <!-- HTTP 请求日志 -->
    <logger name="com.xiangxi.message.client" level="DEBUG" />
</configuration>
```

## 🛠️ 构建和部署

### 环境要求

- **JDK**: 21+
- **Maven**: 3.9+
- **Spring Boot**: 3.0+ (可选)

### 构建命令

```bash
# 编译和测试
mvn clean compile test

# 打包
mvn clean package

# 安装到本地仓库
mvn clean install

# 跳过测试快速构建
mvn clean install -DskipTests
```

### 发布到私有仓库

```bash
# 部署到私有Maven仓库
mvn clean deploy -P release
```

## 📚 API 文档

### 核心接口

- [`MessageSender<C,M,R>`](message-sdk-api/src/main/java/com/xiangxi/message/api/MessageSender.java) - 消息发送器核心接口
- [`MessageSenderManager`](message-sdk-manager/src/main/java/com/xiangxi/message/manager/MessageSenderManager.java) - 消息发送管理器
- [`MessageSdkService`](message-sdk-spring-boot-starter/src/main/java/com/xiangxi/message/service/MessageSdkService.java) - Spring Boot 服务接口

### 数据模型

- [`MessageResult`](message-sdk-common/src/main/java/com/xiangxi/message/common/model/MessageResult.java) - 统一消息发送结果
- [`SmsRequest`](message-sdk-sms/src/main/java/com/xiangxi/message/sms/model/SmsRequest.java) - 短信发送请求
- [`SmsResponse`](message-sdk-sms/src/main/java/com/xiangxi/message/sms/model/SmsResponse.java) - 短信发送响应

### 枚举类型

- [`MessageType`](message-sdk-common/src/main/java/com/xiangxi/message/common/enums/MessageType.java) - 消息类型枚举
- [`SmsChannel`](message-sdk-common/src/main/java/com/xiangxi/message/common/enums/SmsChannel.java) - 短信渠道枚举

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙋‍♂️ 支持

如果你在使用过程中遇到问题，可以通过以下方式获取帮助：

- 📖 查看 [Wiki 文档](../../wiki)
- 🐛 提交 [Issue](../../issues)
- 💬 参与 [Discussions](../../discussions)

---

**Made with ❤️ by 初心**