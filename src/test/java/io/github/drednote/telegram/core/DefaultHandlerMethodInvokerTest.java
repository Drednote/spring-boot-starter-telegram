package io.github.drednote.telegram.core;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.invoke.DefaultHandlerMethodInvoker;
import io.github.drednote.telegram.core.resolver.CompositeArgumentResolver;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

class DefaultHandlerMethodInvokerTest {

  final DefaultHandlerMethodInvoker invoker = new DefaultHandlerMethodInvoker(
      new CompositeArgumentResolver(new ArrayList<>()));

  @Test
  void shouldCallMethodWithKnownArguments() throws NoSuchMethodException {
    Method testMethod = Bean.class.getDeclaredMethod("testMethod", Long.class);
    Bean bean = Mockito.spy(Bean.class);
    Update update = UpdateUtils.createEmpty();
    try {
      invoker.invoke(UpdateRequestUtils.createMockRequest(update),
          new HandlerMethod(bean, testMethod));
    } catch (Exception e) {
      assertThat(e).isNull();
    }
    Mockito.verify(bean, Mockito.only()).testMethod(update.getMessage().getChatId());
  }

  @Test
  void shouldThrowExceptionIfArgumentsNotKnown() throws NoSuchMethodException {
    Method testMethod = Bean.class.getDeclaredMethod("testMethod2", BigDecimal.class);
    Bean bean = Mockito.spy(Bean.class);
    Update update = UpdateUtils.createEmpty();
    try {
      invoker.invoke(UpdateRequestUtils.createMockRequest(update),
          new HandlerMethod(bean, testMethod));
    } catch (Exception e) {
      assertThat(e).isNotNull().isInstanceOf(IllegalStateException.class);
    }
    Mockito.verify(bean, Mockito.never()).testMethod2(
        BigDecimal.valueOf(update.getMessage().getChatId()));
  }

  public static class Bean {

    public String testMethod(Long updateId) {
      return updateId.toString();
    }

    public String testMethod2(BigDecimal updateId) {
      return updateId.toString();
    }
  }
}