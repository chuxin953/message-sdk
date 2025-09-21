# Message SDK

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

一个基于 Java SPI 的多渠道消息发送 SDK，提供统一的消息发送接口，支持短信、邮件、推送等多种消息类型的发送。
暂时只支持短信的发送等 不进行消息厂商的其他操作 比如修改短信的模板 签名等处理 暂时只支持发送短信等

## ✨ 核心特性

- **🚀 统一接口**：提供统一的消息发送入口，按 `type:channel` 自动路由
- **🔌 插件化架构**：基于 Java SPI 机制，支持零侵入式扩展第三方渠道
- **📊 事件驱动**：内置事件发布机制，支持消息发送监控和审计
- **🛡️ 线程安全**：全面的线程安全设计，支持高并发场景
- **📝 完整文档**：详细的 JavaDoc 注释和使用示例
- **🏗️ 模块化设计**：清晰的分层架构，易于维护和扩展

## 🏗️ 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    应用层 (Application)                      │
├─────────────────────────────────────────────────────────────┤
│                MessageSenderManager                         │
│                   (统一管理入口)                             │
├─────────────────────────────────────────────────────────────┤
│  MessageSender API  │  Event System  │  Common Utilities   │
│    (核心接口)        │   (事件系统)    │    (通用工具)        │
├─────────────────────────────────────────────────────────────┤
│   SMS Module   │   Email Module   │   Push Module        │
│   (短信模块)    │    (邮件模块)     │   (推送模块)          │
├─────────────────────────────────────────────────────────────┤
│ Tencent SMS │ Aliyun SMS │ SendGrid │ JPush │ ... (扩展)   │
│  (具体实现)  │  (具体实现)  │  (具体实现) │ (具体实现) │          │
└─────────────────────────────────────────────────────────────┘
```

### 模块说明

| 模块 | 说明 | 职责 |
|------|------|------|
| `message-dependencies` | 依赖管理 | 统一版本管理和依赖声明 |
| `message-sdk-api` | 核心接口 | 定义消息发送的核心抽象接口 |
| `message-sdk-common` | 通用组件 | 提供异常、枚举、工具类等通用功能 |
| `message-sdk-manager` | 管理器 | 统一的消息发送调度和路由管理 |
| `message-sdk-logging` | 日志组件 | 事件发布、监听和日志记录 |
| `message-sdk-sms` | 短信抽象 | 短信发送的抽象层和通用模型 |
| `message-sdk-email` | 邮件抽象 | 邮件发送的抽象层和通用模型 |
| `message-sdk-push` | 推送抽象 | 推送消息的抽象层和通用模型 |
| `message-sms-tencent` | 腾讯云短信 | 腾讯云短信服务的具体实现 |
| `message-sdk-samples` | 示例代码 | 使用示例和最佳实践 |

## 🚀 快速开始

### 1. 添加依赖

在你的 `pom.xml` 中添加以下依赖：

```xml
<dependencies>
    <!-- 核心管理器 -->
    <dependency>
        <groupId>com.xiangxi.message</groupId>
        <artifactId>message-sdk-manager</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    <!-- 腾讯云短信实现 (可选) -->
    <dependency>
        <groupId>com.xiangxi.message</groupId>
        <artifactId>message-sms-tencent</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

### 2. 发送短信示例

```java
import com.xiangxi.message.manager.MessageSenderManager;
import com.xiangxi.message.sms.model.SmsResponse;
import com.xiangxi.message.tencent.config.TencentSmsConfig;
import com.xiangxi.message.tencent.model.TencentSmsRequest;
import com.xiangxi.message.common.exception.MessageSendException;

public class SmsExample {
    public static void main(String[] args) {
        // 1. 创建配置
        TencentSmsConfig config = new TencentSmsConfig(
            "MyApp",           // 签名
            "your-secret-id",  // SecretId
            "your-secret-key", // SecretKey
            "ap-beijing"       // 地域
        );
        
        // 2. 创建请求
        TencentSmsRequest request = new TencentSmsRequest(
            "+8613800138000",              // 手机号
            "SMS_123456789",               // 模板ID
            Map.of("code", "1234")         // 模板参数
        );
        
        // 3. 发送消息
        try {
            SmsResponse response = MessageSenderManager.send(
                "SMS", "TENCENT_SMS", config, request
            );
            
            if (response.success()) {
                System.out.println("短信发送成功，消息ID: " + response.messageId());
            } else {
                System.err.println("短信发送失败: " + response.error());
            }
        } catch (MessageSendException e) {
            System.err.println("发送异常: " + e.getMessage());
        }
    }
}
```

### 3. 获取可用的发送器

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
MessageSender<TencentSmsConfig, TencentSmsRequest, SmsResponse> sender = 
    MessageSenderManager.getSender("SMS", "TENCENT_SMS");
```

## 🔧 扩展开发

### 实现自定义消息发送器

1. **实现 MessageSender 接口**

```java
@Component
public class CustomSmsSender implements ISmsSender<CustomSmsConfig, CustomSmsRequest> {
    
    @Override
    public String type() {
        return "SMS";
    }
    
    @Override
    public String channel() {
        return "CUSTOM_SMS";
    }
    
    @Override
    public SmsResponse send(CustomSmsConfig config, CustomSmsRequest message) 
            throws MessageSendException {
        // 实现具体的发送逻辑
        try {
            // 调用第三方API
            String messageId = callThirdPartyApi(config, message);
            return new SmsResponse(true, messageId, null);
        } catch (Exception e) {
            return new SmsResponse(false, null, e.getMessage());
        }
    }
}
```

2. **注册 SPI 服务**

在 `src/main/resources/META-INF/services/com.xiangxi.message.api.MessageSender` 文件中添加：

```
com.example.CustomSmsSender
```

3. **创建配置和请求模型**

```java
// 配置类
public class CustomSmsConfig extends SmsConfig {
    private final String apiKey;
    private final String apiSecret;
    
    public CustomSmsConfig(String sign, String apiKey, String apiSecret) {
        super(sign);
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }
    
    // getters...
}

// 请求类
public record CustomSmsRequest(
    String phoneNumber,
    String templateId,
    Map<String, String> params
) {}
```

### 事件监听

```java
import com.xiangxi.message.logging.EventListener;
import com.xiangxi.message.logging.MessageSentEvent;
import com.xiangxi.message.logging.MessageEventPublisher;

// 自定义事件监听器
public class CustomMessageListener implements EventListener<MessageSentEvent> {
    
    @Override
    public void onEvent(MessageSentEvent event) {
        System.out.printf("消息发送完成: %s:%s, 结果: %s%n", 
            event.type(), event.channel(), event.result());
        
        // 可以在这里实现:
        // - 发送统计
        // - 审计日志
        // - 监控告警
        // - 数据上报
    }
}

// 注册监听器
MessageEventPublisher publisher = new MessageEventPublisher();
publisher.register(new CustomMessageListener());
```

## 📋 最佳实践

### 1. 配置管理

```java
// 推荐使用配置类统一管理
@Configuration
public class MessageConfig {
    
    @Bean
    public TencentSmsConfig tencentSmsConfig(
            @Value("${message.sms.tencent.secret-id}") String secretId,
            @Value("${message.sms.tencent.secret-key}") String secretKey) {
        return new TencentSmsConfig("MyApp", secretId, secretKey, "ap-beijing");
    }
}
```

### 2. 异常处理

```java
public class MessageService {
    
    public boolean sendSms(String phone, String code) {
        try {
            SmsResponse response = MessageSenderManager.send(
                "SMS", "TENCENT_SMS", config, request
            );
            return response.success();
        } catch (MessageSendException e) {
            // 记录日志
            log.error("短信发送失败: phone={}, error={}", phone, e.getMessage(), e);
            
            // 可以实现重试机制
            return retryWithBackoff(phone, code);
        }
    }
}
```

### 3. 性能优化

```java
// 预热发送器，避免首次调用延迟
@PostConstruct
public void warmUp() {
    MessageSenderManager.getAllSenders();
}

// 异步发送
@Async
public CompletableFuture<SmsResponse> sendSmsAsync(String phone, String code) {
    return CompletableFuture.supplyAsync(() -> {
        return MessageSenderManager.send("SMS", "TENCENT_SMS", config, request);
    });
}
```

## 🔍 监控和调试

### 日志配置

在 `logback.xml` 中配置：

```xml
<configuration>
    <!-- 消息发送相关日志 -->
    <logger name="com.xiangxi.message" level="INFO" />
    
    <!-- 调试模式 -->
    <logger name="com.xiangxi.message.manager.MessageSenderManager" level="DEBUG" />
</configuration>
```

### 健康检查

```java
@Component
public class MessageHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            Map<String, MessageSender<?, ?, ?>> senders = MessageSenderManager.getAllSenders();
            return Health.up()
                .withDetail("available-senders", senders.keySet())
                .withDetail("total-count", senders.size())
                .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
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
- [`ISmsSender<C,M>`](message-sdk-sms/src/main/java/com/xiangxi/message/sms/ISmsSender.java) - 短信发送器接口

### 数据模型

- [`SmsResponse`](message-sdk-sms/src/main/java/com/xiangxi/message/sms/model/SmsResponse.java) - 短信发送响应
- [`MessageSentEvent`](message-sdk-logging/src/main/java/com/xiangxi/message/logging/MessageSentEvent.java) - 消息发送事件

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