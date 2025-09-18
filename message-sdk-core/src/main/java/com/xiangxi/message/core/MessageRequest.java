package com.xiangxi.message.core;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author 初心
 * Create by on 2025/9/16 14:43 34
 */
@Data
@Builder
public class MessageRequest {
    private String to;     //发送给谁 收件人（手机号 / 邮箱 / 用户ID）
    private String title;        // 标题（邮件、推送用）
    private String content;      // 内容
    private Map<String, Object> params; // 模板参数
}
