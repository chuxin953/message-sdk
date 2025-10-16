package com.xiangxi.message.sms.tencent;

import com.xiangxi.message.common.model.MessageSendResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 初心
 * Create by on 2025/10/16 14:20 18
 */
public class MessageSendResultConverter {
    /**
     * 通用转换方法
     *
     * @param sourceList 渠道返回的原始列表
     * @param mapper     渠道对象 -> MessageSendResult 映射函数
     * @param <T>        渠道对象类型
     * @return List<MessageSendResult>
     */
    public static <T> List<MessageSendResult> convert(List<T> sourceList, Function<T, MessageSendResult> mapper) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceList.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 腾讯 SendStatus 转 MessageSendResult
     */
    public static List<MessageSendResult> fromTencentSendStatus(List<TencentSmsApiResponse.SendStatus> sendStatusList) {
        return convert(sendStatusList, status -> {
            MessageSendResult result = new MessageSendResult();
            result.setReceiver(status.getPhoneNumber());
            result.setMessageId(status.getSerialNo());
            boolean success = "OK".equalsIgnoreCase(status.getCode());
            result.setSuccess(success);
            result.setErrorCode(success ? null : status.getCode());
            result.setErrorMsg(success ? null : status.getMessage());
            result.setSendTime(LocalDateTime.now());
            return result;
        });
    }
}
