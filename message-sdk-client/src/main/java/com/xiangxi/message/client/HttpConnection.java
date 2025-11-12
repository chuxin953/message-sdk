package com.xiangxi.message.client;

import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Proxy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP 连接封装类
 * <p>
 * 基于 OkHttp 封装 HTTP 连接，提供统一的请求执行接口。
 * 注意：Response 对象使用后需要关闭，建议使用 try-with-resources 语句。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * HttpConnection connection = new HttpConnection.Builder()
 *     .connectTimeout(10)
 *     .readTimeout(30)
 *     .build();
 *
 * Request request = new Request.Builder()
 *     .url("https://api.example.com/data")
 *     .build();
 *
 * try (Response response = connection.doRequest(request)) {
 *     // 处理响应
 *     String body = response.body().string();
 * }
 * }</pre>
 *
 * @author message-sdk
 * @since 1.0.0
 */
public record HttpConnection(OkHttpClient client) {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpConnection.class);
    
    /**
     * 执行 HTTP 请求
     * <p>
     * <strong>重要：</strong>返回的 Response 对象必须关闭，建议使用 try-with-resources 语句。
     * </p>
     *
     * @param request 请求对象
     * @return 响应对象
     * @throws IOException 网络错误或 IO 错误
     */
    public Response doRequest(Request request) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Executing request: {} {}", request.method(), request.url());
        }
        return this.client.newCall(request).execute();
    }
    
    /**
     * 使用自定义客户端执行 HTTP 请求
     * <p>
     * <strong>重要：</strong>返回的 Response 对象必须关闭，建议使用 try-with-resources 语句。
     * </p>
     *
     * @param request         请求对象
     * @param customizerClient 自定义客户端
     * @return 响应对象
     * @throws IOException 网络错误或 IO 错误
     */
    public Response doRequest(Request request, OkHttpClient customizerClient) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Executing request with custom client: {} {}", request.method(), request.url());
        }
        return customizerClient.newCall(request).execute();
    }

    /**
     * 执行 GET 请求
     * <p>
     * <strong>重要：</strong>返回的 Response 对象必须关闭，建议使用 try-with-resources 语句。
     * </p>
     *
     * @param url 请求 URL
     * @return 响应对象
     * @throws ClientException URL 无效时抛出
     * @throws IOException     网络错误或 IO 错误
     */
    public Response getRequest(String url) throws ClientException, IOException {
        Request request;
        try {
            request = new Request.Builder().url(url).get().build();
        } catch (IllegalArgumentException e) {
            throw new ClientException("Invalid URL: " + url, e);
        }
        return this.doRequest(request);
    }

    /**
     * 执行带请求头的 GET 请求
     * <p>
     * <strong>重要：</strong>返回的 Response 对象必须关闭，建议使用 try-with-resources 语句。
     * </p>
     *
     * @param url     请求 URL
     * @param headers 请求头
     * @return 响应对象
     * @throws ClientException URL 无效时抛出
     * @throws IOException     网络错误或 IO 错误
     */
    public Response getRequest(String url, Headers headers) throws ClientException, IOException {
        Request request;
        try {
            request = new Request.Builder().url(url).headers(headers).get().build();
        } catch (IllegalArgumentException e) {
            throw new ClientException("Invalid URL: " + url, e);
        }
        return this.doRequest(request);
    }

    /**
     * 执行 POST 请求（表单格式）
     * <p>
     * <strong>重要：</strong>返回的 Response 对象必须关闭，建议使用 try-with-resources 语句。
     * </p>
     *
     * @param url  请求 URL
     * @param body 请求体（表单格式）
     * @return 响应对象
     * @throws ClientException URL 无效时抛出
     * @throws IOException     网络错误或 IO 错误
     */
    public Response postRequest(String url, String body) throws ClientException, IOException {
        MediaType contentType = MediaType.parse("application/x-www-form-urlencoded");
        Request request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(contentType, body))
                    .build();
        } catch (IllegalArgumentException e) {
            throw new ClientException("Invalid URL or body: " + url, e);
        }
        return this.doRequest(request);
    }

    /**
     * 执行带请求头的 POST 请求
     * <p>
     * <strong>重要：</strong>返回的 Response 对象必须关闭，建议使用 try-with-resources 语句。
     * </p>
     *
     * @param url     请求 URL
     * @param body    请求体
     * @param headers 请求头（必须包含 Content-Type）
     * @return 响应对象
     * @throws ClientException URL 无效或缺少 Content-Type 时抛出
     * @throws IOException     网络错误或 IO 错误
     */
    public Response postRequest(String url, String body, Headers headers) throws ClientException, IOException {
        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            throw new ClientException("Content-Type header is required");
        }
        return postRequestInternal(url, headers, 
                RequestBody.create(MediaType.parse(contentType), body));
    }

    /**
     * 执行带请求头的 POST 请求（字节数组）
     * <p>
     * <strong>重要：</strong>返回的 Response 对象必须关闭，建议使用 try-with-resources 语句。
     * </p>
     *
     * @param url     请求 URL
     * @param body    请求体（字节数组）
     * @param headers 请求头（必须包含 Content-Type）
     * @return 响应对象
     * @throws ClientException URL 无效或缺少 Content-Type 时抛出
     * @throws IOException     网络错误或 IO 错误
     */
    public Response postRequest(String url, byte[] body, Headers headers) throws ClientException, IOException {
        String contentType = headers.get("Content-Type");
        if (contentType == null) {
            throw new ClientException("Content-Type header is required");
        }
        return postRequestInternal(url, headers, 
                RequestBody.create(MediaType.parse(contentType), body));
    }

    /**
     * 内部 POST 请求方法
     *
     * @param url         请求 URL
     * @param headers     请求头
     * @param requestBody 请求体
     * @return 响应对象
     * @throws ClientException URL 无效时抛出
     * @throws IOException     网络错误或 IO 错误
     */
    private Response postRequestInternal(String url, Headers headers, RequestBody requestBody) 
            throws ClientException, IOException {
        Request request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .headers(headers)
                    .build();
        } catch (IllegalArgumentException e) {
            throw new ClientException("Invalid URL: " + url, e);
        }
        return doRequest(request);
    }


    // -------------------- Builder --------------------
    
    /**
     * HttpConnection 构建器
     * <p>
     * 使用 Builder 模式构建 HttpConnection 实例，支持链式调用。
     * </p>
     */
    public static class Builder {
        private final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        /**
         * 设置连接超时时间
         *
         * @param seconds 超时时间（秒）
         * @return 构建器实例
         */
        public Builder connectTimeout(int seconds) {
            if (seconds <= 0) {
                throw new IllegalArgumentException("Connect timeout must be greater than 0");
            }
            clientBuilder.connectTimeout(seconds, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 设置读取超时时间
         *
         * @param seconds 超时时间（秒）
         * @return 构建器实例
         */
        public Builder readTimeout(int seconds) {
            if (seconds <= 0) {
                throw new IllegalArgumentException("Read timeout must be greater than 0");
            }
            clientBuilder.readTimeout(seconds, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 设置写入超时时间
         *
         * @param seconds 超时时间（秒）
         * @return 构建器实例
         */
        public Builder writeTimeout(int seconds) {
            if (seconds <= 0) {
                throw new IllegalArgumentException("Write timeout must be greater than 0");
            }
            clientBuilder.writeTimeout(seconds, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 设置调用超时时间
         *
         * @param seconds 超时时间（秒）
         * @return 构建器实例
         */
        public Builder callTimeout(int seconds) {
            if (seconds <= 0) {
                throw new IllegalArgumentException("Call timeout must be greater than 0");
            }
            clientBuilder.callTimeout(seconds, TimeUnit.SECONDS);
            return this;
        }

        /**
         * 添加拦截器
         *
         * @param interceptor 拦截器
         * @return 构建器实例
         */
        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptor != null) {
                clientBuilder.addInterceptor(interceptor);
            }
            return this;
        }

        /**
         * 设置代理
         *
         * @param proxy 代理对象
         * @return 构建器实例
         */
        public Builder proxy(Proxy proxy) {
            if (proxy != null) {
                clientBuilder.proxy(proxy);
            }
            return this;
        }

        /**
         * 设置代理认证器
         *
         * @param auth 认证器
         * @return 构建器实例
         */
        public Builder proxyAuthenticator(Authenticator auth) {
            if (auth != null) {
                clientBuilder.proxyAuthenticator(auth);
            }
            return this;
        }

        /**
         * 设置 SSL 配置
         *
         * @param ssl          SSL Socket Factory
         * @param trustManager 信任管理器
         * @return 构建器实例
         */
        public Builder ssl(SSLSocketFactory ssl, X509TrustManager trustManager) {
            if (ssl != null && trustManager != null) {
                clientBuilder.sslSocketFactory(ssl, trustManager);
            }
            return this;
        }

        /**
         * 设置主机名验证器
         *
         * @param verifier 验证器
         * @return 构建器实例
         */
        public Builder hostnameVerifier(HostnameVerifier verifier) {
            if (verifier != null) {
                clientBuilder.hostnameVerifier(verifier);
            }
            return this;
        }

        /**
         * 构建 HttpConnection 实例
         *
         * @return HttpConnection 实例
         */
        public HttpConnection build() {
            return new HttpConnection(clientBuilder.build());
        }
    }
}
