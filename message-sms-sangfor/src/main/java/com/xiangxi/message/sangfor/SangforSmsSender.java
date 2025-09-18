package com.xiangxi.message.sangfor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiangxi.message.common.enums.MessageType;
import com.xiangxi.message.common.enums.SmsChannel;
import com.xiangxi.message.common.exception.MessageSendException;
import com.xiangxi.message.sms.ISmsSender;
import com.xiangxi.message.sms.model.SmsResponse;
import okhttp3.*;

import java.io.IOException;


/**
 * @author 初心
 * Create by on 2025/9/17 14:48 18
 */
public class SangforSmsSender implements ISmsSender<SangforSmsConfig, SmsRequest> {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    @Override
    public String type() {
        return MessageType.SMS.name();
    }

    @Override
    public String channel() {
        return SmsChannel.SANG_FOR_SMS.name();
    }

    @Override
    public SmsResponse send(SangforSmsConfig config, SmsRequest smsRequest) throws MessageSendException {
        try {

            String jsonBody = JSON.toJSONString(smsRequest);

            Request request = new Request.Builder()
                    .url(config.getApiUrl())
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = CLIENT.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return new SmsResponse(false,"HTTP_ERROR", null);
                }

                String respBody = response.body().string();
                JSONObject obj = JSON.parseObject(respBody);

                int code = obj.getIntValue("code");
                String msg = obj.getString("msg");
                String requestId = obj.getString("requestId");

                return new SmsResponse(true, msg, requestId);
            }

        } catch (IOException e) {
            throw new MessageSendException("Sangfor SMS send failed", e);
        }
    }
}
