package com.xiangxi.message.sms.tencent;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 初心
 * Create by on 2025/9/22 10:49 58
 */

public class TencentResponseJsonModel<T> {
    @JSONField(name = "Response")
    private T response;

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
