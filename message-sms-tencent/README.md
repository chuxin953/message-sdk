# 腾讯云短信模块使用指南

## 概述

腾讯云短信模块提供了统一的发送器：

1. **TencentSmsSender** - 支持两种使用方式：
   - 传统方式：使用 `TencentSmsMessage` 和 `TencentSmsApiResponse`
   - 现代方式：使用新的 `SmsRequest` 和 `SmsResponse`

## 现代方式特性

### 支持多接收人
- 单个接收人：`SmsRequest.of(phoneNumber, templateId, params)`
- 多个接收人：`SmsRequest.of(phoneNumbers, templateId, params)`

### 自动手机号格式化
- 自动添加国际区号（+86）
- 支持已包含国际区号的手机号

### 统一参数处理
- 所有接收人使用相同的模板参数
- 参数按名称排序，确保顺序一致

## 使用示例

### 1. 基本使用

```java
// 创建配置
TencentSmsConfig config = new TencentSmsConfig.Builder()
    .secretId("your-secret-id")
    .secretKey("your-secret-key")
    .sdkAppId("your-sdk-app-id")
    .signName("您的签名")
    .region("ap-beijing")
    .build();

// 创建发送器
TencentSmsSender sender = new TencentSmsSender();

// 发送单个接收人短信
SmsRequest request = SmsRequest.of(
    "13800138000",
    "SMS_123456",
    Map.of("code", "1234", "product", "SDK")
);

SmsResponse response = sender.sendSms(config, request);
```

### 2. 多接收人发送

```java
// 多个接收人（相同参数）
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

SmsResponse response = sender.sendSms(config, request);
```


### 4. 使用 Builder 创建复杂请求

```java
// 使用 Builder 创建复杂请求
SmsRequest request = SmsRequest.builder()
    .phoneNumbers(phoneNumbers)
    .templateId("SMS_123456")
    .templateParams(Map.of("code", "1234", "product", "SDK"))
    .signName("您的签名")
    .addProperty("priority", "high")
    .build();

SmsResponse response = sender.sendSms(config, request);
```

### 5. 使用适配器

```java
// 将 SmsRequest 转换为 TencentSmsMessage
TencentSmsMessage tencentMessage = SmsRequestAdapter.toTencentSmsMessage(request);
```

## 配置说明

### TencentSmsConfig 参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| secretId | String | 是 | 腾讯云 SecretId |
| secretKey | String | 是 | 腾讯云 SecretKey |
| sdkAppId | String | 是 | 短信应用ID |
| signName | String | 是 | 短信签名 |
| region | String | 否 | 地域，默认 ap-beijing |

### 手机号格式

- 支持格式：`13800138000` 或 `+8613800138000`
- 自动添加国际区号：`13800138000` → `+8613800138000`
- 已包含国际区号：`+8613800138000` 保持不变

## 注意事项

1. **手机号数量限制**：单次发送最多支持200个手机号
2. **参数顺序**：模板参数会按参数名排序，确保顺序一致
3. **错误处理**：使用统一的 `MessageSendException` 进行错误处理
4. **统一参数**：所有接收人使用相同的模板参数

## 迁移指南

### 从传统发送器迁移

```java
// 传统方式
TencentSmsMessage message = TencentSmsMessage.builder()
    .addPhone("+8613800138000")
    .addParam("1234")
    .templateId("SMS_123456")
    .build();

TencentSmsResponse response = oldSender.send(config, message);

// 现代方式
SmsRequest request = SmsRequest.of(
    "13800138000",  // 自动添加+86
    "SMS_123456",
    Map.of("param1", "1234")
);

SmsResponse response = modernSender.send(config, request);
```

### 批量发送迁移

```java
// 传统方式
TencentSmsMessage message = TencentSmsMessage.builder()
    .addPhones(Arrays.asList("+8613800138000", "+8613800138001"))
    .addParams(Arrays.asList("1234", "SDK"))
    .templateId("SMS_123456")
    .build();

// 现代方式
SmsRequest request = SmsRequest.of(
    Arrays.asList("13800138000", "13800138001"),  // 自动添加+86
    "SMS_123456",
    Map.of("code", "1234", "product", "SDK")
);
```

## 性能优化

1. **连接池**：使用 HttpClient 的连接池功能
2. **异步发送**：支持异步批量发送
3. **错误重试**：内置重试机制
4. **参数验证**：提前验证参数，减少无效请求

## 扩展功能

1. **自定义签名**：支持在请求中指定签名
2. **扩展参数**：支持添加自定义参数
3. **事件监听**：支持发送事件监听
4. **监控指标**：提供发送统计和监控
