package com.xiangxi.message.sms.aliyun;

import com.google.gson.Gson;
import com.xiangxi.message.client.ClientException;
import com.xiangxi.message.client.HttpClient;
import com.xiangxi.message.client.HttpRequest;
import com.xiangxi.message.client.ResponseParse;
import com.xiangxi.message.client.enums.HttpContentType;
import com.xiangxi.message.client.enums.HttpMethod;
import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.common.exception.MessageSendException;
import com.xiangxi.message.common.validation.ValidationException;
import com.xiangxi.message.common.validation.Validator;
import com.xiangxi.message.sms.ISmsSender;

/** 阿里云短信发送器（对齐腾讯结构，HTTP直连签名版简化） */
public class AliyunSmsSender implements ISmsSender<AliyunSmsConfig, AliyunSmsMessage, AliyunSmsApiResponse> {

    private final HttpClient httpClient;
    private static final Gson GSON = new Gson();

    public AliyunSmsSender() {
        this.httpClient = new HttpClient.Builder()
                .connectTimeout(60)
                .readTimeout(60)
                .defaultHeader("Content-Type", HttpContentType.JSON.value())
                .build();
    }

    @Override public String type() { return MessageType.SMS.getTypeName(); }
    @Override public String channel() { return SmsChannel.ALI_SMS.getChannelName(); }

    @Override
    public AliyunSmsApiResponse send(AliyunSmsConfig config, AliyunSmsMessage message) throws MessageSendException {
        try {
            Validator.validate(config);
            Validator.validate(message);
            AliyunSmsApiRequest apiReq = buildApiRequest(config, message);
            String payload = GSON.toJson(apiReq);
            HttpRequest request = buildSignedHttpRequest(config, payload);
            ResponseParse<AliyunSmsApiResponse> parser = body -> GSON.fromJson(body, AliyunSmsApiResponse.class);
            return httpClient.doRequest(request, parser);
        } catch (ValidationException e) {
            throw new MessageSendException("参数校验失败: " + e.getMessage(), e, "VALIDATION_ERROR", type(), channel());
        } catch (ClientException e) {
            throw new MessageSendException("Aliyun SMS send failed", e, "ALIYUN_SDK_ERROR", type(), channel());
        } catch (Exception e) {
            throw new MessageSendException("Unexpected error when sending SMS", e, "UNEXPECTED_ERROR", type(), channel());
        }
    }

    private AliyunSmsApiRequest buildApiRequest(AliyunSmsConfig config, AliyunSmsMessage msg) {
        return new AliyunSmsApiRequest.Builder()
                .phoneNumbers(msg.getPhoneNumbers())
                .signName(config.getSignName())
                .templateCode(msg.getTemplateCode())
                .templateParam(msg.getTemplateParamJson())
                .build();
    }

    private HttpRequest buildSignedHttpRequest(AliyunSmsConfig config, String payload) throws Exception {
        // ACS V3（ROA风格）POST 签名示例
        String host = "dysmsapi.aliyuncs.com";
        String endpoint = "https://" + host;
        String uri = "/"; // 统一入口，Action/Version 放在 body 或 query 中；此处放 body
        byte[] body = payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        java.util.Map<String,String> extra = new java.util.LinkedHashMap<>();
        extra.put("x-acs-action", "SendSms");
        extra.put("x-acs-version", "2017-05-25");
        extra.put("x-acs-region-id", config.getRegionId());
        java.util.Map<String,String> headers = AliyunSignUtils.buildV3Headers(
                config.getAccessKeyId(),
                config.getAccessKeySecret(),
                "POST",
                host,
                uri,
                null,
                body,
                extra
        );
        return HttpRequest.builder()
                .url(endpoint + uri)
                .method(HttpMethod.POST)
                .contentType(HttpContentType.JSON)
                .body(payload)
                .headers(headers)
                .build();
    }
}


