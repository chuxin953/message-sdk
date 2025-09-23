package com.xiangxi.message.sms.tencent;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author 初心
 * Create by on 2025/9/22 10:49 58
 */

public class TencentResponseJsonModel<T> {
    @SerializedName("Response")
    @Expose
    private T response;

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
}
