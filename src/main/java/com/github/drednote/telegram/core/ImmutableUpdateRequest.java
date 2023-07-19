package com.github.drednote.telegram.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.drednote.telegram.datasource.Permission;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public final class ImmutableUpdateRequest extends UpdateRequest {

  public ImmutableUpdateRequest(UpdateRequest request) {
    super(request);
  }

  @Override
  public void setHandlerMethod(@Nullable HandlerMethod handlerMethod) {
    throwImmutableException("handlerMethod");
  }

  @Override
  public void setTemplateVariables(@Nullable Map<String, String> templateVariables) {
    throwImmutableException("templateVariables");
  }

  @Override
  public void setBasePattern(@Nullable String basePattern) {
    throwImmutableException("basePattern");
  }

  @Override
  public void setState(Object state) {
    throwImmutableException("state");
  }

  @Override
  public void setResponse(HandlerResponse response) {
    throwImmutableException("response");
  }

  @Override
  public void setObjectMapper(ObjectMapper objectMapper) {
    throwImmutableException("objectMapper");
  }

  @Override
  public void setPermission(@NonNull Permission permission) {
    throwImmutableException("permission");
  }

  @Override
  public void setError(@Nullable Throwable error) {
    throwImmutableException("error");
  }

  private static void throwImmutableException(String parameter) {
    throw new UnsupportedOperationException(
        "This class is immutable, cannot set parameter '%s'".formatted(parameter));
  }
}
