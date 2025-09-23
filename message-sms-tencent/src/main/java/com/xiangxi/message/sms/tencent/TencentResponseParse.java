package com.xiangxi.message.sms.tencent;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.xiangxi.message.client.ClientException;
import com.xiangxi.message.client.ResponseParse;

import java.lang.reflect.Type;

/**
 * @author 初心
 * Create by on 2025/9/22 10:48 00
 */
public class TencentResponseParse<T> implements ResponseParse<T> {
    private static final Gson gson = new Gson();
    private final Class<T> clazz;

    public TencentResponseParse(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T parse(String body) throws ClientException {
        TencentResponseJsonModel<TencentSmsApiErrResponse> errResp;
        try {
            Type type = new TypeToken<TencentResponseJsonModel<TencentSmsApiErrResponse>>() {}.getType();
            errResp = gson.fromJson(body, type);
        }catch (JsonSyntaxException e){
            String msg = "json is not a valid representation for an object of type";
            throw new ClientException(msg, e);
        }
        if (errResp.getResponse().getError() == null) {
            Type successType = TypeToken.getParameterized(TencentResponseJsonModel.class, clazz).getType();
            TencentResponseJsonModel<T> respModel = gson.fromJson(body, successType);
            return respModel.getResponse();
        }else {
            throw new ClientException(
                    errResp.getResponse().getError().getMessage(),
                    errResp.getResponse().getRequestId(),
                    errResp.getResponse().getError().getCode()
            );
        }
    }
}
