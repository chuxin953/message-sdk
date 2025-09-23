package com.xiangxi.message.sms.tencent;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author 初心
 * Create by on 2025/9/22 10:52 03
 */
public class TencentSmsApiErrResponse {
    @SerializedName("RequestId")
    @Expose
    private String requestId;

    @SerializedName("Error")
    @Expose
    private ErrorInfo error;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    public static class ErrorInfo {
        @SerializedName("Code")
        private String code;

        @SerializedName("Message")
        private String message;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

}
