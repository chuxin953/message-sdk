package com.xiangxi.message.client;

/**
 * 可重试的操作接口
 * <p>
 * 用于 {@link HttpClient#retry(RetryableAction, int)} 方法，
 * 允许操作抛出 {@link ClientException}。
 * </p>
 *
 * @param <T> 返回值类型
 * @author message-sdk
 * @since 1.0.0
 */
@FunctionalInterface
public interface RetryableAction<T> {
    /**
     * 执行操作
     *
     * @return 操作结果
     * @throws ClientException 操作失败时抛出
     */
    T execute() throws ClientException;
}

