package com.xiangxi.message.client.adapter;

import com.xiangxi.message.client.enums.HttpMethod;
import com.xiangxi.message.client.HttpRequest;
import okhttp3.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OkHttpRequestAdapter implements HttpRequestAdapter {

    private final Map<String, String> defaultHeaders;
    public OkHttpRequestAdapter(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders != null ? defaultHeaders : Collections.emptyMap();
    }
    @Override
    public Request adaptRequest(HttpRequest req) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(req.getUrl())).newBuilder();
        if (req.getQuery() != null) {
            for (Map.Entry<String, String> e : req.getQuery().entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    urlBuilder.addQueryParameter(e.getKey(), e.getValue());
                }
            }
        }
        Headers headers = buildHeaders(req.getHeaders());

        Request.Builder rb = new Request.Builder()
                .url(urlBuilder.build())
                .headers(headers);
        // 如果定义了表单或文件，优先走表单/文件构造
        if (!req.getFiles().isEmpty()) {
            rb.method(methodName(req.getMethod()), buildMultipartBody(req.getForm(), req.getFiles()));
        } else if (!req.getForm().isEmpty()) {
            RequestBody formBody = buildFormBody(req.getForm());
            String method = methodName(req.getMethod());
            if ("GET".equals(method) || "HEAD".equals(method) || "DELETE".equals(method)) {
                // 这些方法通常不带 body；如果用户给了 form，则兜底改为 POST
                method = "POST";
            }
            rb.method(method, formBody);
        } else {
            switch (req.getMethod()) {
                case POST -> rb.method("POST", buildRequestBody(req.getBody(), req.getContentType()));
                case PUT -> rb.method("PUT", buildRequestBody(req.getBody(), req.getContentType()));
                case PATCH -> rb.method("PATCH", buildRequestBody(req.getBody(), req.getContentType()));
                case DELETE -> rb.delete();
                case HEAD -> rb.head();
                default -> rb.get();
            }
        }
        return rb.build();
    }

    private Headers buildHeaders(Map<String, String> headers) {
        // 没有传入header 返回默认的headers
        if (headers == null || headers.isEmpty()) {
            return Headers.of(defaultHeaders);
        }
        Map<String, String> mergedHeaders = new HashMap<>(defaultHeaders);
        mergedHeaders.putAll(headers);
        return Headers.of(mergedHeaders);
    }

    private String methodName(HttpMethod method) {
        return method == null ? "GET" : method.name();
    }

    private RequestBody buildRequestBody(String bodyStr, String contentType) {
        if (bodyStr == null) bodyStr = "";
        MediaType mt = null;
        if (contentType != null && !contentType.isEmpty()) {
            mt = MediaType.parse(contentType);
        }
        return RequestBody.create(mt, bodyStr);
    }

    private RequestBody buildFormBody(Map<String, String> form) {
        FormBody.Builder fb = new FormBody.Builder();
        if (form != null) {
            for (Map.Entry<String, String> e : form.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    fb.add(e.getKey(), e.getValue());
                }
            }
        }
        return fb.build();
    }

    private RequestBody buildMultipartBody(Map<String, String> form, Map<String, java.io.File> files) {
        MultipartBody.Builder mb = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (form != null) {
            for (Map.Entry<String, String> e : form.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    mb.addFormDataPart(e.getKey(), e.getValue());
                }
            }
        }
        if (files != null) {
            for (Map.Entry<String, java.io.File> f : files.entrySet()) {
                if (f.getKey() != null && f.getValue() != null) {
                    mb.addFormDataPart(
                            f.getKey(),
                            f.getValue().getName(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), f.getValue())
                    );
                }
            }
        }
        return mb.build();
    }
}
