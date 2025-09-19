package com.xiangxi.message.common.validation;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * 参数校验工具类
 * 使用反射机制校验带有@Required注解的字段
 */
public class Validator {
    
    /**
     * 校验对象的所有字段
     * @param obj 要校验的对象
     * @throws ValidationException 校验失败时抛出
     */
    public static void validate(Object obj) throws ValidationException {
        if (obj == null) {
            throw new ValidationException("校验对象不能为空");
        }
        
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            Required required = field.getAnnotation(Required.class);
            if (required != null) {
                validateField(obj, field, required);
            }
        }
    }
    
    /**
     * 校验单个字段
     * @param obj 对象实例
     * @param field 字段
     * @param required 注解信息
     * @throws ValidationException 校验失败时抛出
     */
    private static void validateField(Object obj, Field field, Required required) throws ValidationException {
        try {
            field.setAccessible(true);
            Object value = field.get(obj);
            
            String fieldName = getFieldName(field, required);
            
            // 检查是否为null
            if (value == null) {
                throw new ValidationException(fieldName, "null", required.message());
            }
            
            // 检查字符串是否为空
            if (value instanceof String) {
                String strValue = (String) value;
                if (strValue.trim().isEmpty()) {
                    throw new ValidationException(fieldName, strValue, required.message());
                }
            }
            
            // 检查集合是否为空
            if (value instanceof Collection) {
                Collection<?> collection = (Collection<?>) value;
                if (collection.isEmpty()) {
                    throw new ValidationException(fieldName, "空集合", required.message());
                }
            }
            
            // 检查数组是否为空
            if (value.getClass().isArray()) {
                Object[] array = (Object[]) value;
                if (array.length == 0) {
                    throw new ValidationException(fieldName, "空数组", required.message());
                }
            }
            
        } catch (IllegalAccessException e) {
            throw new ValidationException("无法访问字段: " + field.getName());
        }
    }
    
    /**
     * 获取字段显示名称
     * @param field 字段
     * @param required 注解
     * @return 字段名称
     */
    private static String getFieldName(Field field, Required required) {
        if (required.fieldName() != null && !required.fieldName().trim().isEmpty()) {
            return required.fieldName();
        }
        return field.getName();
    }
    
    /**
     * 校验对象，返回校验结果而不是抛出异常
     * @param obj 要校验的对象
     * @return 校验结果
     */
    public static ValidationResult validateQuietly(Object obj) {
        try {
            validate(obj);
            return ValidationResult.success();
        } catch (ValidationException e) {
            return ValidationResult.failure(e.getMessage(), e.getFieldName(), e.getFieldValue());
        }
    }
}
