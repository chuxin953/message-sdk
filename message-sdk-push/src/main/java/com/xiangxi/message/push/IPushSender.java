package com.xiangxi.message.push;

import com.xiangxi.message.api.MessageSender;

/**
 * 推送消息发送器接口
 * 
 * <p>所有推送渠道实现均应实现此接口，统一被上层通过 {@link #type()} 与 {@link #channel()} 路由。</p>
 * 
 * <p>约定：</p>
 * <ul>
 *   <li>{@link #type()} 必须返回统一的推送类型标识（例如："PUSH"）</li>
 *   <li>{@link #channel()} 返回渠道编码（例如："JPUSH_PUSH"、"FCM_PUSH"）</li>
 *   <li>{@link #send(Object, Object)} 抛出的异常应转换为统一的业务异常，由上层捕获并记录</li>
 * </ul>
 * 
 * @param <C> 配置类型（含API密钥、服务器地址等渠道初始化所需信息）
 * @param <M> 消息类型（含设备Token、标题、内容等发送所需信息）
 * @param <R> 渠道响应类型（应被调用方转换为统一响应模型）
 * 
 * @author 初心
 * @since 1.0.0
 */
public interface IPushSender<C, M, R> extends MessageSender<C, M, R> {
    // 继承父接口的所有方法，无需额外定义
}