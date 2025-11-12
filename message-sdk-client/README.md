# Message SDK Client

HTTP 客户端封装模块，提供简洁易用的 API，支持各种 HTTP 请求场景。

## 📋 目录

- [核心特性](#核心特性)
- [快速开始](#快速开始)
- [详细使用](#详细使用)
- [API 文档](#api-文档)
- [配置说明](#配置说明)
- [最佳实践](#最佳实践)
- [常见问题](#常见问题)

## ✨ 核心特性

- **简洁易用**：提供链式 API，代码简洁清晰
- **企业级封装**：完善的异常处理、日志记录、资源管理
- **灵活重试**：不自动重试，提供手动重试方法，完全由用户控制
- **资源安全**：自动管理 Response 资源，防止资源泄漏
- **大小限制**：支持响应体大小限制，防止内存溢出
- **类型安全**：完整的类型支持，编译期检查
- **线程安全**：所有方法都是线程安全的，支持高并发

## 🚀 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>com.xiangxi.message</groupId>
    <artifactId>message-sdk-client</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 基本使用

```java
// 1. 创建 HTTP 客户端
HttpClient client = new HttpClient.Builder()
    .connectTimeout(10)      // 连接超时 10 秒
    .readTimeout(30)         // 读取超时 30 秒
    .defaultHeader("User-Agent", "message-sdk/1.0")
    .build();

// 2. 构建请求
HttpRequest request = HttpRequest.builder("https://api.example.com/data")
    .method(HttpMethod.GET)
    .queryParam("page", "1")
    .queryParam("size", "10")
    .build();

// 3. 发送请求并解析响应
String response = client.doRequest(request, body -> body);
```

## 📖 详细使用

### 创建客户端

```java
HttpClient client = new HttpClient.Builder()
    // 超时配置
    .connectTimeout(10)      // 连接超时（秒）
    .readTimeout(30)        // 读取超时（秒）
    .writeTimeout(30)       // 写入超时（秒）
    .callTimeout(60)        // 调用超时（秒）
    
    // 默认请求头
    .defaultHeader("User-Agent", "message-sdk/1.0")
    .defaultHeader("Accept", "application/json")
    .defaultHeaders(headersMap)  // 批量添加
    
    // 响应体大小限制（默认 10MB）
    .maxResponseBodySize(20 * 1024 * 1024)  // 20MB
    
    // SSL 配置
    .ssl(sslSocketFactory, trustManager)
    .hostnameVerifier(hostnameVerifier)
    
    // 代理配置
    .proxy(proxy)
    .proxyAuthenticator(authenticator)
    
    // OkHttp 拦截器
    .addInterceptor(interceptor)
    
    .build();
```

### GET 请求

```java
// 简单 GET 请求
HttpRequest request = HttpRequest.builder("https://api.example.com/users")
    .method(HttpMethod.GET)
    .build();

// 带查询参数的 GET 请求
HttpRequest request = HttpRequest.builder("https://api.example.com/users")
    .method(HttpMethod.GET)
    .queryParam("page", "1")
    .queryParam("size", "10")
    .queryParam("status", "active")
    .build();

// 带请求头的 GET 请求
HttpRequest request = HttpRequest.builder("https://api.example.com/users")
    .method(HttpMethod.GET)
    .header("Authorization", "Bearer token")
    .header("X-Request-ID", UUID.randomUUID().toString())
    .build();

// 发送请求
String response = client.doRequest(request, body -> body);
```

### POST JSON 请求

```java
// 构建 JSON 请求
String jsonBody = "{\"name\":\"John\",\"age\":30}";
HttpRequest request = HttpRequest.builder("https://api.example.com/users")
    .method(HttpMethod.POST)
    .contentType(HttpContentType.JSON)
    .body(jsonBody)
    .header("Authorization", "Bearer token")
    .build();

// 发送请求并解析为对象
UserResponse response = client.doRequest(request, body -> {
    Gson gson = new Gson();
    return gson.fromJson(body, UserResponse.class);
});
```

### POST 表单请求

```java
// 表单提交
HttpRequest request = HttpRequest.builder("https://api.example.com/login")
    .method(HttpMethod.POST)
    .formField("username", "admin")
    .formField("password", "secret")
    .build();

String response = client.doRequest(request, body -> body);
```

### 文件上传

```java
// 单文件上传
File file = new File("/path/to/avatar.jpg");
HttpRequest request = HttpRequest.builder("https://api.example.com/upload")
    .method(HttpMethod.POST)
    .file("avatar", file)  // 会自动验证文件是否存在
    .formField("userId", "123")
    .build();

String response = client.doRequest(request, body -> body);

// 多文件上传
HttpRequest request = HttpRequest.builder("https://api.example.com/upload")
    .method(HttpMethod.POST)
    .file("avatar", new File("/path/to/avatar.jpg"))
    .file("certificate", new File("/path/to/cert.pdf"))
    .formField("userId", "123")
    .build();
```

### PUT/PATCH/DELETE 请求

```java
// PUT 请求
HttpRequest request = HttpRequest.builder("https://api.example.com/users/123")
    .method(HttpMethod.PUT)
    .contentType(HttpContentType.JSON)
    .body("{\"name\":\"John Updated\"}")
    .build();

// PATCH 请求
HttpRequest request = HttpRequest.builder("https://api.example.com/users/123")
    .method(HttpMethod.PATCH)
    .contentType(HttpContentType.JSON)
    .body("{\"age\":31}")
    .build();

// DELETE 请求
HttpRequest request = HttpRequest.builder("https://api.example.com/users/123")
    .method(HttpMethod.DELETE)
    .build();
```

### 手动重试

```java
// 方式1：使用默认延迟（1秒）
String result = client.retry(() -> 
    client.doRequest(request, body -> body), 
    3  // 最多重试3次
);

// 方式2：自定义延迟时间
String result = client.retry(() -> 
    client.doRequest(request, body -> body), 
    5,      // 最多重试5次
    2000    // 每次重试间隔2秒
);

// 方式3：带异常处理的重试
try {
    String result = client.retry(() -> 
        client.doRequest(request, body -> body), 
        3
    );
} catch (ClientException e) {
    if (e.isNetworkError()) {
        // 处理网络错误
    } else if (e.isTimeoutError()) {
        // 处理超时错误
    }
}
```

### 自定义响应解析

```java
// 使用 Gson 解析 JSON
ResponseParse<UserResponse> parser = body -> {
    Gson gson = new Gson();
    return gson.fromJson(body, UserResponse.class);
};

UserResponse response = client.doRequest(request, parser);

// 使用 Jackson 解析 JSON
ResponseParse<UserResponse> parser = body -> {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(body, UserResponse.class);
};

// 直接返回字符串
String response = client.doRequest(request, body -> body);

// 返回字节数组
byte[] response = client.doRequest(request, body -> body.getBytes(StandardCharsets.UTF_8));
```

## 📚 API 文档

### HttpClient

#### 主要方法

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `doRequest(HttpRequest, ResponseParse<T>)` | 发送请求并解析响应 | `T` |
| `retry(RetryableAction<T>, int)` | 重试执行操作（默认延迟1秒） | `T` |
| `retry(RetryableAction<T>, int, long)` | 重试执行操作（自定义延迟） | `T` |
| `getDefaultHeaders()` | 获取默认请求头 | `Map<String, String>` |

#### Builder 方法

| 方法 | 说明 | 默认值 |
|------|------|--------|
| `connectTimeout(int)` | 连接超时（秒） | - |
| `readTimeout(int)` | 读取超时（秒） | - |
| `writeTimeout(int)` | 写入超时（秒） | - |
| `callTimeout(int)` | 调用超时（秒） | - |
| `defaultHeader(String, String)` | 添加默认请求头 | - |
| `defaultHeaders(Map)` | 批量添加默认请求头 | - |
| `maxResponseBodySize(long)` | 最大响应体大小（字节） | 10MB |
| `proxy(Proxy)` | 设置代理 | - |
| `ssl(SSLSocketFactory, X509TrustManager)` | SSL 配置 | - |
| `hostnameVerifier(HostnameVerifier)` | 主机名验证器 | - |
| `addInterceptor(Interceptor)` | 添加 OkHttp 拦截器 | - |

### HttpRequest

#### 主要方法

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `getUrl()` | 获取请求 URL | `String` |
| `getMethod()` | 获取 HTTP 方法 | `HttpMethod` |
| `getHeaders()` | 获取请求头 | `Map<String, String>` |
| `getBody()` | 获取请求体 | `String` |
| `getQuery()` | 获取查询参数 | `Map<String, String>` |
| `getForm()` | 获取表单字段 | `Map<String, String>` |
| `getFiles()` | 获取文件 | `Map<String, File>` |

#### Builder 方法

| 方法 | 说明 |
|------|------|
| `url(String)` | 设置请求 URL（必填） |
| `method(HttpMethod)` | 设置 HTTP 方法 | 默认 GET |
| `header(String, String)` | 添加请求头 |
| `headers(Map)` | 批量添加请求头 |
| `contentType(String)` | 设置 Content-Type |
| `contentType(HttpContentType)` | 设置 Content-Type（枚举） |
| `body(String)` | 设置请求体 |
| `queryParam(String, String)` | 添加查询参数 |
| `queryParams(Map)` | 批量添加查询参数 |
| `formField(String, String)` | 添加表单字段 |
| `formFields(Map)` | 批量添加表单字段 |
| `file(String, File)` | 添加文件（会自动验证） |
| `retries(Integer)` | 设置重试次数 |
| `retryBackoff(Duration)` | 设置重试退避时间 |

### ClientException

统一异常类，包含以下信息：

- `getMessage()` - 错误消息
- `getErrorCode()` - 错误码
- `getRequestId()` - 请求 ID
- `getHttpStatusCode()` - HTTP 状态码
- `isNetworkError()` - 是否为网络错误
- `isTimeoutError()` - 是否为超时错误
- `isHttpError()` - 是否为 HTTP 错误

## ⚙️ 配置说明

### 超时配置

```java
HttpClient client = new HttpClient.Builder()
    .connectTimeout(10)   // 连接超时：建立连接的最大等待时间
    .readTimeout(30)      // 读取超时：读取数据的最大等待时间
    .writeTimeout(30)     // 写入超时：写入数据的最大等待时间
    .callTimeout(60)      // 调用超时：整个请求的最大执行时间
    .build();
```

### 响应体大小限制

```java
HttpClient client = new HttpClient.Builder()
    .maxResponseBodySize(20 * 1024 * 1024)  // 20MB
    .build();
```

如果响应体超过限制，会抛出 `ClientException`，防止内存溢出。

### SSL 配置

```java
// 自定义 SSL
SSLContext sslContext = SSLContext.getInstance("TLS");
sslContext.init(keyManagers, trustManagers, new SecureRandom());
SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

HttpClient client = new HttpClient.Builder()
    .ssl(sslSocketFactory, trustManager)
    .hostnameVerifier((hostname, session) -> true)  // 自定义验证逻辑
    .build();
```

### 代理配置

```java
Proxy proxy = new Proxy(Proxy.Type.HTTP, 
    new InetSocketAddress("proxy.example.com", 8080));

HttpClient client = new HttpClient.Builder()
    .proxy(proxy)
    .proxyAuthenticator((route, response) -> {
        String credential = Credentials.basic("username", "password");
        return response.request().newBuilder()
            .header("Proxy-Authorization", credential)
            .build();
    })
    .build();
```

## 💡 最佳实践

### 1. 客户端复用

```java
// ✅ 推荐：创建单例客户端，复用连接池
public class ApiClient {
    private static final HttpClient CLIENT = new HttpClient.Builder()
        .connectTimeout(10)
        .readTimeout(30)
        .defaultHeader("User-Agent", "my-app/1.0")
        .build();
    
    public String getData(String url) throws ClientException {
        HttpRequest request = HttpRequest.builder(url)
            .method(HttpMethod.GET)
            .build();
        return CLIENT.doRequest(request, body -> body);
    }
}

// ❌ 不推荐：每次请求都创建新客户端
public String getData(String url) throws ClientException {
    HttpClient client = new HttpClient.Builder().build();  // 浪费资源
    // ...
}
```

### 2. 异常处理

```java
try {
    String response = client.doRequest(request, body -> body);
} catch (ClientException e) {
    if (e.isNetworkError()) {
        // 网络错误：连接失败、超时等
        logger.error("Network error: {}", e.getMessage());
    } else if (e.isTimeoutError()) {
        // 超时错误
        logger.error("Timeout error: {}", e.getMessage());
    } else if (e.isHttpError()) {
        // HTTP 错误：4xx, 5xx
        int statusCode = e.getHttpStatusCode();
        logger.error("HTTP error {}: {}", statusCode, e.getMessage());
    } else {
        // 其他错误
        logger.error("Request failed: {}", e.getMessage(), e);
    }
}
```

### 3. 重试策略

```java
// 对于网络错误，使用重试
try {
    String result = client.retry(() -> 
        client.doRequest(request, body -> body), 
        3,  // 重试3次
        1000  // 每次间隔1秒
    );
} catch (ClientException e) {
    // 所有重试都失败
    logger.error("Request failed after retries: {}", e.getMessage());
}

// 对于业务错误（如 400 Bad Request），不要重试
// 直接处理异常即可
```

### 4. 响应解析

```java
// ✅ 推荐：使用类型安全的解析器
ResponseParse<UserResponse> parser = body -> {
    Gson gson = new Gson();
    return gson.fromJson(body, UserResponse.class);
};
UserResponse response = client.doRequest(request, parser);

// ✅ 推荐：处理解析异常
ResponseParse<UserResponse> parser = body -> {
    try {
        Gson gson = new Gson();
        return gson.fromJson(body, UserResponse.class);
    } catch (JsonSyntaxException e) {
        throw new ClientException("Invalid JSON response: " + e.getMessage(), e);
    }
};
```

### 5. 请求构建

```java
// ✅ 推荐：使用 Builder 模式，代码清晰
HttpRequest request = HttpRequest.builder("https://api.example.com/users")
    .method(HttpMethod.POST)
    .contentType(HttpContentType.JSON)
    .body(jsonBody)
    .header("Authorization", "Bearer " + token)
    .build();

// ✅ 推荐：复用请求构建逻辑
private HttpRequest buildUserRequest(String userId) {
    return HttpRequest.builder("https://api.example.com/users/" + userId)
        .method(HttpMethod.GET)
        .header("Authorization", "Bearer " + getToken())
        .build();
}
```

## ❓ 常见问题

### Q1: 如何处理大文件上传？

A: 使用 `file()` 方法添加文件，会自动处理：

```java
File largeFile = new File("/path/to/large-file.zip");
HttpRequest request = HttpRequest.builder("https://api.example.com/upload")
    .method(HttpMethod.POST)
    .file("file", largeFile)
    .build();
```

### Q2: 如何设置请求超时？

A: 在创建客户端时设置：

```java
HttpClient client = new HttpClient.Builder()
    .connectTimeout(10)   // 连接超时
    .readTimeout(30)     // 读取超时
    .callTimeout(60)      // 总超时
    .build();
```

### Q3: 如何自定义重试逻辑？

A: 使用 `retry()` 方法，完全由你控制：

```java
// 指数退避重试
int maxRetries = 5;
long delay = 1000;
for (int i = 0; i <= maxRetries; i++) {
    try {
        return client.doRequest(request, parser);
    } catch (ClientException e) {
        if (i < maxRetries && e.isNetworkError()) {
            Thread.sleep(delay);
            delay *= 2;  // 指数退避
            continue;
        }
        throw e;
    }
}
```

### Q4: 响应体太大怎么办？

A: 调整最大响应体大小限制：

```java
HttpClient client = new HttpClient.Builder()
    .maxResponseBodySize(50 * 1024 * 1024)  // 50MB
    .build();
```

或者使用流式处理（需要直接使用 OkHttp）。

### Q5: 如何添加请求日志？

A: 使用 OkHttp 拦截器：

```java
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
logging.setLevel(HttpLoggingInterceptor.Level.BODY);

HttpClient client = new HttpClient.Builder()
    .addInterceptor(logging)
    .build();
```

### Q6: 线程安全吗？

A: 是的，`HttpClient` 和 `HttpRequest` 都是线程安全的，可以在多线程环境中安全使用。

## 📝 完整示例

```java
public class ApiService {
    private final HttpClient client;
    private final Gson gson = new Gson();
    
    public ApiService() {
        this.client = new HttpClient.Builder()
            .connectTimeout(10)
            .readTimeout(30)
            .defaultHeader("User-Agent", "my-app/1.0")
            .maxResponseBodySize(10 * 1024 * 1024)
            .build();
    }
    
    public User getUser(String userId) throws ClientException {
        HttpRequest request = HttpRequest.builder("https://api.example.com/users/" + userId)
            .method(HttpMethod.GET)
            .header("Authorization", "Bearer " + getToken())
            .build();
        
        ResponseParse<User> parser = body -> gson.fromJson(body, User.class);
        
        // 带重试
        return client.retry(() -> client.doRequest(request, parser), 3);
    }
    
    public User createUser(User user) throws ClientException {
        String jsonBody = gson.toJson(user);
        HttpRequest request = HttpRequest.builder("https://api.example.com/users")
            .method(HttpMethod.POST)
            .contentType(HttpContentType.JSON)
            .body(jsonBody)
            .header("Authorization", "Bearer " + getToken())
            .build();
        
        ResponseParse<User> parser = body -> gson.fromJson(body, User.class);
        return client.doRequest(request, parser);
    }
    
    private String getToken() {
        // 获取认证 token
        return "your-token";
    }
}
```

## 🔗 相关链接

- [项目主页](../../README.md)
- [API 文档](../)
- [示例代码](../../message-sdk-samples/)

## 📄 许可证

MIT License

