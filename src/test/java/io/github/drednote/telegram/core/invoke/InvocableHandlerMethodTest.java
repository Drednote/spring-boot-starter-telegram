package io.github.drednote.telegram.core.invoke;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.method.HandlerMethod;

class InvocableHandlerMethodTest {

  @ParameterizedTest
  @MethodSource("factory")
  void shouldUnwrapExceptions(String name, Class<? extends Throwable> exception) throws NoSuchMethodException {
    TestA bean = new TestA();
    InvocableHandlerMethod handlerMethod = new InvocableHandlerMethod(
        new HandlerMethod(bean, bean.getClass().getDeclaredMethod(name)));

    assertThatThrownBy(handlerMethod::invoke).isInstanceOf(exception);
  }

  static Stream<Arguments> factory() {
    return Stream.of(
        arguments("runtimeException", RuntimeException.class),
        arguments("exception", Exception.class),
        arguments("error", Error.class)
    );
  }

  static class TestA {

    public void runtimeException() {
      throw new RuntimeException();
    }

    public void exception() throws Exception {
      throw new Exception();
    }

    public void error() {
      throw new Error();
    }
  }
}