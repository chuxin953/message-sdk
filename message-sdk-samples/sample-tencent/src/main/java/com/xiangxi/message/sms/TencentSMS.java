package com.xiangxi.message.sms;


import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.manager.MessageSenderManager;
import com.xiangxi.message.sms.tencent.TencentSmsAction;
import com.xiangxi.message.sms.tencent.TencentSmsApiResponse;
import com.xiangxi.message.sms.tencent.TencentSmsConfig;
import com.xiangxi.message.sms.tencent.TencentSmsMessage;

/**
 * @author 初心
 * Create by on 2025/9/16 16:09 18
 */
public class TencentSMS {
    public static void main(String[] args) {
        try {
            TencentSmsConfig config = new TencentSmsConfig.Builder()
                    .secretId("xxx")
                    .secretKey("XXXX")
                    .sdkAppId("XXX")
                    .signName("XXX")
                    .region("ap-guangzhou")
                    .build();
            // 1️⃣ 构建配置

            // 2️⃣ 构建请求
            TencentSmsMessage request = TencentSmsMessage.builder()
                    .addPhone("+XXX") // 手机号
                    .addParam("234567")// 测试手机号，注意加国家码
                    .templateId("XXX")
                    .action(TencentSmsAction.SendSms.toString())
                    .build();
            // 4️⃣ 调用发送
            TencentSmsApiResponse response = MessageSenderManager.send(MessageType.SMS.name(), SmsChannel.TENCENT_SMS.name(), config, request);

            // 5️⃣ 输出结果
//            System.out.println("发送成功: " + resp.success());
//            System.out.println("消息ID: " + resp.messageId());
//            System.out.println("错误信息: " + resp.error());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
