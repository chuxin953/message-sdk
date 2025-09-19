package com.xiangxi.message.common.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * 校验结果
 * 包含校验是否成功以及错误信息
 */
public class ValidationResult {
    
    private final boolean success;
    private final String message;
    private final String fieldName;
    private final String fieldValue;
    private final List<ValidationError> errors;
    
    private ValidationResult(boolean success, String message, String fieldName, String fieldValue) {
        this.success = success;
        this.message = message;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errors = new ArrayList<>();
        
        if (!success) {
            this.errors.add(new ValidationError(fieldName, fieldValue, message));
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
     * @param message 错误消息
     * @return 失败结果
     */
    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message, null, null);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public boolean isFailure() {
        return !success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public String getFieldValue() {
        return fieldValue;
    }
    
    public List<ValidationError> getErrors() {
        return new ArrayList<>(errors);
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
