package com.xiangxi.message.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 消息事件发布器 - 负责发布和分发消息相关事件
 *
 * <p>这是一个线程安全的事件发布器，支持异步事件处理。
 * 使用虚拟线程池来处理事件监听器，提供高性能和低资源消耗。</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * MessageEventPublisher publisher = MessageEventPublisher.getInstance();
 * publisher.register(MessageSentEvent.class, event -> {
 *     // 处理消息发送成功事件
 * });
 * publisher.publish(new MessageSentEvent(...));
 * }</pre>
 *
 * @author 初心
 * @since 1.0.0
 */
public class MessageEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MessageEventPublisher.class);

    // 线程池名称前缀
    private static final String THREAD_NAME_PREFIX = "message-event-";

    // 关闭超时时间（秒）
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 30;

    private final Set<TypedListener<?>> listeners = new CopyOnWriteArraySet<>();
    private static final MessageEventPublisher INSTANCE = new MessageEventPublisher();

    // 异步线程池
    private final ExecutorService executor;

    // 关闭状态标记
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    /**
     * 私有构造函数，防止外部创建
     */
    private MessageEventPublisher() {
        // 使用虚拟线程，每个任务一个线程，轻量且高并发
        this.executor = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual()
                        .name(THREAD_NAME_PREFIX, 0)
                        .factory()
        );
    }

    /**
     * 获取单例实例
     *
     * @return MessageEventPublisher 实例
     */
    public static MessageEventPublisher getInstance() {
        return INSTANCE;
    }

    /**
     * 注册事件监听器
     *
     * @param eventType 事件类型
     * @param listener 监听器
     * @param <E> 事件类型泛型
     * @throws IllegalStateException 如果发布器已关闭
     */
    public <E> void register(Class<E> eventType, EventListener<E> listener) {
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(listener, "listener must not be null");

        if (shutdown.get()) {
            throw new IllegalStateException("Event publisher has been shutdown");
        }

        listeners.add(new TypedListener<>(eventType, listener));
        log.debug("Registered listener {} for event {}",
                listener.getClass().getSimpleName(), eventType.getSimpleName());
    }

    /**
     * 注销事件监听器
     *
     * @param listener 要注销的监听器
     * @param <E> 事件类型泛型
     */
    public <E> void unregister(EventListener<E> listener) {
        Objects.requireNonNull(listener, "listener must not be null");

        boolean removed = listeners.removeIf(l -> l.listener.equals(listener));
        if (removed) {
            log.debug("Unregistered listener {}", listener.getClass().getSimpleName());
        }
    }

    /**
     * 发布事件（异步执行）
     *
     * @param event 要发布的事件
     * @param <E> 事件类型泛型
     * @throws IllegalStateException 如果发布器已关闭
     */
    public <E> void publish(E event) {
        Objects.requireNonNull(event, "event must not be null");

        if (shutdown.get()) {
            throw new IllegalStateException("Event publisher has been shutdown");
        }

        if (listeners.isEmpty()) {
            log.trace("No listeners registered for event: {}", event.getClass().getSimpleName());
            return;
        }

        // 异步处理事件
        for (TypedListener<?> listener : listeners) {
            executor.submit(() -> {
                try {
                    listener.handle(event);
                } catch (Throwable t) {
                    log.warn("Event listener execution failed for event {}: {}",
                            event.getClass().getSimpleName(), t.getMessage(), t);
                }
            });
        }
    }

    /**
     * 获取当前注册的监听器数量
     *
     * @return 监听器数量
     */
    public int getListenerCount() {
        return listeners.size();
    }

    /**
     * 检查是否已关闭
     *
     * @return 如果已关闭返回true
     */
    public boolean isShutdown() {
        return shutdown.get();
    }

    /**
     * 优雅关闭线程池
     *
     * @throws InterruptedException 如果等待过程中被中断
     */
    public void shutdown() throws InterruptedException {
        if (shutdown.compareAndSet(false, true)) {
            log.info("Shutting down MessageEventPublisher...");
            executor.shutdown();

            if (!executor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate gracefully, forcing shutdown");
                executor.shutdownNow();
            }

            log.info("MessageEventPublisher shutdown completed");
        }
    }

    /**
     * 内部封装的类型安全监听器
     *
     * @param <E> 事件类型
     */
    private record TypedListener<E>(Class<E> type, EventListener<E> listener) {

        /**
         * 处理事件
         *
         * @param event 事件对象
         */
        void handle(Object event) {
            if (type.isInstance(event)) {
                listener.onEvent(type.cast(event));
            }
        }

        @Override
        public int hashCode() {
            return listener.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof TypedListener<?> other)) return false;
            return listener.equals(other.listener);
        }
    }
}