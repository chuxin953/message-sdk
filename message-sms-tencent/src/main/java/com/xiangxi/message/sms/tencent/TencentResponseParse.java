package com.xiangxi.message.sms.tencent;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.fastjson2.util.ParameterizedTypeImpl;
import com.google.gson.JsonSyntaxException;
import com.xiangxi.message.client.ClientException;
import com.xiangxi.message.client.ResponseParse;

import java.lang.reflect.Type;

/**
 * @author 初心
 * Create by on 2025/9/22 10:48 00
 */
public class TencentResponseParse<T> implements ResponseParse<T> {
    private final Class<T> clazz;

    public TencentResponseParse(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T parse(String body) throws ClientException {
        TencentResponseJsonModel<TencentSmsApiErrResponse> errResp;
        try {
            Type type = new TypeReference<TencentResponseJsonModel<TencentSmsApiErrResponse>>(){
            }.getType();
            errResp = JSON.parseObject(body, type);
        }catch (JsonSyntaxException e){
            String msg = "json is not a valid representation for an object of type";
            throw new ClientException(msg, e);
        }
        if (errResp.getResponse().getError() == null) {
            // 成功情况，解析为泛型 T
            TencentResponseJsonModel<T> respModel = JSON.parseObject(
                    body,
                    new ParameterizedTypeImpl(
                            new Type[]{clazz},              // 泛型参数 T
                            null,                           // ownerType (外部类) = null
                            TencentResponseJsonModel.class
            ));
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
