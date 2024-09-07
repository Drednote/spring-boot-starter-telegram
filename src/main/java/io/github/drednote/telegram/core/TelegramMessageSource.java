package io.github.drednote.telegram.core;

import io.github.drednote.telegram.core.request.UpdateRequest;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * The {@code TelegramMessageSource} class extends the {@code ReloadableResourceBundleMessageSource}.
 * <p>
 * Resolve message locale looking in UpdateRequest.
 *
 * @author Ivan Galushko
 */
public class TelegramMessageSource extends ReloadableResourceBundleMessageSource {

    @Nullable
    public String resolveResource(String code, UpdateRequest request, @Nullable String defaultMessage) {
        User user = request.getUser();
        Locale locale = Optional.ofNullable(user)
            .map(User::getLanguageCode)
            .map(Locale::forLanguageTag)
            .orElse(getDefaultLocale());
        return this.getMessage(code, null, defaultMessage, locale == null ? Locale.ENGLISH : locale);
    }

    @Nullable
    public String resolveResource(String code, UpdateRequest request) {
        return resolveResource(code, request, null);
    }

    @Override
    @Nullable
    public Locale getDefaultLocale() {
        return super.getDefaultLocale();
    }
}