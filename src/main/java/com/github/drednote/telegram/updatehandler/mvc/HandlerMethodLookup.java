package com.github.drednote.telegram.updatehandler.mvc;

import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface HandlerMethodLookup {

  HandlerMethod lookup(Update update);
}
