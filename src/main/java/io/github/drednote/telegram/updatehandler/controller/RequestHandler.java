package io.github.drednote.telegram.updatehandler.controller;

import java.util.Map;
import org.springframework.web.method.HandlerMethod;

/**
 * Represents a handler for processing incoming requests. This record encapsulates the information
 * required to handle a specific request using a {@link HandlerMethod}.
 *
 * @author Ivan Galushko
 */
public record RequestHandler(
    /*
      The method that will be invoked
     */
    HandlerMethod handlerMethod,
    /*
      The template variables extracted from the request
      Will be using to populate method parameters marked with TelegramPatternVariable
     */
    Map<String, String> templateVariables,
    /*
      The base pattern
     */
    String basePattern) {

}
