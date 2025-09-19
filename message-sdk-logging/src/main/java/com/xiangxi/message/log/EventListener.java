package com.xiangxi.message.log;

/**
 * @author 初心
 * Create by on 2025/9/19 12:56 31
 */
@FunctionalInterface
public interface EventListener<E> {
    void onEvent(E event);
}
