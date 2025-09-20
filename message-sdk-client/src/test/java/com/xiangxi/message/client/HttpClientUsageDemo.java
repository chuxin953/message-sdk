package com.xiangxi.message.client;

import java.time.Duration;

public class HttpClientUsageDemo {
    public static void main(String[] args) {
        try {
            // 1) 构建 HttpClient：默认超时、默认重试与退避、默认请求头
            HttpClient client = new HttpClient.Builder()
                    .connectTimeout(5)
                    .readTimeout(5)
                    .writeTimeout(5)
                    .callTimeout(10)
                    .defaultHeader("User-Agent", "message-sdk-client-demo/1.0")
                    .build();

            // 2) 发起一个 GET 请求（按请求覆盖 readTimeout）
            HttpRequest getReq = new HttpRequest("https://httpbin.org/get")
                    .setMethod(HttpMethod.GET)
                    .header("Accept", "application/json");
            HttpResponse getResp = client.doRequest(getReq, HttpResponse.class);
            System.out.println("GET status=" + getResp.statusCode());
            System.out.println("GET body=" + getResp.body());

            // 3) 发起一个 POST JSON 请求（按请求覆盖 callTimeout）
            String json = "{\"hello\":\"world\"}";
            HttpRequest postReq = new HttpRequest("https://httpbin.org/post")
                    .setMethod(HttpMethod.POST)
                    .setContentType("application/json")
                    .setBody(json);
            HttpResponse postResp = client.doRequest(postReq, HttpResponse.class);
            System.out.println("POST status=" + postResp.statusCode());
            System.out.println("POST body=" + postResp.body());

            // 4) 演示一次会触发重试的请求（例如 503），观察重试与退避生效
            // 注：如果你的网络环境受限，可将 URL 改为实际可访问的服务
            HttpRequest retryReq = new HttpRequest("https://httpbin.org/status/503")
                    .setMethod(HttpMethod.GET);
            HttpResponse retryResp = client.doRequest(postReq, HttpResponse.class);
            System.out.println("RETRY DEMO status=" + retryResp.statusCode());
            System.out.println("RETRY DEMO body=" + retryResp.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}