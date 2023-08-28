package io.github.drednote.telegram.core;

import java.util.Locale;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.lang.Nullable;

/**
 * The {@code TelegramMessageSource} class extends the {@code ReloadableResourceBundleMessageSource}
 * class for only one purpose. It overrides the {@link #getDefaultLocale()} method to make it public
 * and return the default locale configured in the superclass
 *
 * @author Ivan Galushko
 */
public class TelegramMessageSource extends ReloadableResourceBundleMessageSource {

  @Override
  @Nullable
  public Locale getDefaultLocale() {
    return super.getDefaultLocale();
  }
}