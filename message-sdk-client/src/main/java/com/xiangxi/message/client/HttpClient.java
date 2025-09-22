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
import java.util.function.Supplier;

public class HttpClient {
    private final HttpConnection connection;
    private final Map<String, String> defaultHeaders;
    public static final int HTTP_RSP_OK = 200;


    private HttpClient(Builder builder) {
        this.connection = builder.connectionBuilder.build();
        this.defaultHeaders = new HashMap<>(builder.defaultHeaders);
    }

    public <T> T doRequest(HttpRequest req, ResponseParse<T> parser) throws ClientException, IOException {
        return internalRequest(req, parser);
    }

    protected  <T> T internalRequest(HttpRequest req, ResponseParse<T> parser) throws ClientException {
        try {
            Request request = new  OkHttpRequestAdapter(defaultHeaders).adaptRequest(req);
            Response resp = connection.doRequest(request);
            if (resp.code() != HTTP_RSP_OK) {
                String msg = "response code is " + resp.code() + ", not 200";
                throw new ClientException(msg, "", "ServerSideError");
            }
            return processResponseJson(resp, parser);
        }catch (IOException e) {
            throw new ClientException("", e);
        }
    }

    protected <T> T processResponseJson(Response resp, ResponseParse<T> parser) throws ClientException {
        String body;
        try (ResponseBody responseBody = resp.body()) {
            if (responseBody == null) {
                throw new ClientException("Response body is null");
            }
            body = responseBody.string();
        }catch (IOException e){
            String msg = "Cannot transfer response body to string, because Content-Length is too large, or " +
                    "Content-Length and stream length disagree.";
            throw new ClientException(msg, e);
        }
        return parser.parse(body);
    }


    /**
     * 重试执行指定操作，直到成功或超过最大重试次数。
     *
     * <p>该方法通过传入一个 {@link Supplier} 执行操作，如果操作抛出异常，则等待 1 秒后重试。
     * 重试次数用 {@code retryTimes} 指定。如果超过重试次数仍未成功，则抛出最后一次异常。</p>
     *
     * @param <T> 返回值类型，由操作返回
     * @param action 要执行的操作，使用 {@link Supplier} 表示，可能抛出异常
     * @param retryTimes 最大重试次数（必须大于 0）
     * @return 操作成功返回的结果
     * @throws Exception 当所有重试都失败时抛出最后一次异常
     * @throws InterruptedException 如果重试过程中线程被中断
     *
     * <p>示例用法：</p>
     * <pre>
     * String result = retry(() -> myClient.callSomeApi(request), 3);
     * </pre>
     */
    public   <T> T retry(Supplier<T> action, int retryTimes) throws Exception {
        while (retryTimes-- > 0) {
            try {
                return action.get();
            } catch (Exception e) {
                if (retryTimes == 0) throw e;
                Thread.sleep(1000);
            }
        }
        throw new ClientException("Should not reach here");
    }


    public static class Builder {
        private final HttpConnection.Builder connectionBuilder = new HttpConnection.Builder();
        private final Map<String, String> defaultHeaders = new HashMap<>();

        public Builder defaultHeader(String name, String value) { if (name != null && value != null) defaultHeaders.put(name, value); return this; }
        public Builder defaultHeaders(Map<String, String> headers) { if (headers != null) defaultHeaders.putAll(headers); return this; }

        public Builder connectTimeout(int seconds) { connectionBuilder.connectTimeout(seconds); return this; }
        public Builder readTimeout(int seconds)    { connectionBuilder.readTimeout(seconds); return this; }
        public Builder writeTimeout(int seconds)   { connectionBuilder.writeTimeout(seconds); return this; }
        public Builder callTimeout(int seconds)    { connectionBuilder.callTimeout(seconds); return this; }

        public Builder addInterceptor(Interceptor interceptor) { connectionBuilder.addInterceptor(interceptor); return this; }
        public Builder proxy(Proxy proxy)                      { connectionBuilder.proxy(proxy); return this; }
        public Builder proxyAuthenticator(Authenticator auth)  { connectionBuilder.proxyAuthenticator(auth); return this; }
        public Builder ssl(SSLSocketFactory ssl, X509TrustManager trustManager) { connectionBuilder.ssl(ssl, trustManager); return this; }
        public Builder hostnameVerifier(HostnameVerifier verifier) { connectionBuilder.hostnameVerifier(verifier); return this; }

        public HttpClient build() { return new HttpClient(this); }
    }
}
