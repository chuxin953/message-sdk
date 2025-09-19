package com.xiangxi.message.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 必填参数校验注解
 * 
 * <p>用于标记请求参数中的必填字段，支持多种数据类型的空值检查：</p>
 * <ul>
 *   <li>null值检查</li>
 *   <li>空字符串检查（包括空白字符串）</li>
 *   <li>空集合检查</li>
 *   <li>空数组检查</li>
 * </ul>
 * 
 * <p>支持的目标：</p>
 * <ul>
 *   <li>字段（Field）- 用于对象属性校验</li>
 *   <li>参数（Parameter）- 用于方法参数校验</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * public class UserRequest {
 *     @Required(message = "用户名不能为空")
 *     private String username;
 *     
 *     @Required(fieldName = "邮箱地址", message = "邮箱不能为空")
 *     private String email;
 *     
 *     @Required(fieldName = "角色列表")
 *     private List<String> roles;
 *     
 *     // 使用默认错误消息
 *     @Required
 *     private String password;
 * }
 * 
 * // 方法参数校验
 * public void updateUser(@Required(fieldName = "用户ID") String userId, 
 *                       @Required UserRequest request) {
 *     // 方法实现
 * }
 * }</pre>
 * 
 * <p>校验规则：</p>
 * <ul>
 *   <li>字符串：null、空字符串("")、空白字符串("   ")都视为空</li>
 *   <li>集合：null或size()为0视为空</li>
 *   <li>数组：null或length为0视为空</li>
 *   <li>其他对象：null视为空</li>
 * </ul>
 * 
 * @author 初心
 * @version 1.0.0
 * @since 1.0.0
 * @see Validator#validate(Object)
 * @see ValidationException
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Required {
    
    /**
     * 自定义错误消息
     * 
     * <p>当字段校验失败时显示的错误消息。如果为空，将使用默认格式：
     * "{fieldName}不能为空"</p>
     * 
     * <p>支持占位符：</p>
     * <ul>
     *   <li>{fieldName} - 字段名称</li>
     *   <li>{value} - 字段值</li>
     * </ul>
     * 
     * @return 错误消息，默认为空字符串（使用默认消息）
     */
    String message() default "";
    
    /**
     * 字段显示名称
     * 
     * <p>用于错误消息中的字段名称。如果为空，将使用字段的实际名称。
     * 建议使用用户友好的中文名称。</p>
     * 
     * <p>示例：</p>
     * <pre>{@code
     * @Required(fieldName = "用户名")
     * private String username; // 错误消息："用户名不能为空"
     * 
     * @Required
     * private String email; // 错误消息："email不能为空"
     * }</pre>
     * 
     * @return 字段显示名称，默认为空字符串（使用字段名）
     */
    String fieldName() default "";
    
    /**
     * 校验分组
     * 
     * <p>用于分组校验，可以在不同场景下校验不同的字段组合。
     * 例如：创建用户时校验基本信息，更新用户时校验扩展信息。</p>
     * 
     * <p>使用示例：</p>
     * <pre>{@code
     * public class UserRequest {
     *     @Required(groups = {Create.class, Update.class})
     *     private String username;
     *     
     *     @Required(groups = Create.class)
     *     private String password;
     *     
     *     @Required(groups = Update.class)
     *     private Long id;
     * }
     * 
     * // 分组接口
     * public interface Create {}
     * public interface Update {}
     * }</pre>
     * 
     * @return 校验分组数组，默认为空（所有场景都校验）
     */
    Class<?>[] groups() default {};
}
