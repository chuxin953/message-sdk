package com.xiangxi.message.sms.tencent;


import com.google.gson.Gson;
import com.xiangxi.message.client.ClientException;
import com.xiangxi.message.client.HttpClient;
import com.xiangxi.message.client.HttpRequest;
import com.xiangxi.message.client.enums.HttpContentType;
import com.xiangxi.message.client.enums.HttpMethod;
import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.enums.MessageCode;
import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.common.model.MessageSendResult;
import com.xiangxi.message.common.validation.ValidationException;
import com.xiangxi.message.common.validation.Validator;
import com.xiangxi.message.common.exception.MessageSendException;
import com.xiangxi.message.sms.ISmsSender;
import com.xiangxi.message.sms.model.SmsRequest;
import com.xiangxi.message.sms.model.SmsResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 初心
 * Create by on 2025/9/16 15:45 17
 */
public class TencentSmsSender implements ISmsSender<TencentSmsConfig> {

    private final HttpClient httpClient;
    private static final Gson GSON = new Gson();

    public TencentSmsSender() {
        this.httpClient = new HttpClient.Builder()
                .connectTimeout(60)
                .readTimeout(60)
                .defaultHeader("Content-Type", HttpContentType.JSON.value())
                .build();
    }

    @Override
    public String type() {
        return MessageType.SMS.getTypeName();
    }

    @Override
    public String channel() {
        return SmsChannel.TENCENT_SMS.getChannelName();
    }

    @Override
    public SmsResponse send(TencentSmsConfig config, SmsRequest request) throws MessageSendException {
        long startTime = System.currentTimeMillis();
        try {
            // 校验配置参数
            Validator.validate(config);
            // 转换为腾讯云格式
            TencentSmsMessage message = SmsRequestAdapter.toTencentSmsMessage(request);
            // 校验消息参数
            Validator.validate(message);
            // 构建 API 请求体与签名后的 HttpRequest
            TencentSmsApiRequest apiRequest = buildApiRequest(config, message);
            String payload = GSON.toJson(apiRequest);
            HttpRequest httpRequest = buildSignedHttpRequest(config, message, payload);

            // 发送请求并解析响应
            TencentResponseParse<TencentSmsApiResponse> parser = new TencentResponseParse<>(TencentSmsApiResponse.class);
            TencentSmsApiResponse response = httpClient.doRequest(httpRequest, parser);
            // 转换为统一响应格式
            long responseTime = System.currentTimeMillis() - startTime;
            return convertToSmsResponse(response, request, responseTime);
        } catch (ValidationException e) {
            throw new MessageSendException("参数校验失败: " + e.getMessage(), e, "VALIDATION_ERROR", type(), channel());
        } catch (ClientException e) {
            throw new MessageSendException("Tencent SMS send failed", e, "TENCENT_SDK_ERROR", type(), channel());
        } catch (Exception e) {
            // 兜底防御，避免意外 NPE 等导致未包装抛出
            throw new MessageSendException("Unexpected error when sending SMS", e, "UNEXPECTED_ERROR", type(), channel());
        }
    }

    /**
     * 构建API请求
     */
    private TencentSmsApiRequest buildApiRequest(TencentSmsConfig config, TencentSmsMessage message) {
        return new TencentSmsApiRequest.Builder()
                .smsSdkAppId(config.getSdkAppId())
                .signName(config.getSignName())
                .templateId(message.getTemplateId())
                .phoneNumberSet(message.getPhoneNumberArray())
                .templateParamSet(message.getTemplateParamArray())
                .build();
    }

    /**
     * 构建带签名的 HttpRequest。
     */
    private HttpRequest buildSignedHttpRequest(TencentSmsConfig config, TencentSmsMessage message, String payload) throws Exception {
        String authorization = TencentSignUtils.generateAuthorization(
                config.getSecretId(),
                config.getSecretKey(),
                TencentConstant.HOST,
                TencentSmsConfig.SERVICE,
                message.getAction(),
                payload
        );

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        return HttpRequest.builder()
                .url(TencentConstant.TENCENT_SMS_API_URL)
                .method(HttpMethod.POST)
                .contentType(HttpContentType.JSON)
                .body(payload)
                .header("Host", TencentConstant.HOST)
                .header("Authorization", authorization)
                .header("X-TC-Action", message.getAction())
                .header("X-TC-Timestamp", timestamp)
                .header("X-TC-Version", TencentConstant.VERSION)
                .header("X-TC-Region", config.getRegion())
                .build();
    }

    
    /**
     * 转换为统一响应格式（支持部分成功）
     */
    private SmsResponse convertToSmsResponse(TencentSmsApiResponse apiResponse, SmsRequest request, long responseTime) {
        if (apiResponse == null || apiResponse.getSendStatusSet() == null) {
        List<MessageSendResult> results = new ArrayList<>();
        for (String phone : request.phoneNumbers()) {
            MessageSendResult r = new MessageSendResult();
            r.setReceiver(phone);
            r.setSuccess(false);
            r.setErrorCode("TENCENT_EMPTY_RESPONSE"); // 可抽为常量
            r.setErrorMsg("Empty sendStatusSet from Tencent API");
            r.setSendTime(LocalDateTime.now());
            results.add(r);
        }
        return SmsResponse.builder()
                .rawResponse(apiResponse)
                .results(results)
                .requestId(apiResponse != null ? apiResponse.getRequestId() : null)
                .message(MessageCode.FAILED.getDescription())   // 枚举描述
                .channel(channel())
                .code(MessageCode.FAILED.getCode())             // 枚举code
                .build();
    }

    // 明细转换（支持部分成功）
    List<MessageSendResult> results = MessageSendResultConverter.fromTencentSendStatus(apiResponse.getSendStatusSet());
    boolean anySuccess = results.stream().anyMatch(MessageSendResult::isSuccess);
    boolean anyFail = results.stream().anyMatch(r -> !r.isSuccess());

    MessageCode finalCode =
            (anySuccess && anyFail) ? MessageCode.PARTIAL_SUCCESS :
            (anySuccess)            ? MessageCode.SUCCESS :
                                      MessageCode.FAILED;

    return SmsResponse.builder()
            .rawResponse(apiResponse)
            .results(results)
            .requestId(apiResponse.getRequestId())
            .message(finalCode.getDescription())   // 用统一描述
            .channel(channel())
            .code(finalCode.getCode())             // 用统一code
            .build();
    }
}
