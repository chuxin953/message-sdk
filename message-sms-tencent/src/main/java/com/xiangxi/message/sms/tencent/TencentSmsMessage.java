package com.xiangxi.message.sms.tencent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 腾讯云短信请求模型
 * 
 * <p>封装腾讯云短信发送所需的请求参数，包括接收手机号列表和模板参数。
 * 该类采用建造者模式，提供链式调用方式构建请求对象。</p>
 * 
 * <h3>主要功能：</h3>
 * <ul>
 *   <li><strong>手机号管理</strong>: 支持单个或批量添加手机号</li>
 *   <li><strong>模板参数</strong>: 按顺序设置短信模板的替换参数</li>
 *   <li><strong>参数校验</strong>: 自动校验手机号格式和必填参数</li>
 *   <li><strong>格式转换</strong>: 提供腾讯云SDK所需的数组格式</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 发送验证码短信
 * TencentSmsRequest request = TencentSmsRequest.builder()
 *     .addPhone("+8613800138000")
 *     .addPhone("+8613900139000")
 *     .addParam("123456")        // 验证码
 *     .addParam("5")             // 有效期（分钟）
 *     .build();
 * 
 * // 发送通知短信
 * TencentSmsRequest notification = TencentSmsRequest.builder()
 *     .addPhones(Arrays.asList("+8613800138000", "+8613900139000"))
 *     .addParams(Arrays.asList("张三", "订单已发货", "2024-01-15"))
 *     .build();
 * }</pre>
 * 
 * <h3>注意事项：</h3>
 * <ul>
 *   <li>手机号必须包含国际区号，如+86</li>
 *   <li>模板参数的顺序必须与模板中的占位符顺序一致</li>
 *   <li>单次发送最多支持200个手机号</li>
 *   <li>模板参数不能包含敏感词汇</li>
 * </ul>
 * 
 * @author 初心
 * @version 1.0
 * @since 1.0
 * @see com.xiangxi.message.sms.tencent.TencentSmsSender
 * @see com.xiangxi.message.sms.tencent.TencentSmsConfig
 */
public class TencentSmsMessage {
    
    /**
     * 手机号码正则表达式（支持国际格式）
     * 匹配格式：+86xxxxxxxx 或 +1xxxxxxxxxx 等
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+\\d{1,4}\\d{7,15}$");
    
    /**
     * 单次发送最大手机号数量限制
     */
    private static final int MAX_PHONE_COUNT = 200;
    
    /**
     * 接收短信的手机号列表
     * <p>必须包含国际区号，如+8613800138000</p>
     */
    private final List<String> phoneNumberSet;
    private final String templateId;

    private final String action;
    
    /**
     * 短信模板参数列表
     * <p>按顺序存放，对应模板中的占位符 {1}, {2}, {3}...</p>
     */
    private final List<String> templateParams;

    /**
     * 私有构造函数，通过Builder创建实例
     * 
     * @param builder 建造者实例
     */
    private TencentSmsMessage(Builder builder) {
        this.phoneNumberSet = Collections.unmodifiableList(new ArrayList<>(builder.phoneNumberSet));
        this.templateParams = Collections.unmodifiableList(new ArrayList<>(builder.templateParams));
        this.templateId = builder.templateId;
        this.action = builder.action;
    }

    /**
     * 获取模板参数数组
     * <p>转换为腾讯云SDK所需的String数组格式</p>
     * 
     * @return 模板参数数组，如果为空则返回空数组
     */
    public String[] getTemplateParamArray() {
        return templateParams.toArray(new String[0]);
    }

    /**
     * 获取手机号数组
     * <p>转换为腾讯云SDK所需的String数组格式</p>
     * 
     * @return 手机号数组
     */
    public String[] getPhoneNumberArray() {
        return phoneNumberSet.toArray(new String[0]);
    }

    public String getTemplateId() {
        return templateId;
    }

    public String getAction() {
        return  action;
    }

    /**
     * 创建建造者实例
     * 
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * 创建单个手机号的建造者
     * 
     * @param phoneNumber 手机号
     * @return Builder实例
     */
    public static Builder builder(String phoneNumber) {
        return new Builder().addPhone(phoneNumber);
    }
    
    /**
     * 创建批量手机号的建造者
     * 
     * @param phoneNumbers 手机号列表
     * @return Builder实例
     */
    public static Builder builder(List<String> phoneNumbers) {
        return new Builder().addPhones(phoneNumbers);
    }

    /**
     * TencentSmsRequest建造者类
     * <p>提供链式调用方式构建请求对象，支持参数校验和多种便利方法</p>
     * 
     * <h3>使用示例：</h3>
     * <pre>{@code
     * // 基础用法
     * TencentSmsRequest request = TencentSmsRequest.builder()
     *     .addPhone("+8613800138000")
     *     .addParam("123456")
     *     .build();
     * 
     * // 便利方法
     * TencentSmsRequest request2 = TencentSmsRequest.builder("+8613800138000")
     *     .addParamsArray("123456", "5")
     *     .build();
     * }</pre>
     */
    public static class Builder {
        private final List<String> phoneNumberSet = new ArrayList<>();
        private final List<String> templateParams = new ArrayList<>();
        private String templateId;
        private String action;

        /**
         * 添加单个手机号
         * 
         * @param phone 手机号，必须包含国际区号
         * @return Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果手机号格式不正确或超出数量限制
         */
        public Builder addPhone(String phone) {
            validatePhone(phone);
            validatePhoneCount();
            this.phoneNumberSet.add(phone);
            return this;
        }
        
        /**
         * 批量添加手机号
         * 
         * @param phones 手机号列表
         * @return Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果手机号格式不正确或超出数量限制
         */
        public Builder addPhones(List<String> phones) {
            if (phones != null) {
                for (String phone : phones) {
                    addPhone(phone);
                }
            }
            return this;
        }
        
        /**
         * 批量添加手机号（数组形式）
         * 
         * @param phones 手机号数组
         * @return Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果手机号格式不正确或超出数量限制
         */
        public Builder addPhonesArray(String... phones) {
            if (phones != null) {
                return addPhones(Arrays.asList(phones));
            }
            return this;
        }
        
        /**
         * 设置手机号列表（替换现有列表）
         * 
         * @param phones 手机号列表
         * @return Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果手机号格式不正确或超出数量限制
         */
        public Builder setPhones(List<String> phones) {
            this.phoneNumberSet.clear();
            return addPhones(phones);
        }
        
        /**
         * 添加单个模板参数
         * 
         * @param param 模板参数值
         * @return Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果参数为null
         */
        public Builder addParam(String param) {
            if (param == null) {
                throw new IllegalArgumentException("模板参数不能为null");
            }
            this.templateParams.add(param);
            return this;
        }
        
        /**
         * 批量添加模板参数
         * 
         * @param params 模板参数列表
         * @return Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果参数列表包含null值
         */
        public Builder addParams(List<String> params) {
            if (params != null) {
                for (String param : params) {
                    addParam(param);
                }
            }
            return this;
        }

        public Builder addParams(String[] params) {
            if (params != null) {
                for (String param : params) {
                    addParam(param);
                }
            }
            return this;
        }
        
        /**
         * 批量添加模板参数（数组形式）
         * 
         * @param params 模板参数数组
         * @return Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果参数包含null值
         */
        public Builder addParamsArray(String... params) {
            if (params != null) {
                return addParams(Arrays.asList(params));
            }
            return this;
        }

        public Builder templateId(String templateId) {
            if (templateId != null) {
                this.templateId = templateId;
            }
            return this;
        }

        public Builder action(String action) {
            if (action != null) {
                this.action = action;
            }
            return this;
        }
        
        /**
         * 设置模板参数列表（替换现有列表）
         * 
         * @param params 模板参数列表
         * @return Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果参数列表包含null值
         */
        public Builder setParams(List<String> params) {
            this.templateParams.clear();
            return addParams(params);
        }
        
        /**
         * 设置模板参数数组（替换现有列表）
         * 
         * @param params 模板参数数组
         * @return Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果参数包含null值
         */
        public Builder setParamsArray(String... params) {
            this.templateParams.clear();
            return addParamsArray(params);
        }
        
        /**
         * 清空所有手机号
         *
         * @return Builder实例，支持链式调用
         */
        public Builder clearPhones() {
            this.phoneNumberSet.clear();
            return this;
        }
        
        /**
         * 清空所有模板参数
         * 
         * @return Builder实例，支持链式调用
         */
        public Builder clearParams() {
            this.templateParams.clear();
            return this;
        }
        
        /**
         * 清空所有数据
         * 
         * @return Builder实例，支持链式调用
         */
        public Builder clear() {
            this.phoneNumberSet.clear();
            this.templateParams.clear();
            return this;
        }
        
        /**
         * 构建TencentSmsRequest实例
         * 
         * @return TencentSmsRequest实例
         * @throws IllegalStateException 如果必填参数缺失
         */
        public TencentSmsMessage build() {
            if (phoneNumberSet.isEmpty()) {
                throw new IllegalStateException("手机号列表不能为空");
            }
            if (phoneNumberSet.size() > MAX_PHONE_COUNT) {
                throw new IllegalStateException("手机号一次发送最多支持200个");
            }
            return new TencentSmsMessage(this);
        }
        
        /**
         * 校验手机号格式
         * 
         * @param phone 手机号
         * @throws IllegalArgumentException 如果手机号格式不正确
         */
        private void validatePhone(String phone) {
            if (phone == null || phone.trim().isEmpty()) {
                throw new IllegalArgumentException("手机号不能为空");
            }
            if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
                throw new IllegalArgumentException("手机号格式不正确，必须包含国际区号，如+8613800138000");
            }
        }
        
        /**
         * 校验手机号数量限制
         * 
         * @throws IllegalArgumentException 如果超出数量限制
         */
        private void validatePhoneCount() {
            if (phoneNumberSet.size() >= MAX_PHONE_COUNT) {
                throw new IllegalArgumentException("手机号数量不能超过" + MAX_PHONE_COUNT + "个");
            }
        }
    }
}
