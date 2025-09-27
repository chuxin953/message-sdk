package com.xiangxi.message.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 必填字段注解
 * 
 * <p>用于标记必填字段，配合验证工具使用。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
    
    /**
     * 错误消息
     * @return 当字段为空时显示的错误消息
     */
    String message() default "此字段为必填项";

    /**
     * 字段显示名称
     * @return 字段显示名称
     */
    String fieldName() default "";
}
