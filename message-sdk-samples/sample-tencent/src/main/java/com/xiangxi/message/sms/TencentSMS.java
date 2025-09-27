package com.xiangxi.message.sms;


import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.manager.MessageSenderManager;
import com.xiangxi.message.sms.model.SmsRequest;
import com.xiangxi.message.sms.tencent.TencentSmsAction;
import com.xiangxi.message.sms.tencent.TencentSmsApiResponse;
import com.xiangxi.message.sms.tencent.TencentSmsConfig;
import com.xiangxi.message.sms.tencent.TencentSmsMessage;

import java.util.Map;

/**
 * @author 初心
 * Create by on 2025/9/16 16:09 18
 */
public class TencentSMS {
    public static void main(String[] args) {
        try {
            TencentSmsConfig config = new TencentSmsConfig.Builder()
                    .secretId("xxx")
                    .secretKey("xxx")
                    .sdkAppId("xxx")
                    .signName("xxx")
                    .region("xxx")
                    .build();
            // 1️⃣ 构建配置
            SmsRequest request = SmsRequest.of(
                    "+xxx",
                    "xxx",
                    Map.of("params1",  "123456")
            );

            // 2️⃣ 构建请求
            // 4️⃣ 调用发送
            TencentSmsApiResponse response = MessageSenderManager.send(MessageType.SMS.name(), SmsChannel.TENCENT_SMS.name(), config, request);

            // 5️⃣ 输出结果
            System.out.println("发送成功: " + response.isSuccess());
            System.out.println("消息ID: " + response.getMessageId());
            System.out.println("错误信息: " + response.getErrorMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}