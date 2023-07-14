package com.github.drednote.telegram.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.drednote.telegram.UpdateUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.ReflectionUtils.MethodFilter;

class ImmutableUpdateRequestTest {

  final ImmutableUpdateRequest request = new ImmutableUpdateRequest(
      new UpdateRequest(UpdateUtils.createEmpty(), null, null));

  @Test
  void shouldBeImmutableOnSetters() {
    for (Method method : MethodIntrospector.selectMethods(ImmutableUpdateRequest.class,
        (MethodFilter) method ->
            method.getName().startsWith("set") && Modifier.isPublic(method.getModifiers()))) {
      try {
        method.invoke(request, new Object[]{null});
      } catch (IllegalAccessException | InvocationTargetException e) {
        Throwable cause = e.getCause();
        assertThat(cause).isNotNull().isInstanceOf(UnsupportedOperationException.class);
      }
    }

  }
}