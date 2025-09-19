package com.xiangxi.message.sms;


import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.manager.MessageSenderManager;
import com.xiangxi.message.sms.model.SmsResponse;
import com.xiangxi.message.sms.tencent.TencentSmsConfig;
import com.xiangxi.message.sms.tencent.TencentSmsRequest;

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
                    "xxx",       // 替换成你的 AppId
                    "xxx",              // 替换成你的 sdkAppId
                    "xxx",// 替换成你的 AppKey
                    "xxx",      // 腾讯云短信区域
                    "xxx"           // 模板 ID
            );

            // 2️⃣ 构建请求
            TencentSmsRequest request = TencentSmsRequest.builder()
                    .addPhone("+8615384456055") // 手机号
                    .addParams("234567")// 测试手机号，注意加国家码
                    .build();

            // 4️⃣ 调用发送
            SmsResponse resp = MessageSenderManager.send(MessageType.SMS.name(), SmsChannel.TENCENT_SMS.name(), config, request);

            // 5️⃣ 输出结果
            System.out.println("发送成功: " + resp.success());
            System.out.println("消息ID: " + resp.messageId());
            System.out.println("错误信息: " + resp.error());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
