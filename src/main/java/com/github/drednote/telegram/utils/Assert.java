package com.github.drednote.telegram.utils;

import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

public interface Assert {

  static void notNull(Object object, String paramName) {
    if (object == null) {
      throw new IllegalArgumentException("'%s' must not be null".formatted(paramName));
    }
  }

  static void required(Object object, String paramName) {
    if (object == null) {
      throw new IllegalArgumentException("'%s' property is required".formatted(paramName));
    }
  }

  static void notEmpty(String string, String paramName) {
    if (StringUtils.isBlank(string)) {
      throw new IllegalArgumentException("'%s' must not be empty".formatted(paramName));
    }
  }

  static void notEmpty(Collection<?> collection, String paramName) {
    if (CollectionUtils.isEmpty(collection)) {
      throw new IllegalArgumentException("'%s' must have at least one object".formatted(paramName));
    }
  }

  static void notEmpty(Map<?, ?> map, String paramName) {
    if (CollectionUtils.isEmpty(map)) {
      throw new IllegalArgumentException("'%s' must have at least one object".formatted(paramName));
    }
  }

  static void specify(Object object, String paramName) {
    if (object instanceof String string && StringUtils.isBlank(string)) {
      throw new IllegalArgumentException(
          "Consider specify not empty '%s' param".formatted(paramName));
    }
    if (
        object instanceof Collection<?> collection && CollectionUtils.isEmpty(collection)
            || object instanceof Map<?, ?> map && CollectionUtils.isEmpty(map)
    ) {
      throw new IllegalArgumentException(
          "Consider specify at least one object in '%s'".formatted(paramName));
    }
    if (object == null) {
      throw new IllegalArgumentException(
          "Consider specify not null '%s' param".formatted(paramName));
    }
  }
}
