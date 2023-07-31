package com.github.drednote.telegram.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class BotMessageSourceTest {

  @Test
  void shouldReturnDefaultLocaleIfSet() {
    BotMessageSource source = new BotMessageSource();
    Locale defaultLocale = Locale.forLanguageTag("ca");
    source.setDefaultLocale(defaultLocale);
    assertEquals(source.getDefaultLocale(), defaultLocale);
  }
}