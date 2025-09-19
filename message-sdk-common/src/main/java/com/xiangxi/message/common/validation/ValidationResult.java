package com.xiangxi.message.common.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 校验结果封装类
 * 
 * <p>用于封装参数校验的结果，支持单个和多个校验错误的处理。
 * 提供链式调用和批量校验的便利方法。</p>
 * 
 * <p>主要特性：</p>
 * <ul>
 *   <li>支持成功和失败状态的封装</li>
 *   <li>支持单个和多个错误信息</li>
 *   <li>提供链式调用的便利方法</li>
 *   <li>支持错误信息的聚合和格式化</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * // 单个校验
 * ValidationResult result = ValidationResult.success();
 * if (StringUtils.isEmpty(username)) {
 *     result = ValidationResult.failure("用户名不能为空", "username", username);
 * }
 * 
 * // 批量校验
 * ValidationResult result = ValidationResult.success()
 *     .and(() -> validateUsername(username))
 *     .and(() -> validateEmail(email))
 *     .and(() -> validatePassword(password));
 * 
 * if (result.isFailure()) {
 *     log.warn("校验失败: {}", result.getAllErrorMessages());
 * }
 * }</pre>
 * 
 * @author 初心
 * @version 1.0.0
 * @since 1.0.0
 */
public class ValidationResult {
    
    /**
     * 校验是否成功
     */
    private final boolean success;
    
    /**
     * 主要错误消息（第一个错误的消息）
     */
    private final String message;
    
    /**
     * 主要错误字段名（第一个错误的字段名）
     */
    private final String fieldName;
    
    /**
     * 主要错误字段值（第一个错误的字段值）
     */
    private final String fieldValue;
    
    /**
     * 所有校验错误列表
     */
    private final List<ValidationError> errors;
    
    /**
     * 私有构造函数
     * 
     * @param success 是否成功
     * @param message 错误消息
     * @param fieldName 字段名
     * @param fieldValue 字段值
     */
    private ValidationResult(boolean success, String message, String fieldName, String fieldValue) {
        this.success = success;
        this.message = message;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errors = new ArrayList<>();
        
        if (!success && message != null) {
            this.errors.add(new ValidationError(fieldName, fieldValue, message));
        }
    }
    
    /**
     * 私有构造函数（用于多错误场景）
     * 
     * @param errors 错误列表
     */
    private ValidationResult(List<ValidationError> errors) {
        this.errors = new ArrayList<>(errors);
        this.success = errors.isEmpty();
        
        if (!errors.isEmpty()) {
            ValidationError firstError = errors.get(0);
            this.message = firstError.getMessage();
            this.fieldName = firstError.getFieldName();
            this.fieldValue = firstError.getFieldValue();
        } else {
            this.message = null;
            this.fieldName = null;
            this.fieldValue = null;
        }
    }
    
    /**
     * 创建成功的校验结果
     * @return 成功结果
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null, null, null);
    }
    
    /**
     * 创建失败的校验结果
     * @param message 错误消息
     * @param fieldName 字段名
     * @param fieldValue 字段值
     * @return 失败结果
     */
    public static ValidationResult failure(String message, String fieldName, String fieldValue) {
        return new ValidationResult(false, message, fieldName, fieldValue);
    }
    
    /**
     * 创建失败的校验结果
     * 
     * @param message 错误消息
     * @return 失败结果
     */
    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message, null, null);
    }
    
    /**
     * 创建包含多个错误的校验结果
     * 
     * @param errors 错误列表
     * @return 校验结果
     */
    public static ValidationResult of(List<ValidationError> errors) {
        return new ValidationResult(errors);
    }
    
    /**
     * 合并多个校验结果
     * 
     * @param results 校验结果列表
     * @return 合并后的校验结果
     */
    public static ValidationResult merge(ValidationResult... results) {
        List<ValidationError> allErrors = new ArrayList<>();
        for (ValidationResult result : results) {
            if (result.isFailure()) {
                allErrors.addAll(result.getErrors());
            }
        }
        return new ValidationResult(allErrors);
    }

    /**
     * 判断校验是否成功
     * 
     * @return true表示成功，false表示失败
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 判断校验是否失败
     * 
     * @return true表示失败，false表示成功
     */
    public boolean isFailure() {
        return !success;
    }

    /**
     * 获取主要错误消息（第一个错误的消息）
     * 
     * @return 错误消息，成功时返回null
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取主要错误字段名（第一个错误的字段名）
     * 
     * @return 字段名，成功时返回null
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 获取主要错误字段值（第一个错误的字段值）
     * 
     * @return 字段值，成功时返回null
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * 获取所有校验错误列表
     * 
     * @return 不可变的错误列表
     */
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }
    
    /**
     * 获取错误数量
     * 
     * @return 错误数量
     */
    public int getErrorCount() {
        return errors.size();
    }
    
    /**
     * 是否有多个错误
     * 
     * @return true表示有多个错误
     */
    public boolean hasMultipleErrors() {
        return errors.size() > 1;
    }
    
    /**
     * 获取所有错误消息的字符串表示
     * 
     * @return 错误消息字符串，多个错误用分号分隔
     */
    public String getAllErrorMessages() {
        return errors.stream()
                .map(ValidationError::getMessage)
                .collect(Collectors.joining("; "));
    }
    
    /**
     * 链式校验 - 与另一个校验结果合并
     * 
     * @param other 另一个校验结果
     * @return 合并后的校验结果
     */
    public ValidationResult and(ValidationResult other) {
        if (this.isSuccess() && other.isSuccess()) {
            return success();
        }
        
        List<ValidationError> allErrors = new ArrayList<>(this.errors);
        allErrors.addAll(other.errors);
        return new ValidationResult(allErrors);
    }
    
    /**
     * 链式校验 - 执行校验函数
     * 
     * @param validator 校验函数
     * @return 合并后的校验结果
     */
    public ValidationResult and(ValidationSupplier validator) {
        if (this.isFailure()) {
            return this; // 如果已经失败，直接返回
        }
        
        try {
            ValidationResult other = validator.validate();
            return this.and(other);
        } catch (Exception e) {
            return failure("校验过程中发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 校验函数接口
     */
    @FunctionalInterface
    public interface ValidationSupplier {
        ValidationResult validate() throws Exception;
    }
    
    @Override
    public String toString() {
        if (success) {
            return "ValidationResult{success=true}";
        } else {
            return String.format("ValidationResult{success=false, errorCount=%d, message='%s'}", 
                    errors.size(), message);
        }
    }
    
    /**
     * 校验错误信息
     */
    public static class ValidationError {
        private final String fieldName;
        private final String fieldValue;
        private final String message;
        
        public ValidationError(String fieldName, String fieldValue, String message) {
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
            this.message = message;
        }
        
        public String getFieldName() {
            return fieldName;
        }
        
        public String getFieldValue() {
            return fieldValue;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("字段 '%s' 校验失败: %s (值: %s)", fieldName, message, fieldValue);
        }
    }
}
