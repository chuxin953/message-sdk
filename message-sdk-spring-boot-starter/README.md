# Message SDK Spring Boot Starter

## 简介

Message SDK Spring Boot Starter 是一个开箱即用的消息发送解决方案，支持多种短信厂商，提供统一的 API 接口。

## 功能特性

- 🚀 **开箱即用**：引入依赖即可使用，无需额外配置
- 🔧 **配置驱动**：通过 `application.yml` 管理所有厂商参数
- 🏢 **多厂商支持**：支持腾讯云、阿里云等主流短信厂商
- 🔍 **自动发现**：基于 SPI 机制自动发现并注册厂商实现
- 🛡️ **配置验证**：启动时自动验证配置完整性
- 📊 **健康检查**：集成 Spring Boot Actuator 健康检查
- 🎯 **统一接口**：屏蔽厂商差异，提供统一的发送接口

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.xiangxi.message</groupId>
    <artifactId>message-sdk-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 配置参数

```yaml
message:
  sdk:
    enabled: true
    default-type: sms
    default-channel: tencent
    events-enabled: true
    logging-enabled: true
  
  # 短信厂商配置
  sms:
    default-vendor: tencent  # 默认厂商
    
    # 腾讯云 SMS 配置
    tencent:
      enabled: true
      secret-id: your-secret-id
      secret-key: your-secret-key
      sdk-app-id: your-sdk-app-id
      region: ap-beijing
      sign-name: your-sign-name
    
    # 阿里云 SMS 配置
    aliyun:
      enabled: true
      access-key-id: your-access-key-id
      access-key-secret: your-access-key-secret
      sign-name: your-sign-name
      region-id: cn-hangzhou
```

### 3. 使用示例

```java
@RestController
public class SmsController {
    
    @Autowired
    private MessageSdkService messageSdkService;
    
    @PostMapping("/send-sms")
    public String sendSms(@RequestParam String phone, 
                         @RequestParam String templateId, 
                         @RequestParam String[] params) {
        try {
            // 使用默认厂商发送短信
            Object result = messageSdkService.sendSms(phone, templateId, params);
            return "发送成功: " + result;
        } catch (Exception e) {
            return "发送失败: " + e.getMessage();
        }
    }
    
    @PostMapping("/send-sms/{vendor}")
    public String sendSmsWithVendor(@PathVariable String vendor,
                                   @RequestParam String phone, 
                                   @RequestParam String templateId, 
                                   @RequestParam String[] params) {
        try {
            // 使用指定厂商发送短信
            Object result = messageSdkService.sendSms(vendor, phone, templateId, params);
            return "发送成功: " + result;
        } catch (Exception e) {
            return "发送失败: " + e.getMessage();
        }
    }
}
```

## 配置说明

### 基础配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `message.sdk.enabled` | boolean | true | 是否启用 Message SDK |
| `message.sdk.default-type` | string | sms | 默认消息类型 |
| `message.sdk.default-channel` | string | tencent | 默认渠道 |
| `message.sdk.events-enabled` | boolean | true | 是否启用事件发布 |
| `message.sdk.logging-enabled` | boolean | true | 是否启用日志记录 |

### 腾讯云 SMS 配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `message.sms.tencent.enabled` | boolean | true | 是否启用腾讯云短信 |
| `message.sms.tencent.secret-id` | string | - | 腾讯云 SecretId |
| `message.sms.tencent.secret-key` | string | - | 腾讯云 SecretKey |
| `message.sms.tencent.sdk-app-id` | string | - | 腾讯云 SDK AppId |
| `message.sms.tencent.region` | string | ap-beijing | 腾讯云区域 |
| `message.sms.tencent.sign-name` | string | - | 短信签名 |

### 阿里云 SMS 配置

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `message.sms.aliyun.enabled` | boolean | true | 是否启用阿里云短信 |
| `message.sms.aliyun.access-key-id` | string | - | 阿里云 AccessKeyId |
| `message.sms.aliyun.access-key-secret` | string | - | 阿里云 AccessKeySecret |
| `message.sms.aliyun.sign-name` | string | - | 短信签名 |
| `message.sms.aliyun.region-id` | string | cn-hangzhou | 阿里云区域ID |

## API 接口

### MessageSdkService

#### 发送短信（默认厂商）

```java
public Object sendSms(String phoneNumber, String templateId, String[] templateParams)
```

#### 发送短信（指定厂商）

```java
public Object sendSms(String vendor, String phoneNumber, String templateId, String[] templateParams)
```

#### 发送腾讯云短信

```java
public Object sendTencentSms(String phoneNumber, String templateId, String[] templateParams)
```

#### 发送阿里云短信

```java
public Object sendAliyunSms(String phoneNumber, String templateCode, String templateParamJson)
```

## 健康检查

### 1. Spring Boot Actuator 健康检查

如果项目中集成了 Spring Boot Actuator，可以通过以下端点检查 Message SDK 状态：

```
GET /actuator/health
```

健康检查会返回：
- 启用的厂商列表
- 已注册的发送器
- 配置状态

### 2. 自定义状态检查端点

如果项目集成了 Spring Web，可以通过以下端点检查状态：

```
GET /message-sdk/status    # 详细状态信息
GET /message-sdk/health    # 健康状态
GET /message-sdk/summary   # 状态摘要
```

### 3. 编程式状态检查

```java
@Autowired
private MessageSdkStatusChecker statusChecker;

// 检查是否健康
boolean isHealthy = statusChecker.isHealthy();

// 获取详细状态
Map<String, Object> status = statusChecker.checkStatus();

// 获取状态摘要
String summary = statusChecker.getStatusSummary();
```

## 故障排除

### 常见问题

1. **启动失败：没有启用的短信厂商**
   - 检查 `message.sms.*.enabled` 配置
   - 确保至少有一个厂商被启用

2. **启动失败：配置不完整**
   - 检查必填的厂商参数是否已配置
   - 腾讯云需要：secret-id、secret-key、sdk-app-id
   - 阿里云需要：access-key-id、access-key-secret

3. **发送失败：厂商未启用**
   - 检查对应厂商的 `enabled` 配置
   - 确保配置参数正确

### 调试模式

启用调试日志：

```yaml
logging:
  level:
    com.xiangxi.message: DEBUG
```

## 扩展开发

### 添加新厂商

1. 实现 `MessageSender` 接口
2. 在 `META-INF/services/com.xiangxi.message.api.MessageSender` 中注册
3. 在 `SmsVendorProperties` 中添加配置类
4. 在 `SmsConfigManager` 中添加配置获取方法
5. 在 `MessageSdkService` 中添加发送方法

## 许可证

MIT License
