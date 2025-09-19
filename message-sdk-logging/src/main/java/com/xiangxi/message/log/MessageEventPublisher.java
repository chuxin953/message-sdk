package com.xiangxi.message.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 初心
 * Create by on 2025/9/19 13:00 03
 */
public class MessageEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(MessageEventPublisher.class);

    private final Set<TypedListener<?>> listeners = new CopyOnWriteArraySet<>();
    private static final MessageEventPublisher INSTANCE = new MessageEventPublisher();

    // 异步线程池
    private final ExecutorService executor;


    // 私有构造函数，防止外部创建
    private MessageEventPublisher() {
        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        this.executor = new ThreadPoolExecutor(
                threads,
                threads,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactory() {
                    private final AtomicInteger count = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("message-event-thread-" + count.incrementAndGet());
                        t.setDaemon(true);
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // 队列满了让调用者执行，防止丢失事件
        );
    }
    // 获取单例
    public static MessageEventPublisher getInstance() {
        return INSTANCE;
    }

    /**
     * 注册监听器，自动去重
     */
    public <E> void register(Class<E> eventType, EventListener<E> listener) {
        Objects.requireNonNull(eventType, "eventType must not be null");
        Objects.requireNonNull(listener, "listener must not be null");
        listeners.add(new TypedListener<>(eventType, listener));
        log.debug("Registered listener {} for event {}", listener.getClass().getSimpleName(), eventType.getSimpleName());
    }

    public <E> void unregister(EventListener<E> listener) {
        Objects.requireNonNull(listener, "listener must not be null");
        listeners.removeIf(l -> l.listener.equals(listener));
        log.debug("Unregistered listener {}", listener.getClass().getSimpleName());
    }

    /**
     * 发布事件（异步执行）
     */
    public <E> void publish(E event) {
        Objects.requireNonNull(event, "event must not be null");
        if (listeners.isEmpty()) {
            return;
        }
        for (TypedListener<?> listener : listeners) {
            executor.submit(() -> {
                try {
                    listener.handle(event);
                } catch (Throwable t) {
                    log.warn("Event listener execution failed: {}", t.getMessage(), t);
                }
            });
        }
    }

    /**
     * 优雅关闭线程池
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
         * 内部封装的类型安全监听器
         */
        private record TypedListener<E>(Class<E> type, EventListener<E> listener) {

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
