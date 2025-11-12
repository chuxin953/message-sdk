package com.xiangxi.message.client.adapter;

import com.xiangxi.message.client.ClientException;
import com.xiangxi.message.client.HttpRequest;
import okhttp3.Request;

/**
 * HTTP 请求适配器接口
 * <p>
 * 将高层 {@link HttpRequest} 模型转换为底层 HTTP 客户端请求对象。
 * </p>
 *
 * @author message-sdk
 * @since 1.0.0
 */
public interface HttpRequestAdapter {
    /**
     * 将 HttpRequest 适配为底层请求对象
     *
     * @param request HTTP 请求对象
     * @return 底层请求对象
     * @throws ClientException 如果适配失败
     */
    Request adaptRequest(HttpRequest request) throws ClientException;
}
