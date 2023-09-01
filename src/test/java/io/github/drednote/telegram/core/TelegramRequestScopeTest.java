package io.github.drednote.telegram.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.drednote.telegram.core.TelegramRequestScopeTest.Config;
import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

@SpringBootTest(classes = {TelegramRequestScope.class, Config.class})
class TelegramRequestScopeTest {

  static final TelegramRequestScope scope = Mockito.spy(new TelegramRequestScope());
  @Autowired
  ApplicationContext applicationContext;
  @Autowired
  ObjectProvider<TestClass> bean;
  private static final String BEAN_NAME = "TestBeanName";

  @Nested
  class WithoutSpringTest {

    @Test
    void shouldCorrectManageBeans() {
      TelegramUpdateRequest request = Mockito.mock(TelegramUpdateRequest.class);
      UpdateRequestContext.saveRequest(request);
      when(request.getId()).thenReturn(1);
      TelegramRequestScope localScope = new TelegramRequestScope();

      Object bean = localScope.get(BEAN_NAME, Object::new);
      Object bean2 = localScope.get(BEAN_NAME, Object::new);

      assertThat(bean).isSameAs(bean2);
      assertThat(UpdateRequestContext.beanNames).containsExactly(Map.entry(1, List.of(BEAN_NAME)));

      when(request.getId()).thenReturn(2);

      bean2 = localScope.get(BEAN_NAME, Object::new);

      assertThat(bean).isNotSameAs(bean2);
      assertThat(UpdateRequestContext.beanNames).containsExactly(Map.entry(1, List.of(BEAN_NAME)),
          Map.entry(2, List.of(BEAN_NAME)));
    }
  }


  @Test
  @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
  void shouldRemoveFromScopeIfCallRemoveFromRequest() {
    TelegramUpdateRequest request = Mockito.mock(TelegramUpdateRequest.class);
    UpdateRequestContext.saveRequest(request);
    when(request.getId()).thenReturn(1);

    new UpdateRequestContext() {}.setApplicationContext(applicationContext);

    // Should not delete. It is trigger creating bean
    System.out.println("bean = " + bean.getObject());

    verify(scope).get(contains(BEAN_NAME), any());

    UpdateRequestContext.removeRequest(true);

    verify(scope).remove(contains(BEAN_NAME));
  }


  @Component(BEAN_NAME)
  @TelegramScope
  public static class TestClass {}

  @Configuration
  @Import(TestClass.class)
  static class Config {

    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
      CustomScopeConfigurer configurer = new CustomScopeConfigurer();
      configurer.addScope(TelegramRequestScope.BOT_SCOPE_NAME, scope);
      return configurer;
    }
  }
}