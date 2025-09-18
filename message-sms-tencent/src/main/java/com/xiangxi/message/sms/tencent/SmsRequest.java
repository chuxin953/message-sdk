package com.xiangxi.message.sms.tencent;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 初心
 * Create by on 2025/9/16 15:45 58
 */
@Getter
public class SmsRequest {
    private final String phone;
    private final List<String> templateParams; // 按顺序存放

    private SmsRequest(Builder builder) {
        this.phone = builder.phone;
        this.templateParams = builder.templateParams;
    }

    // 转为 SDK 数组
    public String[] getTemplateParamArray() {
        return templateParams.toArray(new String[0]);
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String phone;
        private final List<String> templateParams = new ArrayList<>();

        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder param(String value) {
            this.templateParams.add(value);
            return this;
        }
        public SmsRequest build() { return new SmsRequest(this); }
    }
}
