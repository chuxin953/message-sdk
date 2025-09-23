package com.xiangxi.message.sms.tencent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author 初心
 * Create by on 2025/9/22 10:51 46
 */
public class TencentSmsApiResponse {
    @SerializedName("SendStatusSet")
    @Expose
    private List<SendStatus> sendStatusSet;

    @SerializedName("RequestId")
    @Expose
    private String requestId;

    public List<SendStatus> getSendStatusSet() { return sendStatusSet; }
    public void setSendStatusSet(List<SendStatus> sendStatusSet) { this.sendStatusSet = sendStatusSet; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }


    public static class SendStatus {
        @SerializedName("Code")
        @Expose
        private String code;

        @SerializedName("Message")
        @Expose
        private String message;

        @SerializedName("SerialNo")
        @Expose
        private String serialNo;

        @SerializedName("PhoneNumber")
        @Expose
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
