package com.xiangxi.message.common.model;

import java.util.List;

/**
 * @author 初心
 * Create by on 2025/10/16 11:24 49
 *
 * 消息中心统一响应接口
 * 各渠道返回体需实现本接口
 */
public interface MessageResponse {
    /** 渠道标识（如 tencent / aliyun / wechat） */
    String getChannel();

    /** 消息类型（sms / email / push / voice 等） */
    String getType();

    /** 是否http请求是否成功 */
    boolean isSuccess();

    /** 平台返回的通用状态码 */
    String getCode();

    /** 平台返回的通用状态描述 */
    String getMessage();

    /** 平台返回的唯一请求ID（用于日志或追踪） */
    String getRequestId();

    /** 每个接收人的详细发送结果 */
    List<MessageSendResult> getResults();

    /** 渠道原始返回数据（可选，便于调试或日志记录） */
    Object getRawResponse();
}
