package com.github.drednote.telegram.updatehandler;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

  UpdateHandlerResponse onUpdate(Update update);

}
