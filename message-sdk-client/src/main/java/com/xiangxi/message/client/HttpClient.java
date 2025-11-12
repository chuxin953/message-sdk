package com.xiangxi.message.client;

import com.xiangxi.message.client.adapter.OkHttpRequestAdapter;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 企业级 HTTP 客户端封装
 * <p>
 * 提供简洁的 API，封装常用功能：超时、默认头、异常处理、日志记录。
 * 不自动重试，提供手动重试方法供用户控制。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * // 创建客户端
 * HttpClient client = new HttpClient.Builder()
 *     .connectTimeout(10)
 *     .readTimeout(30)
 *     .defaultHeader("User-Agent", "message-sdk/1.0")
 *     .build();
 *
 * // 发送请求（不重试）
 * HttpRequest request = HttpRequest.builder("https://api.example.com/data")
 *     .method(HttpMethod.GET)
 *     .build();
 * String response = client.doRequest(request, body -> body);
 *
 * // 手动重试
 * String result = client.retry(() -> client.doRequest(request, body -> body), 3);
 * }</pre>
 *
 * @author message-sdk
 * @since 1.0.0
 */
public class HttpClient {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    
    /**
     * HTTP 响应成功状态码
     */
    public static final int HTTP_RSP_OK = 200;
    
    /**
     * 默认最大响应体大小（10MB）
     */
    private static final long DEFAULT_MAX_RESPONSE_BODY_SIZE = 10 * 1024 * 1024;
    
    private final HttpConnection connection;
    private final Map<String, String> defaultHeaders;
    private final OkHttpRequestAdapter requestAdapter;
    private final long maxResponseBodySize;

    /**
     * 私有构造函数，使用 Builder 创建实例
     *
     * @param builder 构建器
     */
    private HttpClient(Builder builder) {
        this.connection = builder.connectionBuilder.build();
        this.defaultHeaders = new HashMap<>(builder.defaultHeaders);
        this.requestAdapter = new OkHttpRequestAdapter(this.defaultHeaders);
        this.maxResponseBodySize = builder.maxResponseBodySize > 0 
            ? builder.maxResponseBodySize 
            : DEFAULT_MAX_RESPONSE_BODY_SIZE;
    }

    /**
     * 发送请求并解析响应
     * <p>
     * 此方法不自动重试，如果请求失败会直接抛出异常。
     * 如需重试，请使用 {@link #retry(Supplier, int)} 方法。
     * </p>
     *
     * @param req     请求对象
     * @param parser  响应解析器（字符串 -> 目标对象）
     * @param <T>     响应类型
     * @return 解析后的响应对象
     * @throws ClientException 网络/服务端/解析异常时抛出
     */
    public <T> T doRequest(HttpRequest req, ResponseParse<T> parser) throws ClientException {
        try {
            // 适配请求
            Request request = requestAdapter.adaptRequest(req);
            
            if (logger.isDebugEnabled()) {
                logger.debug("Sending HTTP request: {} {}", req.getMethod(), req.getUrl());
            }
            
            // 执行请求，使用 try-with-resources 确保 Response 正确关闭
            try (Response resp = connection.doRequest(request)) {
                // 检查响应状态码
                if (resp.code() != HTTP_RSP_OK) {
                    String errorMsg = String.format("HTTP request failed with status code %d: %s", 
                            resp.code(), resp.message());
                    if (logger.isWarnEnabled()) {
                        logger.warn("Request failed: {} - {}", req.getUrl(), errorMsg);
                    }
                    throw new ClientException(errorMsg, "", "HTTP_" + resp.code(), resp.code());
                }
                
                // 读取响应体
                ResponseBody responseBody = resp.body();
                if (responseBody == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Response body is null for request: {}", req.getUrl());
                    }
                    throw new ClientException("Response body is null");
                }
                
                // 检查响应体大小（如果 Content-Length 头存在）
                long contentLength = responseBody.contentLength();
                if (contentLength > 0 && contentLength > maxResponseBodySize) {
                    String msg = String.format("Response body too large: %d bytes (max: %d bytes)", 
                            contentLength, maxResponseBodySize);
                    if (logger.isErrorEnabled()) {
                        logger.error(msg);
                    }
                    throw new ClientException(msg);
                }
                
                // 读取响应体内容（如果 Content-Length 为 -1，会在读取时检查实际大小）
                String body = readResponseBody(responseBody, req.getUrl(), maxResponseBodySize);
                
                // 解析响应
                return parseResponse(body, parser, req.getUrl());
            }
            
        } catch (ClientException e) {
            throw e;
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("Network error for request: {} - {}", req.getUrl(), e.getMessage(), e);
            }
            throw new ClientException("Network error: " + e.getMessage(), e);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Unexpected error for request: {} - {}", req.getUrl(), e.getMessage(), e);
            }
            throw new ClientException("Unexpected error: " + e.getMessage(), e);
        }
    }
    
    /**
     * 读取响应体内容
     *
     * @param responseBody 响应体对象
     * @param url          请求 URL（用于日志）
     * @param maxSize      最大响应体大小（字节）
     * @return 响应体字符串
     * @throws ClientException 如果读取失败或响应体过大
     */
    private String readResponseBody(ResponseBody responseBody, String url, long maxSize) throws ClientException {
        try {
            String body = responseBody.string();
            // 如果 Content-Length 为 -1，读取后检查实际大小
            if (body.length() > maxSize) {
                String msg = String.format("Response body too large: %d bytes (max: %d bytes)", 
                        body.length(), maxSize);
                if (logger.isErrorEnabled()) {
                    logger.error("{} for request: {}", msg, url);
                }
                throw new ClientException(msg);
            }
            return body;
        } catch (IOException e) {
            String msg = "Cannot read response body: " + e.getMessage();
            if (logger.isErrorEnabled()) {
                logger.error("{} for request: {}", msg, url, e);
            }
            throw new ClientException(msg, e);
        }
    }

    /**
     * 解析响应体
     *
     * @param body 响应体字符串
     * @param parser 响应解析器
     * @param url 请求 URL（用于日志）
     * @param <T> 响应类型
     * @return 解析后的响应对象
     * @throws ClientException 如果解析失败
     */
    private <T> T parseResponse(String body, ResponseParse<T> parser, String url) throws ClientException {
        try {
            T result = parser.parse(body);
            if (logger.isDebugEnabled()) {
                logger.debug("Request succeeded: {}", url);
            }
            return result;
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed to parse response body for request: {} - {}", url, e.getMessage(), e);
            }
            throw new ClientException("Failed to parse response: " + e.getMessage(), e);
        }
    }

    /**
     * 重试执行指定操作
     * <p>
     * 该方法会重复执行操作，直到成功或超过最大重试次数。
     * 每次重试前会等待指定时间（默认1秒）。
     * </p>
     *
     * <p>使用示例：</p>
     * <pre>{@code
     * // 重试3次，每次间隔1秒
     * String result = client.retry(() -> 
     *     client.doRequest(request, body -> body), 3);
     *
     * // 重试5次，每次间隔2秒
     * String result = client.retry(() -> 
     *     client.doRequest(request, body -> body), 5, 2000);
     * }</pre>
     *
     * @param action     要执行的操作
     * @param maxRetries 最大重试次数（必须大于 0）
     * @param <T>        返回值类型
     * @return 操作成功返回的结果
     * @throws ClientException 当所有重试都失败时抛出最后一次异常
     * @throws InterruptedException 如果重试过程中线程被中断
     */
    public <T> T retry(RetryableAction<T> action, int maxRetries) 
            throws ClientException, InterruptedException {
        return retry(action, maxRetries, 1000);
    }

    /**
     * 重试执行指定操作
     * <p>
     * 该方法会重复执行操作，直到成功或超过最大重试次数。
     * 每次重试前会等待指定时间。
     * </p>
     *
     * @param action      要执行的操作
     * @param maxRetries  最大重试次数（必须大于 0）
     * @param delayMs     每次重试的延迟时间（毫秒）
     * @param <T>         返回值类型
     * @return 操作成功返回的结果
     * @throws ClientException 当所有重试都失败时抛出最后一次异常
     * @throws InterruptedException 如果重试过程中线程被中断
     */
    public <T> T retry(RetryableAction<T> action, int maxRetries, long delayMs) 
            throws ClientException, InterruptedException {
        if (maxRetries <= 0) {
            throw new IllegalArgumentException("Max retries must be greater than 0");
        }
        if (delayMs < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        
        Exception lastException = null;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Retrying request (attempt {}/{}) after {}ms", 
                                attempt, maxRetries, delayMs);
                    }
                    Thread.sleep(delayMs);
                }
                
                return action.execute();
                
            } catch (ClientException e) {
                lastException = e;
                if (attempt < maxRetries) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Request failed, will retry: {}", e.getMessage());
                    }
                    continue;
                }
                // 最后一次重试失败，抛出异常
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxRetries) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Request failed with exception, will retry: {}", e.getMessage());
                    }
                    continue;
                }
                // 最后一次重试失败，包装为 ClientException 抛出
                throw new ClientException("Request failed after " + (maxRetries + 1) + " attempts", e);
            }
        }
        
        // 理论上不会到达这里
        throw new ClientException("Request failed after " + (maxRetries + 1) + " attempts", 
                lastException != null ? lastException : new Exception("Unknown error"));
    }

    /**
     * 获取默认请求头
     *
     * @return 默认请求头的副本
     */
    public Map<String, String> getDefaultHeaders() {
        return new HashMap<>(defaultHeaders);
    }

    /**
     * HttpClient 构建器
     * <p>
     * 使用 Builder 模式构建 HttpClient 实例，支持链式调用。
     * </p>
     */
    public static class Builder {
        private final HttpConnection.Builder connectionBuilder = new HttpConnection.Builder();
        private final Map<String, String> defaultHeaders = new HashMap<>();
        private long maxResponseBodySize = DEFAULT_MAX_RESPONSE_BODY_SIZE;

        /**
         * 添加默认请求头
         *
         * @param name  请求头名称
         * @param value 请求头值
         * @return 构建器实例
         */
        public Builder defaultHeader(String name, String value) {
            if (name != null && value != null) {
                defaultHeaders.put(name, value);
            }
            return this;
        }

        /**
         * 批量添加默认请求头
         *
         * @param headers 请求头 Map
         * @return 构建器实例
         */
        public Builder defaultHeaders(Map<String, String> headers) {
            if (headers != null) {
                defaultHeaders.putAll(headers);
            }
            return this;
        }

        /**
         * 设置连接超时时间（秒）
         *
         * @param seconds 超时时间
         * @return 构建器实例
         */
        public Builder connectTimeout(int seconds) {
            connectionBuilder.connectTimeout(seconds);
            return this;
        }

        /**
         * 设置读取超时时间（秒）
         *
         * @param seconds 超时时间
         * @return 构建器实例
         */
        public Builder readTimeout(int seconds) {
            connectionBuilder.readTimeout(seconds);
            return this;
        }

        /**
         * 设置写入超时时间（秒）
         *
         * @param seconds 超时时间
         * @return 构建器实例
         */
        public Builder writeTimeout(int seconds) {
            connectionBuilder.writeTimeout(seconds);
            return this;
        }

        /**
         * 设置调用超时时间（秒）
         *
         * @param seconds 超时时间
         * @return 构建器实例
         */
        public Builder callTimeout(int seconds) {
            connectionBuilder.callTimeout(seconds);
            return this;
        }

        /**
         * 添加 OkHttp 拦截器
         *
         * @param interceptor 拦截器
         * @return 构建器实例
         */
        public Builder addInterceptor(Interceptor interceptor) {
            connectionBuilder.addInterceptor(interceptor);
            return this;
        }

        /**
         * 设置代理
         *
         * @param proxy 代理对象
         * @return 构建器实例
         */
        public Builder proxy(Proxy proxy) {
            connectionBuilder.proxy(proxy);
            return this;
        }

        /**
         * 设置代理认证器
         *
         * @param auth 认证器
         * @return 构建器实例
         */
        public Builder proxyAuthenticator(Authenticator auth) {
            connectionBuilder.proxyAuthenticator(auth);
            return this;
        }

        /**
         * 设置 SSL 配置
         *
         * @param ssl           SSL Socket Factory
         * @param trustManager  信任管理器
         * @return 构建器实例
         */
        public Builder ssl(SSLSocketFactory ssl, X509TrustManager trustManager) {
            connectionBuilder.ssl(ssl, trustManager);
            return this;
        }

        /**
         * 设置主机名验证器
         *
         * @param verifier 验证器
         * @return 构建器实例
         */
        public Builder hostnameVerifier(HostnameVerifier verifier) {
            connectionBuilder.hostnameVerifier(verifier);
            return this;
        }

        /**
         * 设置最大响应体大小（字节）
         * <p>
         * 如果响应体超过此大小，将抛出异常，防止内存溢出。
         * 默认值为 10MB。
         * </p>
         *
         * @param maxSize 最大响应体大小（字节），0 表示使用默认值
         * @return 构建器实例
         */
        public Builder maxResponseBodySize(long maxSize) {
            if (maxSize < 0) {
                throw new IllegalArgumentException("Max response body size must be non-negative");
            }
            this.maxResponseBodySize = maxSize;
            return this;
        }

        /**
         * 构建 HttpClient 实例
         *
         * @return HttpClient 实例
         */
        public HttpClient build() {
            return new HttpClient(this);
        }
    }
}
