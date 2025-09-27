package com.xiangxi.message.sms.model;

import com.xiangxi.message.common.annotation.Required;
import com.xiangxi.message.common.util.MessageValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信发送请求
 *
 * <p>封装短信发送的请求参数，支持单个或多个接收人。</p>
 *
 * @param phoneNumbers   手机号列表（支持多个接收人）
 * @param templateId     模板ID
 * @param templateParams 模板参数（所有接收人使用相同的模板参数）
 * @param signName       签名（可选）
 * @param properties     扩展参数
 * @author 初心
 * @since 1.0.0
 */
public record SmsRequest(@Required(message = "手机号列表不能为空") List<String> phoneNumbers,
                         @Required(message = "模板ID不能为空") String templateId, Map<String, String> templateParams,
                         String signName, Map<String, Object> properties) {

    /**
     * 构造函数
     *
     * @param phoneNumbers   手机号列表
     * @param templateId     模板ID
     * @param templateParams 模板参数
     * @param signName       签名
     * @param properties     扩展参数
     */
    public SmsRequest {
    }

    /**
     * 创建单个接收人的短信请求
     *
     * @param phoneNumber    手机号
     * @param templateId     模板ID
     * @param templateParams 模板参数
     * @return 短信请求
     */
    public static SmsRequest of(String phoneNumber, String templateId, Map<String, String> templateParams) {
        return new SmsRequest(List.of(phoneNumber), templateId, templateParams, null, null);
    }

    /**
     * 创建单个接收人的短信请求（带签名）
     *
     * @param phoneNumber    手机号
     * @param templateId     模板ID
     * @param templateParams 模板参数
     * @param signName       签名
     * @return 短信请求
     */
    public static SmsRequest of(String phoneNumber, String templateId, Map<String, String> templateParams, String signName) {
        return new SmsRequest(List.of(phoneNumber), templateId, templateParams, signName, null);
    }

    /**
     * 创建多个接收人的短信请求
     *
     * @param phoneNumbers   手机号列表
     * @param templateId     模板ID
     * @param templateParams 模板参数（所有接收人使用相同参数）
     * @return 短信请求
     */
    public static SmsRequest of(List<String> phoneNumbers, String templateId, Map<String, String> templateParams) {
        return new SmsRequest(phoneNumbers, templateId, templateParams, null, null);
    }

    /**
     * 创建多个接收人的短信请求（带签名）
     *
     * @param phoneNumbers   手机号列表
     * @param templateId     模板ID
     * @param templateParams 模板参数（所有接收人使用相同参数）
     * @param signName       签名
     * @return 短信请求
     */
    public static SmsRequest of(List<String> phoneNumbers, String templateId, Map<String, String> templateParams, String signName) {
        return new SmsRequest(phoneNumbers, templateId, templateParams, signName, null);
    }

    /**
     * 获取手机号列表
     *
     * @return 手机号列表
     */
    @Override
    public List<String> phoneNumbers() {
        return phoneNumbers;
    }

    /**
     * 获取第一个手机号（兼容单个接收人的场景）
     *
     * @return 第一个手机号
     */
    public String getPhoneNumber() {
        return phoneNumbers != null && !phoneNumbers.isEmpty() ? phoneNumbers.get(0) : null;
    }

    /**
     * 获取接收人数量
     *
     * @return 接收人数量
     */
    public int getRecipientCount() {
        return phoneNumbers != null ? phoneNumbers.size() : 0;
    }

    /**
     * 是否为单个接收人
     *
     * @return 如果只有一个接收人返回true
     */
    public boolean isSingleRecipient() {
        return getRecipientCount() == 1;
    }

    /**
     * 是否为多个接收人
     *
     * @return 如果有多个接收人返回true
     */
    public boolean isMultipleRecipients() {
        return getRecipientCount() > 1;
    }

    /**
     * 获取模板ID
     *
     * @return 模板ID
     */
    @Override
    public String templateId() {
        return templateId;
    }

    /**
     * 获取模板参数（所有接收人使用相同的模板参数）
     *
     * @return 模板参数
     */
    @Override
    public Map<String, String> templateParams() {
        return templateParams;
    }


    /**
     * 获取签名
     *
     * @return 签名
     */
    @Override
    public String signName() {
        return signName;
    }

    /**
     * 获取扩展参数
     *
     * @return 扩展参数
     */
    @Override
    public Map<String, Object> properties() {
        return properties;
    }

    /**
     * 获取扩展参数值
     *
     * @param key 参数键
     * @return 参数值
     */
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }

    /**
     * 获取字符串类型的扩展参数
     *
     * @param key 参数键
     * @return 参数值
     */
    public String getStringProperty(String key) {
        Object value = getProperty(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 验证请求参数
     *
     * @throws IllegalArgumentException 如果参数无效
     */
    public void validate() {
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            throw new IllegalArgumentException("手机号列表不能为空");
        }

        MessageValidator.validateNotEmpty(templateId, "模板ID");

        // 验证所有手机号格式
        for (String phoneNumber : phoneNumbers) {
            if (!MessageValidator.isValidMobile(phoneNumber)) {
                throw new IllegalArgumentException("手机号格式不正确: " + phoneNumber);
            }
        }

        if (!MessageValidator.isValidTemplateId(templateId)) {
            throw new IllegalArgumentException("模板ID格式不正确: " + templateId);
        }

    }

    @Override
    public String toString() {
        return String.format("SmsRequest{phoneNumbers=%s, templateId='%s', signName='%s'}",
                phoneNumbers, templateId, signName);
    }

    /**
     * 创建 Builder 实例
     *
     * @return Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 创建单个手机号的 Builder
     *
     * @param phoneNumber 手机号
     * @return Builder 实例
     */
    public static Builder builder(String phoneNumber) {
        return new Builder().phoneNumber(phoneNumber);
    }

    /**
     * 创建多个手机号的 Builder
     *
     * @param phoneNumbers 手机号列表
     * @return Builder 实例
     */
    public static Builder builder(List<String> phoneNumbers) {
        return new Builder().phoneNumbers(phoneNumbers);
    }

    /**
     * SmsRequest 构建器
     *
     * <p>提供链式调用方式构建 SmsRequest 对象。</p>
     *
     * <h3>使用示例：</h3>
     * <pre>{@code
     * // 基础用法
     * SmsRequest request = SmsRequest.builder()
     *     .phoneNumber("13800138000")
     *     .templateId("SMS_123456")
     *     .templateParams(Map.of("code", "1234"))
     *     .build();
     *
     * // 批量发送
     * SmsRequest request = SmsRequest.builder()
     *     .phoneNumbers(Arrays.asList("13800138000", "13800138001"))
     *     .templateId("SMS_123456")
     *     .templateParams(Map.of("code", "1234", "product", "SDK"))
     *     .signName("您的签名")
     *     .build();
     * }</pre>
     */
    public static class Builder {
        private List<String> phoneNumbers;
        private String templateId;
        private Map<String, String> templateParams;
        private String signName;
        private Map<String, Object> properties;

        /**
         * 设置单个手机号
         *
         * @param phoneNumber 手机号
         * @return Builder 实例
         */
        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumbers = List.of(phoneNumber);
            return this;
        }

        /**
         * 设置手机号列表
         *
         * @param phoneNumbers 手机号列表
         * @return Builder 实例
         */
        public Builder phoneNumbers(List<String> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
            return this;
        }

        /**
         * 添加手机号
         *
         * @param phoneNumber 手机号
         * @return Builder 实例
         */
        public Builder addPhoneNumber(String phoneNumber) {
            if (this.phoneNumbers == null) {
                this.phoneNumbers = new ArrayList<>();
            } else if (this.phoneNumbers.size() == 1) {
                // 如果当前只有一个手机号，转换为列表
                this.phoneNumbers = new ArrayList<>(this.phoneNumbers);
            }
            this.phoneNumbers.add(phoneNumber);
            return this;
        }

        /**
         * 设置模板ID
         *
         * @param templateId 模板ID
         * @return Builder 实例
         */
        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        /**
         * 设置模板参数
         *
         * @param templateParams 模板参数
         * @return Builder 实例
         */
        public Builder templateParams(Map<String, String> templateParams) {
            this.templateParams = templateParams;
            return this;
        }

        /**
         * 添加模板参数
         *
         * @param key   参数键
         * @param value 参数值
         * @return Builder 实例
         */
        public Builder addTemplateParam(String key, String value) {
            if (this.templateParams == null) {
                this.templateParams = new HashMap<>();
            }
            this.templateParams.put(key, value);
            return this;
        }

        /**
         * 设置签名
         *
         * @param signName 签名
         * @return Builder 实例
         */
        public Builder signName(String signName) {
            this.signName = signName;
            return this;
        }

        /**
         * 设置扩展参数
         *
         * @param properties 扩展参数
         * @return Builder 实例
         */
        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        /**
         * 添加扩展参数
         *
         * @param key   参数键
         * @param value 参数值
         * @return Builder 实例
         */
        public Builder addProperty(String key, Object value) {
            if (this.properties == null) {
                this.properties = new HashMap<>();
            }
            this.properties.put(key, value);
            return this;
        }

        /**
         * 构建 SmsRequest 实例
         *
         * @return SmsRequest 实例
         * @throws IllegalStateException 如果必填参数缺失
         */
        public SmsRequest build() {
            if (phoneNumbers == null || phoneNumbers.isEmpty()) {
                throw new IllegalStateException("手机号列表不能为空");
            }
            if (templateId == null || templateId.trim().isEmpty()) {
                throw new IllegalStateException("模板ID不能为空");
            }

            return new SmsRequest(phoneNumbers, templateId, templateParams, signName, properties);
        }
    }
}
