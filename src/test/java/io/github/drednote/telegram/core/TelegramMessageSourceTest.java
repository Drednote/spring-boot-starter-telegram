package io.github.drednote.telegram.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class TelegramMessageSourceTest {

  @Test
  void shouldReturnDefaultLocaleIfSet() {
    TelegramMessageSource source = new TelegramMessageSource();
    Locale defaultLocale = Locale.forLanguageTag("ca");
    source.setDefaultLocale(defaultLocale);
    assertEquals(source.getDefaultLocale(), defaultLocale);
  }
}