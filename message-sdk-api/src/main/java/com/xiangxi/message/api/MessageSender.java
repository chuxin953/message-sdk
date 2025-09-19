package com.xiangxi.message.api;

import com.xiangxi.message.common.exception.MessageSendException;

/**
 * 消息发送器核心接口
 * <p>
 * 这是整个消息SDK的核心抽象接口，定义了统一的消息发送规范。
 * 所有具体的消息发送实现（如短信、邮件、推送等）都需要实现此接口。
 * </p>
 * 
 * <p>
 * 接口采用泛型设计，支持不同类型的配置、消息体和响应：
 * </p>
 * <ul>
 *   <li><strong>C</strong> - 配置类型（渠道特定的配置信息，如API密钥、服务器地址等）</li>
 *   <li><strong>M</strong> - 请求类型（消息体，如短信内容、邮件内容等）</li>
 *   <li><strong>R</strong> - 响应类型（发送结果，包含成功状态、消息ID、错误信息等）</li>
 * </ul>
 * 
 * <p>
 * 实现类需要通过Java SPI机制注册，在META-INF/services/com.xiangxi.message.api.MessageSender
 * 文件中声明实现类的全限定名，以便MessageSenderManager能够自动发现和加载。
 * </p>
 * 
 * <p>
 * 线程安全要求：实现类应当是线程安全的，因为MessageSenderManager会在多线程环境中复用同一个实例。
 * </p>
 * 
 * @param <C> 配置类型，包含发送消息所需的渠道特定配置
 * @param <M> 消息类型，表示要发送的消息内容和参数
 * @param <R> 响应类型，表示消息发送的结果
 * 
 * @author 初心
 * @since 1.0.0
 * @see com.xiangxi.message.manager.MessageSenderManager
 * @see com.xiangxi.message.common.exception.MessageSendException
 */
public interface MessageSender<C, M, R> {
    
    /**
     * 获取消息类型标识
     * <p>
     * 返回此发送器处理的消息类型，如"SMS"、"EMAIL"、"PUSH"等。
     * 此标识与channel()方法返回的渠道标识组合，形成唯一的路由键"type:channel"。
     * </p>
     * 
     * @return 消息类型标识，不能为null或空字符串
     * @see #channel()
     */
    String type();
    
    /**
     * 获取渠道标识
     * <p>
     * 返回此发送器对应的具体渠道，如"TENCENT_SMS"、"ALIYUN_EMAIL"等。
     * 此标识与type()方法返回的类型标识组合，形成唯一的路由键"type:channel"。
     * </p>
     * 
     * @return 渠道标识，不能为null或空字符串
     * @see #type()
     */
    String channel();
    
    /**
     * 发送消息
     * <p>
     * 使用指定的配置和消息内容发送消息。实现类应当：
     * </p>
     * <ul>
     *   <li>验证配置参数的有效性</li>
     *   <li>验证消息内容的完整性</li>
     *   <li>调用第三方服务API发送消息</li>
     *   <li>处理发送过程中的异常</li>
     *   <li>返回标准化的发送结果</li>
     * </ul>
     * 
     * @param config 发送配置，包含API密钥、服务器地址等渠道特定信息，不能为null
     * @param message 消息内容，包含接收者、消息体等信息，不能为null
     * @return 发送结果，包含成功状态、消息ID、错误信息等
     * @throws MessageSendException 当消息发送失败时抛出，包含详细的错误信息
     * @throws IllegalArgumentException 当参数为null或无效时抛出
     */
    R send(C config, M message) throws MessageSendException;
}


