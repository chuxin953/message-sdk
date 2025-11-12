package com.xiangxi.message.client;

import java.io.Serial;

/**
 * HTTP 客户端异常类
 * <p>
 * 统一封装 HTTP 客户端相关的所有异常，包括网络异常、服务端错误、解析错误等。
 * 提供错误码、请求 ID 等额外信息，便于问题排查和错误处理。
 * </p>
 *
 * <p>常见错误码：</p>
 * <ul>
 *   <li>{@code HTTP_XXX} - HTTP 状态码错误（如 HTTP_404, HTTP_500）</li>
 *   <li>{@code NetworkError} - 网络连接错误</li>
 *   <li>{@code TimeoutError} - 请求超时</li>
 *   <li>{@code ParseError} - 响应解析错误</li>
 *   <li>{@code InvalidRequest} - 请求参数无效</li>
 *   <li>{@code ServerSideError} - 服务端错误</li>
 * </ul>
 *
 * @author message-sdk
 * @since 1.0.0
 */
public class ClientException extends Exception {
    
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求 ID，用于追踪请求
     * <p>
     * 如果请求未完成，此字段可能为空。
     * </p>
     */
    private String requestId;

    /**
     * 错误码
     * <p>
     * 当 API 返回失败时，必须包含错误码。
     * </p>
     */
    private String errorCode;

    /**
     * HTTP 状态码（如果适用）
     */
    private Integer httpStatusCode;

    /**
     * 创建客户端异常
     *
     * @param message 错误消息
     * @param cause   原因异常
     */
    public ClientException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = cause != null ? cause.getClass().getSimpleName() : "UnknownError";
    }

    /**
     * 创建客户端异常
     *
     * @param message 错误消息
     */
    public ClientException(String message) {
        super(message, null);
        this.errorCode = "UnknownError";
    }

    /**
     * 创建客户端异常
     *
     * @param message   错误消息
     * @param requestId 请求 ID
     */
    public ClientException(String message, String requestId) {
        this(message, requestId, "");
    }

    /**
     * 创建客户端异常
     *
     * @param message   错误消息
     * @param requestId 请求 ID
     * @param errorCode 错误码
     */
    public ClientException(String message, String requestId, String errorCode) {
        super(message);
        this.requestId = requestId;
        this.errorCode = errorCode != null && !errorCode.isEmpty() ? errorCode : "UnknownError";
    }

    /**
     * 创建客户端异常
     *
     * @param message       错误消息
     * @param requestId    请求 ID
     * @param errorCode    错误码
     * @param httpStatusCode HTTP 状态码
     */
    public ClientException(String message, String requestId, String errorCode, Integer httpStatusCode) {
        super(message);
        this.requestId = requestId;
        this.errorCode = errorCode != null && !errorCode.isEmpty() ? errorCode : "UnknownError";
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * 获取请求 ID
     *
     * @return 请求 ID
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 设置请求 ID
     *
     * @param requestId 请求 ID
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * 获取错误码
     *
     * @return 错误码字符串
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 设置错误码
     *
     * @param errorCode 错误码
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 获取 HTTP 状态码
     *
     * @return HTTP 状态码，如果不适用则返回 null
     */
    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * 设置 HTTP 状态码
     *
     * @param httpStatusCode HTTP 状态码
     */
    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * 判断是否为网络错误
     *
     * @return 如果是网络错误返回 true
     */
    public boolean isNetworkError() {
        return "NetworkError".equals(errorCode) || getCause() instanceof java.net.UnknownHostException
                || getCause() instanceof java.net.ConnectException
                || getCause() instanceof java.net.SocketTimeoutException;
    }

    /**
     * 判断是否为超时错误
     *
     * @return 如果是超时错误返回 true
     */
    public boolean isTimeoutError() {
        return "TimeoutError".equals(errorCode) || getCause() instanceof java.net.SocketTimeoutException
                || getCause() instanceof java.util.concurrent.TimeoutException;
    }

    /**
     * 判断是否为 HTTP 错误
     *
     * @return 如果是 HTTP 错误返回 true
     */
    public boolean isHttpError() {
        return errorCode != null && errorCode.startsWith("HTTP_");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[ClientException]");
        
        if (errorCode != null && !errorCode.isEmpty()) {
            sb.append(" code: ").append(errorCode);
        }
        
        if (getMessage() != null && !getMessage().isEmpty()) {
            sb.append(" message: ").append(getMessage());
        }
        
        if (requestId != null && !requestId.isEmpty()) {
            sb.append(" requestId: ").append(requestId);
        }
        
        if (httpStatusCode != null) {
            sb.append(" httpStatusCode: ").append(httpStatusCode);
        }
        
        if (getCause() != null) {
            sb.append(" cause: ").append(getCause().toString());
        }
        
        return sb.toString();
    }
}
