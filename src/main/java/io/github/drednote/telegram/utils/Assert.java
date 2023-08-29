package io.github.drednote.telegram.utils;

import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/**
 * Utility class containing various assertion methods for checking conditions and validating input
 * parameters. These methods throw IllegalArgumentException with appropriate error messages when
 * assertions fail.
 */
public interface Assert {

  /**
   * Asserts that the given object is not null.
   *
   * @param object    The object to check for null
   * @param paramName The name of the parameter being checked
   * @throws IllegalArgumentException if the object is null
   */
  static void notNull(@Nullable Object object, String paramName) {
    if (object == null) {
      throw new IllegalArgumentException("'%s' cannot be null".formatted(paramName));
    }
  }

  /**
   * Asserts that the given object is not null with a custom error message.
   *
   * @param object  The object to check for null
   * @param message The custom error message
   * @throws IllegalArgumentException if the object is null
   */
  static void notNullC(@Nullable Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Asserts that the given object is required (not null).
   * <p>
   * Generally uses in constructors
   *
   * @param object    The object to check for null
   * @param paramName The name of the parameter being checked
   * @throws IllegalArgumentException if the object is null
   */
  static void required(@Nullable Object object, String paramName) {
    if (object == null) {
      throw new IllegalArgumentException("'%s' is required".formatted(paramName));
    }
  }

  /**
   * Asserts that the given string is not blank.
   *
   * @param string    The string to check for emptiness
   * @param paramName The name of the parameter being checked
   * @throws IllegalArgumentException if the string is empty or blank
   */
  static void notEmpty(@Nullable String string, String paramName) {
    if (StringUtils.isBlank(string)) {
      throw new IllegalArgumentException("'%s' cannot be empty".formatted(paramName));
    }
  }

  /**
   * Asserts that the given collection is not empty.
   *
   * @param collection The collection to check for emptiness
   * @param paramName  The name of the parameter being checked
   * @throws IllegalArgumentException if the collection is empty or null
   */
  static void notEmpty(@Nullable Collection<?> collection, String paramName) {
    if (CollectionUtils.isEmpty(collection)) {
      throw new IllegalArgumentException("'%s' must have at least one object".formatted(paramName));
    }
  }

  /**
   * Asserts that the given map is not empty.
   *
   * @param map       The map to check for emptiness
   * @param paramName The name of the parameter being checked
   * @throws IllegalArgumentException if the map is empty or null
   */
  static void notEmpty(@Nullable Map<?, ?> map, String paramName) {
    if (CollectionUtils.isEmpty(map)) {
      throw new IllegalArgumentException("'%s' must have at least one object".formatted(paramName));
    }
  }

  /**
   * Asserts that the given object is specified (not null, not blank string, not empty
   * collection/map).
   *
   * @param object    The object to check for being specified
   * @param paramName The name of the parameter being checked
   * @throws IllegalArgumentException if the object is null, blank string, or an empty
   *                                  collection/map
   */
  static void specify(@Nullable Object object, String paramName) {
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
