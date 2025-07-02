package io.github.drednote.telegram.response.resolver;

import io.github.drednote.telegram.response.TelegramResponse;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

public interface TelegramResponseTypesResolver extends Ordered {

    @Nullable
    TelegramResponse resolve(Object response);

    default int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
