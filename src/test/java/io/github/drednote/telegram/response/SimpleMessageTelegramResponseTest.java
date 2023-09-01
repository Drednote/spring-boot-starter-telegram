package io.github.drednote.telegram.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.drednote.telegram.core.TelegramMessageSource;
import io.github.drednote.telegram.core.request.UpdateRequest;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.objects.User;

class SimpleMessageTelegramResponseTest {

  private SimpleMessageTelegramResponse handlerResponse;
  private UpdateRequest updateRequest;
  private User user;
  private TelegramMessageSource messageSource;

  @BeforeEach
  void setUp() {
    // Create a mock BotRequest object
    updateRequest = mock(UpdateRequest.class);

    // Create a mock User object
    user = mock(User.class);

    // Create a mock BotMessageSource object
    messageSource = mock(TelegramMessageSource.class);

    // Create an instance of AbstractHandlerResponse with a code and default message
    handlerResponse = new SimpleMessageTelegramResponse("CODE", "Default Message") {
      @Override
      public void process(@NonNull UpdateRequest request) {

      }
    };

    // Set the messageSource field of the handlerResponse object
    handlerResponse.setMessageSource(messageSource);
  }

  @Test
  void withCodeAndMessageSourceReturnsMessageForLocale() {
    // Set up the mock objects
    when(updateRequest.getUser()).thenReturn(user);
    when(user.getLanguageCode()).thenReturn("en-US");

    // Set up the messageSource mock to return a specific message
    when(messageSource.getMessage(eq("CODE"), eq(null), eq("Default Message"), any(Locale.class)))
        .thenReturn("Localized Message");

    // Call the method under test
    String message = handlerResponse.getMessageForLocale(updateRequest);

    // Verify the interactions and assertions
    verify(messageSource).getMessage(eq("CODE"), eq(null), eq("Default Message"),
        any(Locale.class));
    assertEquals("Localized Message", message);
  }

  @Test
  void withoutCodeOrMessageSourceReturnsDefaultMessage() {
    // Set up the mock objects
    when(updateRequest.getUser()).thenReturn(user);
    handlerResponse.setMessageSource(null);

    // Call the method under test
    String message = handlerResponse.getMessageForLocale(updateRequest);

    // Verify the interactions and assertions
    verifyZeroInteractions(messageSource);
    assertEquals("Default Message", message);
  }

  @Test
  void withoutUserReturnsDefaultMessage() {
    // Call the method under test
    String message = handlerResponse.getMessageForLocale(updateRequest);

    // Verify the interactions and assertions
    verifyZeroInteractions(messageSource);
    assertEquals("Default Message", message);
  }

  @Test
  void ifMessageSourceReturnNullThenReturnDefaultMessage() {
    when(updateRequest.getUser()).thenReturn(user);
    // Call the method under test
    String message = handlerResponse.getMessageForLocale(updateRequest);

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

