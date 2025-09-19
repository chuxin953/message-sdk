package com.xiangxi.message.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 必填参数校验注解
 * 用于标记请求参数中的必填字段
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
    
    /**
     * 错误消息
     * @return 当字段为空时显示的错误消息
     */
    String message() default "字段不能为空";
    
    /**
     * 字段名称（用于错误消息）
     * @return 字段的显示名称
     */
    String fieldName() default "";
}
