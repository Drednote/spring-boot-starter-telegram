package io.github.drednote.telegram.handler.controller;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.datasource.permission.Permission.DefaultPermission;
import io.github.drednote.telegram.exception.type.HasRoleValidationException;
import io.github.drednote.telegram.handler.controller.annotation.HasRole;
import io.github.drednote.telegram.handler.controller.annotation.HasRole.StrategyMatching;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;

class HasRoleRequestValidatorTest {

  private static final String TEST_ROLE = "TestRole";
  private static final String SECOND_ROLE = "SecondRole";
  HasRoleRequestValidator validator = new HasRoleRequestValidator();

  @Test
  void shouldValidateRoleIfHasRoleAnnotationExist() throws NoSuchMethodException {
    // intersection
    DefaultUpdateRequest request = createRequest(Set.of(TEST_ROLE), "hasRole");
    assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();

    DefaultUpdateRequest request2 = createRequest(Set.of("NoRole"), "hasRole");
    assertThatThrownBy(() -> validator.validate(request2))
        .isInstanceOf(HasRoleValidationException.class);
    DefaultUpdateRequest request3 = createRequest(Set.of(), "hasRole");
    assertThatThrownBy(() -> validator.validate(request3))
        .isInstanceOf(HasRoleValidationException.class);

    // complete match
    DefaultUpdateRequest request4 = createRequest(Set.of(TEST_ROLE), "completeMatch");
    assertThatThrownBy(() -> validator.validate(request4))
        .isInstanceOf(HasRoleValidationException.class);

    DefaultUpdateRequest request5 = createRequest(Set.of(TEST_ROLE, SECOND_ROLE), "completeMatch");
    assertThatCode(() -> validator.validate(request5)).doesNotThrowAnyException();
  }

  @Test
  void shouldNotValidateIfHasRoleAnnotationDoesntExist() throws NoSuchMethodException {
    DefaultUpdateRequest request = createRequest(Set.of(), "noRole");
    assertThatCode(() -> validator.validate(request)).doesNotThrowAnyException();
  }

  @NonNull
  private static DefaultUpdateRequest createRequest(Set<String> roles, String methodName)
      throws NoSuchMethodException {
    DefaultUpdateRequest request = UpdateRequestUtils.createMockRequest(UpdateUtils.createEmpty());
    request.setRequestHandler(new RequestHandler(
        new HandlerMethod(new TestClass(),
            TestClass.class.getDeclaredMethod(methodName)), null, null));

    request.setPermission(new DefaultPermission(1L, roles));
    return request;
  }

  static class TestClass {

    @HasRole({TEST_ROLE, SECOND_ROLE})
    public void hasRole() {
    }

    @HasRole(value = {TEST_ROLE, SECOND_ROLE}, strategyMatching = StrategyMatching.COMPLETE_MATCH)
    public void completeMatch() {
    }

    public void noRole() {
    }
  }
}