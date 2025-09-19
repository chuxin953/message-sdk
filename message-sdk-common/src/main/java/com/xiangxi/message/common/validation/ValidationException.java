package com.xiangxi.message.common.validation;

/**
 * 参数校验异常
 * 当参数校验失败时抛出此异常
 */
public class ValidationException extends RuntimeException {
    
    private final String fieldName;
    private final String fieldValue;
    
    public ValidationException(String message) {
        super(message);
        this.fieldName = null;
        this.fieldValue = null;
    }
    
    public ValidationException(String fieldName, String fieldValue, String message) {
        super(String.format("字段 '%s' 校验失败: %s (值: %s)", fieldName, message, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public String getFieldValue() {
        return fieldValue;
    }
}
