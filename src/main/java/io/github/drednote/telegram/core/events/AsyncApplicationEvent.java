package io.github.drednote.telegram.core.events;

import java.time.Clock;
import org.springframework.context.ApplicationEvent;

public abstract class AsyncApplicationEvent extends ApplicationEvent {

    protected AsyncApplicationEvent(Object source) {
        super(source);
    }

    protected AsyncApplicationEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
