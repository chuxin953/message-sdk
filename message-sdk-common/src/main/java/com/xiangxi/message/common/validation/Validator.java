package com.xiangxi.message.common.validation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 参数校验工具类
 * 
 * <p>提供基于注解和编程式的参数校验功能，支持多种校验规则：</p>
 * <ul>
 *   <li>必填字段校验（@Required注解）</li>
 *   <li>字符串长度校验</li>
 *   <li>数值范围校验</li>
 *   <li>格式校验（邮箱、手机号等）</li>
 *   <li>集合和数组校验</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * // 注解方式校验
 * public class UserRequest {
 *     @Required(message = "用户名不能为空")
 *     private String username;
 *     
 *     @Required(fieldName = "邮箱地址")
 *     private String email;
 * }
 * 
 * UserRequest request = new UserRequest();
 * Validator.validate(request); // 抛出ValidationException
 * 
 * // 编程式校验
 * Validator.notNull(username, "用户名");
 * Validator.notEmpty(email, "邮箱");
 * Validator.isEmail(email, "邮箱格式不正确");
 * }</pre>
 * 
 * @author 初心
 * @version 1.0.0
 * @since 1.0.0
 */
public class Validator {
    
    // 常用正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$");
    
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
        "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    
    /**
     * 校验对象中带有@Required注解的字段
     * 
     * <p>使用反射机制遍历对象的所有字段，检查带有@Required注解的字段是否为空。</p>
     * 
     * @param obj 待校验的对象，不能为null
     * @throws ValidationException 当校验失败时抛出，包含所有校验错误信息
     * @throws IllegalArgumentException 当传入对象为null时抛出
     */
    public static void validate(Object obj) throws ValidationException {
        if (obj == null) {
            throw new IllegalArgumentException("校验对象不能为null");
        }

        List<ValidationException.ValidationError> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Required required = field.getAnnotation(Required.class);
            if (required != null) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (isEmpty(value)) {
                        String fieldName = getFieldName(field, required);
                        String message = required.message().isEmpty() ? 
                            fieldName + "不能为空" : required.message();
                        String valueStr = value == null ? "null" : value.toString();
                        errors.add(new ValidationException.ValidationError(fieldName, valueStr, message));
                    }
                } catch (IllegalAccessException e) {
                    errors.add(new ValidationException.ValidationError(field.getName(), 
                        "null", "字段访问异常: " + field.getName()));
                }
            }
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
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
            
            if (isEmpty(value)) {
                 String message = required.message().isEmpty() ? 
                     fieldName + "不能为空" : required.message();
                 String valueStr = value == null ? "null" : value.toString();
                 throw new ValidationException(fieldName, valueStr, message);
             }
             
         } catch (IllegalAccessException e) {
             throw new ValidationException(field.getName(), "null", "无法访问字段: " + field.getName());
         }
    }
    
    /**
     * 判断值是否为空
     * @param value 要检查的值
     * @return 如果为空返回true，否则返回false
     */
    private static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        
        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }
        
        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }
        
        if (value.getClass().isArray()) {
            return ((Object[]) value).length == 0;
        }
        
        return false;
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
    
    // ==================== 编程式校验方法 ====================
    
    /**
     * 校验对象不为null
     * 
     * @param value 待校验的值
     * @param fieldName 字段名称
     * @throws ValidationException 当值为null时抛出
     */
    public static void notNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, "null", fieldName + "不能为null");
        }
    }
    
    /**
     * 校验字符串不为空（null、空字符串、空白字符串）
     * 
     * @param value 待校验的字符串
     * @param fieldName 字段名称
     * @throws ValidationException 当字符串为空时抛出
     */
    public static void notEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            String valueStr = value == null ? "null" : "'" + value + "'";
            throw new ValidationException(fieldName, valueStr, fieldName + "不能为空");
        }
    }
    
    /**
     * 校验字符串长度
     * 
     * @param value 待校验的字符串
     * @param minLength 最小长度（包含）
     * @param maxLength 最大长度（包含）
     * @param fieldName 字段名称
     * @throws ValidationException 当长度不符合要求时抛出
     */
    public static void length(String value, int minLength, int maxLength, String fieldName) {
        notNull(value, fieldName);
        int length = value.length();
        if (length < minLength || length > maxLength) {
            throw new ValidationException(fieldName, value, 
                String.format("%s长度必须在%d-%d之间，当前长度: %d", fieldName, minLength, maxLength, length));
        }
    }
    
    /**
     * 校验数值范围
     * 
     * @param value 待校验的数值
     * @param min 最小值（包含）
     * @param max 最大值（包含）
     * @param fieldName 字段名称
     * @throws ValidationException 当数值超出范围时抛出
     */
    public static void range(Number value, Number min, Number max, String fieldName) {
        notNull(value, fieldName);
        double doubleValue = value.doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        
        if (doubleValue < minValue || doubleValue > maxValue) {
            throw new ValidationException(fieldName, value.toString(), 
                String.format("%s必须在%s-%s之间，当前值: %s", fieldName, min, max, value));
        }
    }
    
    /**
     * 校验邮箱格式
     * 
     * @param email 待校验的邮箱
     * @param fieldName 字段名称
     * @throws ValidationException 当邮箱格式不正确时抛出
     */
    public static void isEmail(String email, String fieldName) {
        notEmpty(email, fieldName);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(fieldName, email, fieldName + "格式不正确");
        }
    }
    
    /**
     * 校验手机号格式（中国大陆）
     * 
     * @param phone 待校验的手机号
     * @param fieldName 字段名称
     * @throws ValidationException 当手机号格式不正确时抛出
     */
    public static void isPhone(String phone, String fieldName) {
        notEmpty(phone, fieldName);
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException(fieldName, phone, fieldName + "格式不正确");
        }
    }
    
    /**
     * 校验身份证号格式（中国大陆）
     * 
     * @param idCard 待校验的身份证号
     * @param fieldName 字段名称
     * @throws ValidationException 当身份证号格式不正确时抛出
     */
    public static void isIdCard(String idCard, String fieldName) {
        notEmpty(idCard, fieldName);
        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            throw new ValidationException(fieldName, idCard, fieldName + "格式不正确");
        }
    }
    
    /**
     * 校验集合不为空
     * 
     * @param collection 待校验的集合
     * @param fieldName 字段名称
     * @throws ValidationException 当集合为null或空时抛出
     */
    public static void notEmpty(Collection<?> collection, String fieldName) {
        if (collection == null || collection.isEmpty()) {
            String valueStr = collection == null ? "null" : "空集合";
            throw new ValidationException(fieldName, valueStr, fieldName + "不能为空");
        }
    }
    
    /**
     * 校验数组不为空
     * 
     * @param array 待校验的数组
     * @param fieldName 字段名称
     * @throws ValidationException 当数组为null或空时抛出
     */
    public static void notEmpty(Object[] array, String fieldName) {
        if (array == null || array.length == 0) {
            String valueStr = array == null ? "null" : "空数组";
            throw new ValidationException(fieldName, valueStr, fieldName + "不能为空");
        }
    }
    
    /**
     * 自定义校验
     * 
     * @param condition 校验条件，true表示校验通过
     * @param fieldName 字段名称
     * @param fieldValue 字段值
     * @param message 错误消息
     * @throws ValidationException 当条件不满足时抛出
     */
    public static void isTrue(boolean condition, String fieldName, Object fieldValue, String message) {
        if (!condition) {
            String valueStr = fieldValue == null ? "null" : fieldValue.toString();
            throw new ValidationException(fieldName, valueStr, message);
        }
    }
}
