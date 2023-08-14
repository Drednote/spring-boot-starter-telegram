package io.github.drednote.telegram.core.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.mvc.RequestHandler;
import io.github.drednote.telegram.updatehandler.mvc.annotation.TelegramPathVariable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;

class PathVariableArgumentResolverTest {

  private PathVariableArgumentResolver resolver;
  private TelegramUpdateRequest request;
  private MethodParameter methodParameter;

  @BeforeEach
  public void setUp() {
    resolver = new PathVariableArgumentResolver();
    request = mock(TelegramUpdateRequest.class);
  }

  @Test
  void shouldSupportIfAnnotationIsPresent() throws NoSuchMethodException {
    methodParameter = new MethodParameter(
        TestClass.class.getDeclaredMethod("supportIfAnnotationIsPresent", String.class), 0);
    boolean supports = resolver.supportsParameter(methodParameter);

    assertTrue(supports);
  }

  @Test
  void shouldNotSupportIfAnnotationIsNotPresent() throws NoSuchMethodException {
    methodParameter = new MethodParameter(
        TestClass.class.getDeclaredMethod("notSupportIfAnnotationIsNotPresent", String.class), 0);
    boolean supports = resolver.supportsParameter(methodParameter);

    assertFalse(supports);

    assertThrows(IllegalArgumentException.class,
        () -> resolver.resolveArgument(methodParameter, request));
  }

  @Test
  void shouldPassAllVariablesToMap() throws NoSuchMethodException {
    methodParameter = new MethodParameter(
        TestClass.class.getDeclaredMethod("passAllVariablesToMap", Map.class), 0);
    Map<String, String> map = Map.of("variable", "value");
    Mockito.when(request.getRequestHandler())
        .thenReturn(new RequestHandler(null, map, ""));

    Object result = resolver.resolveArgument(methodParameter, request);

    assertTrue(result instanceof Map<?, ?>);
    assertFalse(((Map<?, ?>) result).isEmpty());
    assertEquals(map, result);
  }

  @Test
  void shouldThrowExceptionIfRequiredAndNoValue()
      throws NoSuchMethodException {
    methodParameter = new MethodParameter(
        TestClass.class.getDeclaredMethod("throwExceptionIfRequiredAndNoValue", Optional.class), 0);
    Mockito.when(request.getRequestHandler())
        .thenReturn(new RequestHandler(null, Collections.emptyMap(), ""));

    assertThrows(IllegalStateException.class,
        () -> resolver.resolveArgument(methodParameter, request));
  }

  @Test
  void shouldReturnOptionalIfNotRequiredAndNoValue()
      throws NoSuchMethodException {
    methodParameter = new MethodParameter(
        TestClass.class.getDeclaredMethod("returnOptionalIfNotRequiredAndNoValue", Optional.class),
        0);
    Mockito.when(request.getRequestHandler())
        .thenReturn(new RequestHandler(null, Collections.emptyMap(), ""));

    Object result = resolver.resolveArgument(methodParameter, request);

    assertTrue(result instanceof Optional);
    assertFalse(((Optional<?>) result).isPresent());
  }

  @SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
  static class TestClass {

    void supportIfAnnotationIsPresent(@TelegramPathVariable String variable) {

    }

    void notSupportIfAnnotationIsNotPresent(String variable) {

    }

    void passAllVariablesToMap(@TelegramPathVariable Map<String, String> variables) {

    }

    void throwExceptionIfRequiredAndNoValue(@TelegramPathVariable Optional<String> variable) {

    }

    void returnOptionalIfNotRequiredAndNoValue(
        @TelegramPathVariable(required = false) Optional<String> variable) {

    }
  }
}