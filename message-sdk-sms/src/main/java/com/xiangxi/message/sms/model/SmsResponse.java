package com.xiangxi.message.sms.model;

import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.model.MessageResponse;
import com.xiangxi.message.common.enums.MessageCode;
import com.xiangxi.message.common.model.MessageSendResult;

import java.util.List;

/**
 * 短信发送响应
 * 
 * <p>封装短信发送的结果信息。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */

public class SmsResponse implements MessageResponse {

    private final String channel;                   // 渠道标识，如 tencent / aliyun
    private final String code;                      // 通用状态码，如 SUCCESS / FAILED
    private final String message;                   // 通用状态描述
    private final String requestId;                 // 请求ID
    private final List<MessageSendResult> results;  // 每个接收人的发送结果
    private final Object rawResponse;

    private SmsResponse(Builder builder) {
        this.channel = builder.channel;
        this.code = builder.code;
        this.message = builder.message;
        this.requestId = builder.requestId;
        this.results = builder.results;
        this.rawResponse = builder.rawResponse;
    }

    @Override
    public String getChannel() { return channel; }

    @Override
    public String getType() { return MessageType.SMS.getTypeName(); }

    @Override
    public boolean isSuccess() { return MessageCode.SUCCESS.name().equalsIgnoreCase(code); }

    @Override
    public String getCode() { return code; }

    @Override
    public String getMessage() { return message; }

    @Override
    public String getRequestId() { return requestId; }

    @Override
    public List<MessageSendResult> getResults() { return results; }

    @Override
    public Object getRawResponse() { return rawResponse; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String channel;
        private String code;
        private String message;
        private String requestId;
        private List<MessageSendResult> results;
        private Object rawResponse;

        public Builder channel(String channel) { this.channel = channel; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder requestId(String requestId) { this.requestId = requestId; return this; }
        public Builder results(List<MessageSendResult> results) { this.results = results; return this; }
        public Builder rawResponse(Object rawResponse) { this.rawResponse = rawResponse; return this; }

        public SmsResponse build() { return new SmsResponse(this); }
    }
}
