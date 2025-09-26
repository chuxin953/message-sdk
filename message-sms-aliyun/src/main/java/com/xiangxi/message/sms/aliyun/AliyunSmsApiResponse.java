package com.xiangxi.message.sms.aliyun;

import com.google.gson.annotations.SerializedName;

/** 阿里云短信 API 响应（简化） */
public class AliyunSmsApiResponse {
    @SerializedName("Message") private String message;
    @SerializedName("RequestId") private String requestId;
    @SerializedName("Code") private String code; // OK 或 错误码

    public String getMessage() { return message; }
    public String getRequestId() { return requestId; }
    public String getCode() { return code; }
}


