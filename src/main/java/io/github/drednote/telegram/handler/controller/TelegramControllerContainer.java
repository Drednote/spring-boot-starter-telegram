package io.github.drednote.telegram.handler.controller;

import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.method.HandlerMethod;

/**
 * A container class that implements the {@code HandlerMethodPopular} and
 * {@code ControllerRegistrar} interfaces. It manages the mapping between
 * {@link UpdateRequestMapping} instances and corresponding {@link HandlerMethod} instances.
 * <p>
 * Basically this class storing mappings between methods that marked with {@link TelegramRequest}
 * annotation and criteria to match them
 *
 * @author Ivan Galushko
 * @see UpdateRequestMapping
 * @see RequestHandler
 * @see TelegramRequest
 */
public class TelegramControllerContainer implements HandlerMethodPopular, ControllerRegistrar {

  private final Map<UpdateRequestMapping, HandlerMethod> mappingLookup = new HashMap<>();

  /**
   * Populates the provided {@link UpdateRequest} with the appropriate
   * {@link RequestHandler} based on the incoming update. It matches the update against the
   * registered {@link UpdateRequestMapping} instances and sets the appropriate handler for
   * further processing.
   * <p>
   * If the {@code UpdateRequest} matches with {@code UpdateRequestMapping}, than sorted
   * will be applied by {@link UpdateRequestMapping#compareTo(UpdateRequestMapping)} method.
   * After sorting finished will be picked the {@code min} element (with the highest priority)
   * <p>
   * To match with {@code UpdateRequest} using
   * {@link UpdateRequestMapping#matches(UpdateRequest)} method
   *
   * @param request The Telegram update request
   */
  @Override
  public void populate(UpdateRequest request) {
    String text = request.getText() == null ? "" : request.getText();

    mappingLookup.keySet().stream()
        .filter(requestMappingInfo -> requestMappingInfo.matches(request))
        .min(UpdateRequestMapping::compareTo)
        .ifPresent(mappingInfo -> {
          String pattern = mappingInfo.getPattern();
          Map<String, String> templateVariables =
              mappingInfo.getPathMatcher().extractUriTemplateVariables(pattern, text);
          request.setRequestHandler(
              new RequestHandler(mappingLookup.get(mappingInfo), templateVariables, pattern));
        });
  }

  /**
   * Registers a controller method with its corresponding {@link UpdateRequestMapping}. It
   * associates the provided bean and method with the specified mapping for later use in handling
   * incoming requests.
   *
   * @param bean    The bean containing the method
   * @param method  The controller method
   * @param mapping The Telegram request mapping
   * @throws IllegalStateException if an ambiguous mapping is detected
   */
  @Override
  public void register(Object bean, Method method, UpdateRequestMapping mapping) {
    HandlerMethod handlerMethod = new HandlerMethod(bean, method);
    HandlerMethod existingHandler = mappingLookup.get(mapping);
    if (existingHandler != null) {
      throw new IllegalStateException(
          "\nAmbiguous mapping. Cannot map '" + handlerMethod.getBean() + "' method \n" +
              handlerMethod + "\nto " + mapping + ": There is already '" +
              existingHandler.getBean() + "' bean method\n" + existingHandler + " mapped.");
    }
    mappingLookup.put(mapping, handlerMethod);
  }
}
