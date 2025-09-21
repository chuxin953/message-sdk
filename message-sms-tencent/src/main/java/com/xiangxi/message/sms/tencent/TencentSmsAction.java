package com.xiangxi.message.sms.tencent;

/**
 * @author 初心
 * Create by on 2025/9/21 17:39
 */

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 腾讯云 SMS API 动作枚举
 */
public enum TencentSmsAction {
    /**
     * 发送短信
     */
    SendSms("SendSms"),

    /**
     * 拉取短信回执状态
     */
    PullSmsSendStatus("PullSmsSendStatus"),

    /**
     * 拉取短信回复
     */
    PullSmsReplyStatus("PullSmsReplyStatus"),

    /**
     * 发送国际/港澳台短信
     */
    SendSmsIntl("SendSmsIntl"),

    /**
     * 拉取国际/港澳台短信回执
     */
    PullSmsSendStatusIntl("PullSmsSendStatusIntl"),

    /**
     * 拉取国际/港澳台短信回复
     */
    PullSmsReplyStatusIntl("PullSmsReplyStatusIntl");

    private final String value;

    TencentSmsAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    // 反查表
    private static final Map<String, TencentSmsAction> LOOKUP =
            Stream.of(values()).collect(Collectors.toMap(TencentSmsAction::getValue, e -> e));

    /**
     * 根据字符串查找对应的枚举
     */
    public static TencentSmsAction fromValue(String value) {
        TencentSmsAction action = LOOKUP.get(value);
        if (action == null) {
            throw new IllegalArgumentException("Unknown TencentSmsAction: " + value);
        }
        return action;
    }
}