package com.xiangxi.message.client;

import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Proxy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public record HttpConnection (OkHttpClient client){
    public Response doRequest(Request request) throws IOException {
        return this.client.newCall(request).execute();
    }
    public Response doRequest(Request request, OkHttpClient customizerClient) throws IOException {
        return customizerClient.newCall(request).execute();
    }

    public Response getRequest(String url) throws ClientException, IOException{
        Request request = null;
        try {
            request = (new Request.Builder()).url(url).get().build();
        } catch (IllegalArgumentException e) {
            throw new ClientException(e.getClass().getName() + "-" + e.getMessage());
        }
        return this.doRequest(request);
    }

    public Response getRequest(String url, Headers headers) throws ClientException, IOException {
        Request request = null;
        try {
            request = (new Request.Builder()).url(url).headers(headers).get().build();
        } catch (IllegalArgumentException e) {
            throw new ClientException(e.getClass().getName() + "-" + e.getMessage());
        }
        return this.doRequest(request);
    }

    public Response postRequest(String url, String body) throws ClientException, IOException {
        MediaType contentType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = null;
        try {
            request = (new Request.Builder()).url(url).post(RequestBody.create(body,contentType)).build();
        } catch (IllegalArgumentException e) {
            throw new ClientException(e.getClass().getName() + "-" + e.getMessage());
        }
        return this.doRequest(request);
    }

    public Response postRequest(String url, String body, Headers headers) throws ClientException, IOException {
        return postRequestInternal(url, headers, RequestBody.create(body, MediaType.parse(Objects.requireNonNull(headers.get("Content-Type")))));
    }

    public Response postRequest(String url, byte[] body, Headers headers) throws ClientException, IOException {
        return postRequestInternal(url, headers, RequestBody.create(body, MediaType.parse(Objects.requireNonNull(headers.get("Content-Type")))));
    }

    public Response postRequestInternal(String url, Headers headers, RequestBody requestBody) throws ClientException, IOException {
        Request request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .headers(headers)
                    .build();
        } catch (IllegalArgumentException e) {
            throw new ClientException(e.getClass().getName() + "-" + e.getMessage());
        }
        return doRequest(request);
    }


    // -------------------- Builder --------------------
    public static class Builder {
        private final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        public Builder connectTimeout(int seconds) { clientBuilder.connectTimeout(seconds, TimeUnit.SECONDS); return this; }
        public Builder readTimeout(int seconds)    { clientBuilder.readTimeout(seconds, TimeUnit.SECONDS); return this; }
        public Builder writeTimeout(int seconds)   { clientBuilder.writeTimeout(seconds, TimeUnit.SECONDS); return this; }
        public Builder callTimeout(int seconds)    { clientBuilder.callTimeout(seconds, TimeUnit.SECONDS); return this; }

        public Builder addInterceptor(Interceptor interceptor) { clientBuilder.addInterceptor(interceptor); return this; }
        public Builder proxy(Proxy proxy)                      { clientBuilder.proxy(proxy); return this; }
        public Builder proxyAuthenticator(Authenticator auth)  { clientBuilder.proxyAuthenticator(auth); return this; }
        public Builder ssl(SSLSocketFactory ssl, X509TrustManager trustManager) { clientBuilder.sslSocketFactory(ssl, trustManager); return this; }
        public Builder hostnameVerifier(HostnameVerifier verifier) { clientBuilder.hostnameVerifier(verifier); return this; }

        public HttpConnection build() { return new HttpConnection(clientBuilder.build()); }
    }
}
