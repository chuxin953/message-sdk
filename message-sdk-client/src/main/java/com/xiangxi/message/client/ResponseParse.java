package com.xiangxi.message.client;

/**
 * @author 初心
 * Create by on 2025/9/22 10:47 23
 */
public interface ResponseParse<T> {
    T parse(String response) throws ClientException;
}
