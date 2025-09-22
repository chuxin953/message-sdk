package com.xiangxi.message.sms.tencent;

import com.alibaba.fastjson2.annotation.JSONField;

/**
 * @author 初心
 * Create by on 2025/9/22 10:52 03
 */
public class TencentSmsApiErrResponse {
    @JSONField(name = "RequestId")
    private String requestId;

    @JSONField(name = "Error")
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
        @JSONField(name = "Code")
        private String code;

        @JSONField(name = "Message")
        private String message;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

}
