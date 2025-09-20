package com.xiangxi.message.client;

import com.alibaba.fastjson2.annotation.JSONField;

public class JsonResponseModel<T> {
    @JSONField(name = "response")
    private T data;
}
