package io.github.drednote.telegram.core.events;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@ConditionalOnClass({ApplicationEventMulticaster.class, ThreadPoolTaskExecutor.class})
@Slf4j
public class AsyncEventAutoConfiguration {

    /**
     * Bean for creating async {@link org.springframework.context.ApplicationEventPublisher}
     * <p>
     * forkJoinPool async executor that takes threads from the default Thread Pool
     *
     * @return async {@link org.springframework.context.ApplicationEventPublisher}
     */
    @Bean
    @ConditionalOnMissingBean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        var eventMulticaster = new SimpleApplicationEventMulticaster() {
            @Override
            public void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType) {
                ResolvableType type = (eventType != null ? eventType : ResolvableType.forInstance(event));
                Executor executor = getTaskExecutor();
                boolean isAsync = AsyncApplicationEvent.class.isAssignableFrom(event.getClass());

                for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {
                    if (executor != null && isAsync) {
                        executor.execute(() -> invokeListener(listener, event));
                    } else {
                        invokeListener(listener, event);
                    }
                }
            }
        };
        eventMulticaster.setTaskExecutor(ForkJoinPool.commonPool());
        log.info("Create AsyncApplicationEventMulticaster and set ForkJoinPool as executor");
        return eventMulticaster;
    }
}