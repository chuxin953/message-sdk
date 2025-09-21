package com.xiangxi.message.sms;

import com.xiangxi.message.api.MessageSender;

/**
 * 短信发送器接口
 * <p>
 * 这是所有短信发送实现的统一接口，继承自{@link MessageSender}核心接口。
 * </p>
 * 
 * <p>
 * 实现此接口的类需要：
 * </p>
 * <ul>
 *   <li>实现{@link #type()}方法，返回"SMS"</li>
 *   <li>实现{@link #channel()}方法，返回具体的短信渠道标识</li>
 *   <li>实现{@link #send(Object, Object)}方法，处理短信发送逻辑</li>
 *   <li>通过SPI机制注册到系统中</li>
 * </ul>
 * 
 * <p>
 * 典型的实现包括：
 * </p>
 * <ul>
 *   <li>腾讯云短信：TencentSmsSender</li>
 *   <li>阿里云短信：AliyunSmsSender</li>
 *   <li>华为云短信：HuaweiSmsSender</li>
 * </ul>
 * 
 * @param <C> 短信配置类型，包含API密钥、签名、模板ID等信息
 * @param <M> 短信请求类型，包含接收者手机号、模板参数等信息
 * 
 * @author 初心
 * @since 1.0.0
 * @see MessageSender
 */
public interface ISmsSender<C, M, R> extends MessageSender<C, M, R> {
    // 继承父接口的所有方法，无需额外定义
}