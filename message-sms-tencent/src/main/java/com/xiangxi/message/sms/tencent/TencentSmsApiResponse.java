package com.xiangxi.message.sms.tencent;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.List;

/**
 * @author 初心
 * Create by on 2025/9/22 10:51 46
 */
public class TencentSmsApiResponse {
    @JSONField(name = "SendStatusSet")
    private List<SendStatus> sendStatusSet;

    @JSONField(name = "RequestId")
    private String requestId;

    public List<SendStatus> getSendStatusSet() { return sendStatusSet; }
    public void setSendStatusSet(List<SendStatus> sendStatusSet) { this.sendStatusSet = sendStatusSet; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }


    public static class SendStatus {
        @JSONField(name = "Code")
        private String code;

        @JSONField(name = "Message")
        private String message;

        @JSONField(name = "SerialNo")
        private String serialNo;

        @JSONField(name = "PhoneNumber")
        private String phoneNumber;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getSerialNo() { return serialNo; }
        public void setSerialNo(String serialNo) { this.serialNo = serialNo; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
}
