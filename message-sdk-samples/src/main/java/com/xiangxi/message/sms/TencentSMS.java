package com.xiangxi.message.sms;


import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.sms.factory.ISenderFactory;
import com.xiangxi.message.sms.model.SmsResponse;
import com.xiangxi.message.sms.tencent.SmsRequest;
import com.xiangxi.message.sms.tencent.TencentSmsConfig;

/**
 * @author 初心
 * Create by on 2025/9/16 16:09 18
 */
public class TencentSMS {
    public static void main(String[] args) {
        try {
            // 1️⃣ 构建配置
            TencentSmsConfig config = new TencentSmsConfig(
                    "xxx",
                    "xxxx",       // 替换成你的 AppId
                    "xxx",              // 替换成你的 sdkAppId
                    "xxx",// 替换成你的 AppKey
                    "ap-guangzhou",      // 腾讯云短信区域
                    "xxx"           // 模板 ID
            );

            // 2️⃣ 构建请求
            SmsRequest request = SmsRequest.builder()
                    .phone("") // 手机号
                    .param("123456")// 测试手机号，注意加国家码
                    .build();

            // 3️⃣ 从 Factory 获取 Sender


//            MessageSender<TencentSmsConfig, SmsRequest, SmsResponse> sender =
//                    MessageSenderFactory.getSender(MessageType.SMS.name(), SmsChannel.TENCENT_SMS.name());
            ISmsSender<TencentSmsConfig, SmsRequest> sender = ISenderFactory.getSmsSender(SmsChannel.TENCENT_SMS.name());
            // 4️⃣ 调用发送
            SmsResponse resp = sender.send(config, request);

            // 5️⃣ 输出结果
            System.out.println("发送成功: " + resp.success());
            System.out.println("消息ID: " + resp.messageId());
            System.out.println("错误信息: " + resp.error());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
