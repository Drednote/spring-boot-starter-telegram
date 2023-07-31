package com.github.drednote.telegram.updatehandler.mvc;

import java.util.Map;
import org.springframework.web.method.HandlerMethod;

public record RequestHandler(
    HandlerMethod handlerMethod,
    Map<String, String> templateVariables,
    String basePattern) {}
