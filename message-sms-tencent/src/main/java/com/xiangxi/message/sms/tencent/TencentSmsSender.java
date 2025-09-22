package com.xiangxi.message.sms.tencent;


import com.alibaba.fastjson2.JSON;
import com.xiangxi.message.client.ClientException;
import com.xiangxi.message.client.HttpClient;
import com.xiangxi.message.client.HttpRequest;
import com.xiangxi.message.client.enums.HttpContentType;
import com.xiangxi.message.client.enums.HttpMethod;
import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.common.exception.MessageSendException;
import com.xiangxi.message.common.validation.ValidationException;
import com.xiangxi.message.common.validation.Validator;
import com.xiangxi.message.sms.ISmsSender;

/**
 * @author 初心
 * Create by on 2025/9/16 15:45 17
 */
public class TencentSmsSender implements ISmsSender<TencentSmsConfig, TencentSmsMessage, TencentSmsApiResponse> {

    private final HttpClient httpClient;

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
    public TencentSmsApiResponse send(TencentSmsConfig config, TencentSmsMessage message) throws MessageSendException {
        try {
            // 校验配置参数
            Validator.validate(config);
            // 校验消息参数
            Validator.validate(message);
            // 构建API请求
            TencentSmsApiRequest apiRequest = buildApiRequest(config, message);
            String payload = JSON.toJSONString(apiRequest);
            // 生成签名
            String authorization = TencentSignUtils.generateAuthorization(config.getSecretId(),
                    config.getSecretKey(),
                    TencentConstant.HOST,
                    TencentSmsConfig.SERVICE,
                    message.getAction(),
                    payload);

            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);


            HttpRequest request = HttpRequest.builder()
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

            TencentResponseParse<TencentSmsApiResponse> parser = new TencentResponseParse<>(TencentSmsApiResponse.class);
            return httpClient.doRequest(request, parser);
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
}
