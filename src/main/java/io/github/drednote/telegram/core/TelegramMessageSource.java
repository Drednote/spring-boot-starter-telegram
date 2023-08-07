package io.github.drednote.telegram.core;

import java.util.Locale;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.lang.Nullable;

public class TelegramMessageSource extends ReloadableResourceBundleMessageSource {

  @Override
  @Nullable
  public Locale getDefaultLocale() {
    return super.getDefaultLocale();
  }
}