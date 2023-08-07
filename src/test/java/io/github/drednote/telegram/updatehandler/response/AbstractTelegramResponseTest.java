package io.github.drednote.telegram.updatehandler.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.User;

class AbstractTelegramResponseTest {

  private AbstractTelegramResponse handlerResponse;
  private TelegramUpdateRequest telegramUpdateRequest;
  private User user;
  private TelegramMessageSource messageSource;

  @BeforeEach
  void setUp() {
    // Create a mock BotRequest object
    telegramUpdateRequest = mock(TelegramUpdateRequest.class);

    // Create a mock User object
    user = mock(User.class);

    // Create a mock BotMessageSource object
    messageSource = mock(TelegramMessageSource.class);

    // Create an instance of AbstractHandlerResponse with a code and default message
    handlerResponse = new AbstractTelegramResponse("CODE", "Default Message") {
      @Override
      public void process(TelegramUpdateRequest request) {

      }
    };

    // Set the messageSource field of the handlerResponse object
    handlerResponse.setMessageSource(messageSource);
  }

  @Test
  void withCodeAndMessageSourceReturnsMessageForLocale() {
    // Set up the mock objects
    when(telegramUpdateRequest.getUser()).thenReturn(user);
    when(user.getLanguageCode()).thenReturn("en-US");

    // Set up the messageSource mock to return a specific message
    when(messageSource.getMessage(eq("CODE"), eq(null), eq("Default Message"), any(Locale.class)))
        .thenReturn("Localized Message");

    // Call the method under test
    String message = handlerResponse.getMessageForLocale(telegramUpdateRequest);

    // Verify the interactions and assertions
    verify(messageSource).getMessage(eq("CODE"), eq(null), eq("Default Message"),
        any(Locale.class));
    assertEquals("Localized Message", message);
  }

  @Test
  void withoutCodeOrMessageSourceReturnsDefaultMessage() {
    // Set up the mock objects
    when(telegramUpdateRequest.getUser()).thenReturn(user);
    handlerResponse.setMessageSource(null);

    // Call the method under test
    String message = handlerResponse.getMessageForLocale(telegramUpdateRequest);

    // Verify the interactions and assertions
    verifyZeroInteractions(messageSource);
    assertEquals("Default Message", message);
  }

  @Test
  void withoutUserReturnsDefaultMessage() {
    // Call the method under test
    String message = handlerResponse.getMessageForLocale(telegramUpdateRequest);

    // Verify the interactions and assertions
    verifyZeroInteractions(messageSource);
    assertEquals("Default Message", message);
  }

  @Test
  void ifMessageSourceReturnNullThenReturnDefaultMessage() {
    when(telegramUpdateRequest.getUser()).thenReturn(user);
    // Call the method under test
    String message = handlerResponse.getMessageForLocale(telegramUpdateRequest);

    // Verify the interactions and assertions
    verifyOneInteractions(messageSource);
    assertEquals("Default Message", message);
  }

  private void verifyZeroInteractions(TelegramMessageSource messageSource) {
    verify(messageSource, Mockito.never())
        .getMessage(eq("CODE"), eq(null), eq("Default Message"), any(Locale.class));
  }

  private void verifyOneInteractions(TelegramMessageSource messageSource) {
    verify(messageSource, Mockito.atMostOnce())
        .getMessage(eq("CODE"), eq(null), eq("Default Message"), any(Locale.class));
  }
}

