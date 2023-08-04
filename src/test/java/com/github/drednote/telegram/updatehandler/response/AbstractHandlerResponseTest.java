package com.github.drednote.telegram.updatehandler.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.drednote.telegram.core.BotMessageSource;
import com.github.drednote.telegram.core.request.BotRequest;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.User;

class AbstractHandlerResponseTest {

  private AbstractHandlerResponse handlerResponse;
  private BotRequest botRequest;
  private User user;
  private BotMessageSource messageSource;

  @BeforeEach
  void setUp() {
    // Create a mock BotRequest object
    botRequest = mock(BotRequest.class);

    // Create a mock User object
    user = mock(User.class);

    // Create a mock BotMessageSource object
    messageSource = mock(BotMessageSource.class);

    // Create an instance of AbstractHandlerResponse with a code and default message
    handlerResponse = new AbstractHandlerResponse("CODE", "Default Message") {
      @Override
      public void process(BotRequest request) {

      }
    };

    // Set the messageSource field of the handlerResponse object
    handlerResponse.setMessageSource(messageSource);
  }

  @Test
  void withCodeAndMessageSourceReturnsMessageForLocale() {
    // Set up the mock objects
    when(botRequest.getUser()).thenReturn(user);
    when(user.getLanguageCode()).thenReturn("en-US");

    // Set up the messageSource mock to return a specific message
    when(messageSource.getMessage(eq("CODE"), eq(null), eq("Default Message"), any(Locale.class)))
        .thenReturn("Localized Message");

    // Call the method under test
    String message = handlerResponse.getMessageForLocale(botRequest);

    // Verify the interactions and assertions
    verify(messageSource).getMessage(eq("CODE"), eq(null), eq("Default Message"),
        any(Locale.class));
    assertEquals("Localized Message", message);
  }

  @Test
  void withoutCodeOrMessageSourceReturnsDefaultMessage() {
    // Set up the mock objects
    when(botRequest.getUser()).thenReturn(user);
    handlerResponse.setMessageSource(null);

    // Call the method under test
    String message = handlerResponse.getMessageForLocale(botRequest);

    // Verify the interactions and assertions
    verifyZeroInteractions(messageSource);
    assertEquals("Default Message", message);
  }

  @Test
  void withoutUserReturnsDefaultMessage() {
    // Call the method under test
    String message = handlerResponse.getMessageForLocale(botRequest);

    // Verify the interactions and assertions
    verifyZeroInteractions(messageSource);
    assertEquals("Default Message", message);
  }

  @Test
  void ifMessageSourceReturnNullThenReturnDefaultMessage() {
    when(botRequest.getUser()).thenReturn(user);
    // Call the method under test
    String message = handlerResponse.getMessageForLocale(botRequest);

    // Verify the interactions and assertions
    verifyOneInteractions(messageSource);
    assertEquals("Default Message", message);
  }

  private void verifyZeroInteractions(BotMessageSource messageSource) {
    verify(messageSource, Mockito.never())
        .getMessage(eq("CODE"), eq(null), eq("Default Message"), any(Locale.class));
  }

  private void verifyOneInteractions(BotMessageSource messageSource) {
    verify(messageSource, Mockito.atMostOnce())
        .getMessage(eq("CODE"), eq(null), eq("Default Message"), any(Locale.class));
  }
}

