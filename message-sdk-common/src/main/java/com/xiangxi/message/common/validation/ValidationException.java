package com.xiangxi.message.common.validation;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * 参数校验异常
 * 
 * <p>当参数校验失败时抛出此异常。这是一个运行时异常，
 * 通常表示程序逻辑错误或输入数据不符合预期格式。</p>
 * 
 * <p>异常场景包括：</p>
 * <ul>
 *   <li>必填字段为空或null</li>
 *   <li>字符串长度不符合要求</li>
 *   <li>数值超出有效范围</li>
 *   <li>格式不正确（如邮箱、手机号等）</li>
 *   <li>集合或数组为空</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * try {
 *     Validator.validate(request);
 * } catch (ValidationException e) {
 *     log.warn("参数校验失败: {}", e.getMessage());
 *     return ResponseEntity.badRequest().body(e.getFieldName() + ": " + e.getMessage());
 * }
 * }</pre>
 * 
 * @author 初心
 * @version 1.0.0
 * @since 1.0.0
 */
public class ValidationException extends RuntimeException {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 校验失败的字段名称
     */
    private final String fieldName;
    
    /**
     * 校验失败的字段值
     */
    private final String fieldValue;
    
    /**
     * 校验错误列表（支持多个错误）
     */
    private final List<ValidationError> errors;
    
    /**
     * 构造一个通用的校验异常
     * 
     * @param message 异常消息
     */
    public ValidationException(String message) {
        super(message);
        this.fieldName = null;
        this.fieldValue = null;
        this.errors = new ArrayList<>();
    }
    
    /**
     * 构造一个带有字段信息的校验异常
     * 
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @param message 错误消息
     */
    public ValidationException(String fieldName, String fieldValue, String message) {
        super(String.format("字段 '%s' 校验失败: %s (值: %s)", fieldName, message, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errors = new ArrayList<>();
        this.errors.add(new ValidationError(fieldName, fieldValue, message));
    }
    
    /**
     * 构造一个包含多个校验错误的异常
     * 
     * @param errors 校验错误列表
     */
    public ValidationException(List<ValidationError> errors) {
        super(buildMessage(errors));
        this.errors = new ArrayList<>(errors);
        
        // 如果只有一个错误，设置字段信息
        if (errors.size() == 1) {
            ValidationError error = errors.get(0);
            this.fieldName = error.getFieldName();
            this.fieldValue = error.getFieldValue();
        } else {
            this.fieldName = null;
            this.fieldValue = null;
        }
    }
    
    /**
     * 构造一个带有原因的校验异常
     * 
     * @param message 异常消息
     * @param cause 原始异常
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.fieldName = null;
        this.fieldValue = null;
        this.errors = new ArrayList<>();
    }
    
    /**
     * 获取校验失败的字段名称
     * 
     * @return 字段名称，可能为null
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * 获取校验失败的字段值
     * 
     * @return 字段值，可能为null
     */
    public String getFieldValue() {
        return fieldValue;
    }
    
    /**
     * 获取所有校验错误
     * 
     * @return 校验错误列表的副本
     */
    public List<ValidationError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * 检查是否有多个校验错误
     * 
     * @return 如果有多个错误返回true，否则返回false
     */
    public boolean hasMultipleErrors() {
        return errors.size() > 1;
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
     * 构建多个错误的消息
     * 
     * @param errors 错误列表
     * @return 组合后的错误消息
     */
    private static String buildMessage(List<ValidationError> errors) {
        if (errors == null || errors.isEmpty()) {
            return "参数校验失败";
        }
        
        if (errors.size() == 1) {
            return errors.get(0).toString();
        }
        
        StringBuilder sb = new StringBuilder("参数校验失败，共 ");
        sb.append(errors.size()).append(" 个错误:\n");
        
        for (int i = 0; i < errors.size(); i++) {
            sb.append("  ").append(i + 1).append(". ").append(errors.get(i).toString());
            if (i < errors.size() - 1) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 校验错误信息
     */
    public static class ValidationError {
        private final String fieldName;
        private final String fieldValue;
        private final String message;
        
        /**
         * 构造校验错误
         * 
         * @param fieldName 字段名称
         * @param fieldValue 字段值
         * @param message 错误消息
         */
        public ValidationError(String fieldName, String fieldValue, String message) {
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
            this.message = message;
        }
        
        /**
         * 获取字段名称
         * 
         * @return 字段名称
         */
        public String getFieldName() {
            return fieldName;
        }
        
        /**
         * 获取字段值
         * 
         * @return 字段值
         */
        public String getFieldValue() {
            return fieldValue;
        }
        
        /**
         * 获取错误消息
         * 
         * @return 错误消息
         */
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("字段 '%s' 校验失败: %s (值: %s)", fieldName, message, fieldValue);
        }
    }
}
