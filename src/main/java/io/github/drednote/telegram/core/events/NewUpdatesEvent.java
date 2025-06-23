package io.github.drednote.telegram.core.events;

import org.springframework.context.ApplicationEvent;

public class NewUpdatesEvent extends ApplicationEvent {

    public NewUpdatesEvent(Object source) {
        super(source);
    }
}
