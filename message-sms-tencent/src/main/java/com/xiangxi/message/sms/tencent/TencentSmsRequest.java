package com.xiangxi.message.sms.tencent;

import com.xiangxi.message.common.validation.Required;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯短信请求
 * @author 初心
 * Create by on 2025/9/16 15:45 58
 */
@Getter
public class TencentSmsRequest {
    
    @Required(message = "手机号列表不能为空", fieldName = "手机号列表")
    private final List<String> phoneNumberSet;
    
    private final List<String> templateParams; // 按顺序存放

    private TencentSmsRequest(Builder builder) {
        this.phoneNumberSet = builder.phoneNumberSet;
        this.templateParams = builder.templateParams;
    }

    // 转为 SDK 数组
    public String[] getTemplateParamArray() {
        return templateParams.toArray(new String[0]);
    }

    public String[] getPhoneNumberSet() {
        return phoneNumberSet.toArray(new String[0]);
    }

    public static Builder builder() {
        return new Builder(); }

    public static class Builder {
        private final List<String> phoneNumberSet = new ArrayList<>();
        private final List<String> templateParams = new ArrayList<>();


        public Builder addPhone(String phone) {
            this.phoneNumberSet.add(phone);
            return this;
        }
        public Builder addParams(String value) {
            this.templateParams.add(value);
            return this;
        }
        public TencentSmsRequest build() { return new TencentSmsRequest(this); }
    }
}
