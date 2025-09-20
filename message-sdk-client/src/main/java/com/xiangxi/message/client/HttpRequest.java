package com.xiangxi.message.client;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    // 请求地址（可以是绝对URL或相对路径）
    private String url;
    // 请求方式
    private HttpMethod method = HttpMethod.GET;
    // 请求头
    private final Map<String, String> headers = new HashMap<>();
    // 可选的 Content-Type（若未设置，则从 headers 中读取；仍未设置则由客户端选择合适默认值）
    private String contentType;
    // 请求体（当 method=POST 等有体时使用；字符串/JSON/表单等由 contentType 指定）
    private String body;
    // 表单字段（application/x-www-form-urlencoded）
    private final Map<String, String> form = new HashMap<>();
    // 文件字段（multipart/form-data）
    private final Map<String, File> files = new HashMap<>();

    public HttpRequest(String url) {
        this.url = url;
    }

    public String getUrl() { return url; }
    public HttpRequest setUrl(String url) { this.url = url; return this; }

    public HttpMethod getMethod() { return method; }
    public HttpRequest setMethod(HttpMethod method) { if (method != null) this.method = method; return this; }

    public Map<String, String> getHeaders() { return headers; }
    public HttpRequest header(String name, String value) { if (name != null && value != null) headers.put(name, value); return this; }
    public HttpRequest headers(Map<String, String> headers) { if (headers != null) this.headers.putAll(headers); return this; }

    public String getBody() { return body; }
    public HttpRequest setBody(String body) { this.body = body; return this; }

    public String getContentType() { return contentType; }
    public HttpRequest setContentType(String contentType) { this.contentType = contentType; return this; }

    public Map<String, String> getForm() { return form; }
    public HttpRequest formField(String name, String value) { if (name != null && value != null) form.put(name, value); return this; }
    public HttpRequest formFields(Map<String, String> fields) { if (fields != null) form.putAll(fields); return this; }

    public Map<String, File> getFiles() { return files; }
    public HttpRequest file(String field, File file) { if (field != null && file != null) files.put(field, file); return this; }

    private Map<String, String> query = new LinkedHashMap<>();

    // 每个请求可覆盖的重试设置
    private Integer retries; // null 表示使用客户端默认
    private Duration retryBackoff; // null 表示使用客户端默认

    // 现有字段与方法保持不变...
    // 追加 getters/setters（链式）

    public Map<String, String> getQuery() {
        return query == null ? Collections.emptyMap() : query;
    }

    public HttpRequest setQuery(Map<String, String> query) {
        if (query == null) {
            this.query = new LinkedHashMap<>();
        } else {
            this.query = new LinkedHashMap<>(query);
        }
        return this;
    }

    public HttpRequest queryParam(String name, String value) {
        if (name != null && value != null) {
            if (this.query == null) this.query = new LinkedHashMap<>();
            this.query.put(name, value);
        }
        return this;
    }

    public HttpRequest queryParams(Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            if (this.query == null) this.query = new LinkedHashMap<>();
            this.query.putAll(params);
        }
        return this;
    }

    public Integer getRetries() { return retries; }
    public HttpRequest setRetries(Integer retries) { this.retries = retries; return this; }

    public Duration getRetryBackoff() { return retryBackoff; }
    public HttpRequest setRetryBackoff(Duration retryBackoff) { this.retryBackoff = retryBackoff; return this; }
}
