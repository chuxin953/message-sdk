package com.xiangxi.message.sms.tencent;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.common.exception.MessageSendException;
import com.xiangxi.message.common.validation.ValidationException;
import com.xiangxi.message.common.validation.Validator;
import com.xiangxi.message.sms.ISmsSender;
import com.xiangxi.message.sms.model.SmsResponse;

/**
 * @author 初心
 * Create by on 2025/9/16 15:45 17
 */
public class TencentSmsSender implements ISmsSender<TencentSmsConfig, TencentSmsRequest> {

    @Override
    public String type() {
        return MessageType.SMS.name();
    }

    @Override
    public String channel() {
        return SmsChannel.TENCENT_SMS.name();
    }

    @Override
    public SmsResponse send(TencentSmsConfig config, TencentSmsRequest message) throws MessageSendException {
        try {
            // 校验配置参数
            Validator.validate(config);
            
            // 校验消息参数
            Validator.validate(message);
            
            // 构建凭证
            Credential cred = new Credential(config.getAppId(), config.getAppKey());
            // 创建短信客户端
            SmsClient client = new SmsClient(cred, config.getRegion());

            SendSmsRequest request =  new SendSmsRequest();
            // appid
            request.setSmsSdkAppId(config.getSdkAppId());
            // 模版id
            request.setTemplateId(config.getTemplateId());
            // 发送给谁
            request.setPhoneNumberSet(message.getPhoneNumberSet());
            // 国内需要设置签名
            request.setSignName(config.getSign());
            // 模版参数替换
            request.setTemplateParamSet(message.getTemplateParamArray());

            SendSmsResponse resp = client.SendSms(request);
            // 返回结果
            if ("Ok".equalsIgnoreCase(resp.getSendStatusSet()[0].getCode())) {
                return new SmsResponse(true, resp.getSendStatusSet()[0].getSerialNo(), null);
            } else {
                return new SmsResponse(false, null, resp.getSendStatusSet()[0].getMessage());
            }
        } catch (ValidationException e) {
            throw new MessageSendException("参数校验失败: " + e.getMessage(), e);
        } catch (TencentCloudSDKException e) {
            throw new MessageSendException("Tencent SMS send failed", e);
        }
    }
}
