package io.github.drednote.telegram.response.resolver;

import io.github.drednote.telegram.response.TelegramResponse;
import org.springframework.lang.Nullable;

public interface TelegramResponseTypesResolver {

    @Nullable
    TelegramResponse resolve(Object response);
}
