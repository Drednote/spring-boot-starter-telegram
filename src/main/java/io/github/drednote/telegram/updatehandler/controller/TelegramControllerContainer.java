package io.github.drednote.telegram.updatehandler.controller;

import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.method.HandlerMethod;

/**
 * A container class that implements the {@code HandlerMethodPopular} and {@code
 * ControllerRegistrar} interfaces. It manages the mapping between {@link TelegramRequestMapping}
 * instances and corresponding {@link HandlerMethod} instances.
 * <p>
 * Basically this class storing mappings between methods that marked with {@link TelegramRequest}
 * annotation and criteria to match them
 *
 * @author Ivan Galushko
 * @see TelegramRequestMapping
 * @see RequestHandler
 * @see TelegramRequest
 */
public class TelegramControllerContainer implements HandlerMethodPopular, ControllerRegistrar {

  private final Map<TelegramRequestMapping, HandlerMethod> mappingLookup = new HashMap<>();

  /**
   * Populates the provided {@link TelegramUpdateRequest} with the appropriate {@link
   * RequestHandler} based on the incoming update. It matches the update against the registered
   * {@link TelegramRequestMapping} instances and sets the appropriate handler for further
   * processing.
   * <p>
   * If the {@code TelegramUpdateRequest} matches with {@code TelegramRequestMapping}, than sorted
   * will be applied by {@link TelegramRequestMapping#compareTo(TelegramRequestMapping)} method.
   * After sorting finished will be picked the {@code min} element (with the highest priority)
   * <p>
   * To match with {@code TelegramUpdateRequest} using {@link TelegramRequestMapping#matches(TelegramUpdateRequest)}
   * method
   *
   * @param request The Telegram update request
   */
  @Override
  public void populate(TelegramUpdateRequest request) {
    String text = request.getText() == null ? "" : request.getText();

    mappingLookup.keySet().stream()
        .filter(requestMappingInfo -> requestMappingInfo.matches(request))
        .min(TelegramRequestMapping::compareTo)
        .ifPresent(mappingInfo -> {
          String pattern = mappingInfo.getPattern();
          Map<String, String> templateVariables =
              mappingInfo.getPathMatcher().extractUriTemplateVariables(pattern, text);
          request.setRequestHandler(
              new RequestHandler(mappingLookup.get(mappingInfo), templateVariables, pattern));
        });
  }

  /**
   * Registers a controller method with its corresponding {@link TelegramRequestMapping}. It
   * associates the provided bean and method with the specified mapping for later use in handling
   * incoming requests.
   *
   * @param bean    The bean containing the method
   * @param method  The controller method
   * @param mapping The Telegram request mapping
   * @throws IllegalStateException if an ambiguous mapping is detected
   */
  @Override
  public void register(Object bean, Method method, TelegramRequestMapping mapping) {
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
