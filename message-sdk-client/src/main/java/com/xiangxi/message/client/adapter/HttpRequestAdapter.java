package com.xiangxi.message.client.adapter;

import com.xiangxi.message.client.HttpRequest;
import okhttp3.Request;

public interface HttpRequestAdapter {
    Request adaptRequest(HttpRequest request);
}
