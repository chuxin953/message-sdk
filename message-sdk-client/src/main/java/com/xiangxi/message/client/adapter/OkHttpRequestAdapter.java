package com.xiangxi.message.client.adapter;

import com.xiangxi.message.client.ClientException;
import com.xiangxi.message.client.HttpRequest;
import com.xiangxi.message.client.enums.HttpContentType;
import com.xiangxi.message.client.enums.HttpMethod;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OkHttp 请求适配器实现
 * <p>
 * 将高层 {@link HttpRequest} 模型转换为 OkHttp 的 {@link Request} 对象。
 * 支持 GET、POST、PUT、DELETE、PATCH、HEAD 等方法，以及表单、文件上传等功能。
 * </p>
 *
 * @author message-sdk
 * @since 1.0.0
 */
public record OkHttpRequestAdapter(Map<String, String> defaultHeaders) implements HttpRequestAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(OkHttpRequestAdapter.class);

    /**
     * 构造函数
     *
     * @param defaultHeaders 默认请求头
     */
    public OkHttpRequestAdapter(Map<String, String> defaultHeaders) {
        this.defaultHeaders = defaultHeaders != null ? defaultHeaders : Collections.emptyMap();
    }

    /**
     * 将 HttpRequest 适配为 OkHttp Request
     *
     * @param req HTTP 请求对象
     * @return OkHttp Request 对象
     * @throws ClientException 如果 URL 无效或构建请求失败
     */
    @Override
    public Request adaptRequest(HttpRequest req) throws ClientException {
        try {
            // 解析和构建 URL
            HttpUrl httpUrl = HttpUrl.parse(req.getUrl());
            if (httpUrl == null) {
                throw new ClientException("Invalid URL: " + req.getUrl());
            }
            
            HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
            if (req.getQuery() != null && !req.getQuery().isEmpty()) {
                for (Map.Entry<String, String> e : req.getQuery().entrySet()) {
                    if (e.getKey() != null && e.getValue() != null) {
                        urlBuilder.addQueryParameter(e.getKey(), e.getValue());
                    }
                }
            }
            
            // 构建请求头
            Headers headers = buildHeaders(req.getHeaders());
            
            // 构建请求
            Request.Builder rb = new Request.Builder()
                    .url(urlBuilder.build())
                    .headers(headers);
            
            // 根据请求类型构建请求体
            if (!req.getFiles().isEmpty()) {
                // 文件上传（multipart/form-data）
                rb.method(methodName(req.getMethod()), buildMultipartBody(req.getForm(), req.getFiles()));
            } else if (!req.getForm().isEmpty()) {
                // 表单提交（application/x-www-form-urlencoded）
                RequestBody formBody = buildFormBody(req.getForm());
                String method = methodName(req.getMethod());
                if ("GET".equals(method) || "HEAD".equals(method) || "DELETE".equals(method)) {
                    // 这些方法通常不带 body；如果用户给了 form，则兜底改为 POST
                    if (logger.isWarnEnabled()) {
                        logger.warn("Form data provided for {} method, changing to POST", method);
                    }
                    method = "POST";
                }
                rb.method(method, formBody);
            } else {
                // 普通请求体（JSON、XML 等）
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
        } catch (ClientException e) {
            throw e;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("Failed to adapt request: {}", e.getMessage(), e);
            }
            throw new ClientException("Failed to build request: " + e.getMessage(), e);
        }
    }

    /**
     * 构建请求头
     * <p>
     * 合并默认请求头和请求特定的请求头，请求特定的请求头会覆盖默认请求头。
     * </p>
     *
     * @param headers 请求特定的请求头
     * @return OkHttp Headers 对象
     */
    private Headers buildHeaders(Map<String, String> headers) {
        // 没有传入 header 返回默认的 headers
        if (headers == null || headers.isEmpty()) {
            return defaultHeaders.isEmpty() ? Headers.of() : Headers.of(defaultHeaders);
        }
        
        // 合并默认请求头和请求特定的请求头
        Map<String, String> mergedHeaders = new HashMap<>(defaultHeaders);
        mergedHeaders.putAll(headers);
        return Headers.of(mergedHeaders);
    }

    /**
     * 获取 HTTP 方法名称
     *
     * @param method HTTP 方法枚举
     * @return HTTP 方法名称字符串
     */
    private String methodName(HttpMethod method) {
        return method == null ? "GET" : method.name();
    }

    /**
     * 构建请求体
     *
     * @param bodyStr     请求体字符串
     * @param contentType Content-Type
     * @return RequestBody 对象
     */
    private RequestBody buildRequestBody(String bodyStr, String contentType) {
        if (bodyStr == null) {
            bodyStr = "";
        }
        
        MediaType mt = null;
        if (contentType != null && !contentType.isEmpty()) {
            try {
                mt = MediaType.parse(contentType);
            } catch (IllegalArgumentException e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Invalid Content-Type: {}, using default", contentType);
                }
            }
        }
        
        return RequestBody.create(bodyStr, mt);
    }

    /**
     * 构建表单请求体（application/x-www-form-urlencoded）
     *
     * @param form 表单字段 Map
     * @return FormBody 对象
     */
    private RequestBody buildFormBody(Map<String, String> form) {
        FormBody.Builder fb = new FormBody.Builder();
        if (form != null && !form.isEmpty()) {
            for (Map.Entry<String, String> e : form.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    fb.add(e.getKey(), e.getValue());
                }
            }
        }
        return fb.build();
    }

    /**
     * 构建多部分请求体（multipart/form-data）
     * <p>
     * 支持同时上传表单字段和文件。
     * 会验证文件是否存在以及是否为文件。
     * </p>
     *
     * @param form  表单字段 Map
     * @param files 文件 Map
     * @return MultipartBody 对象
     * @throws ClientException 如果文件不存在、不是文件或添加文件失败
     */
    private RequestBody buildMultipartBody(Map<String, String> form, Map<String, File> files) throws ClientException {
        MultipartBody.Builder mb = new MultipartBody.Builder().setType(MultipartBody.FORM);
        
        // 添加表单字段
        if (form != null && !form.isEmpty()) {
            for (Map.Entry<String, String> e : form.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    mb.addFormDataPart(e.getKey(), e.getValue());
                }
            }
        }
        
        // 添加文件
        if (files != null && !files.isEmpty()) {
            for (Map.Entry<String, File> f : files.entrySet()) {
                if (f.getKey() != null && f.getValue() != null) {
                    File file = f.getValue();
                    
                    // 验证文件
                    if (!file.exists()) {
                        throw new ClientException("File does not exist: " + file.getAbsolutePath());
                    }
                    if (!file.isFile()) {
                        throw new ClientException("Not a file: " + file.getAbsolutePath());
                    }
                    
                    try {
                        mb.addFormDataPart(
                                f.getKey(),
                                file.getName(),
                                RequestBody.create(file, MediaType.parse(HttpContentType.OCTET_STREAM.value()))
                        );
                    } catch (Exception e) {
                        throw new ClientException("Failed to add file to multipart body: " + e.getMessage(), e);
                    }
                }
            }
        }
        
        return mb.build();
    }
}
