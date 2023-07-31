package com.github.drednote.telegram.core;

import com.github.drednote.telegram.TelegramProperties;
import com.github.drednote.telegram.datasource.Permission;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import com.github.drednote.telegram.updatehandler.scenario.Scenario;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface BotRequest {

  //-----------getters for Update-----------

  @NonNull
  Integer getId();

  @NonNull
  Long getChatId();

  @NonNull
  RequestType getMessageType();

  @Nullable
  String getText();

  @Nullable
  Message getMessage();

  @Nullable
  Chat getChat();

  @Nullable
  User getUser();

  @NonNull
  Update getOrigin();

  //-----------other getters-----------

  @NonNull
  AbsSender getAbsSender();

  @NonNull
  Permission getPermission();

  @Nullable
  Scenario getScenario();

  @Nullable
  HandlerResponse getResponse();

  @Nullable
  Throwable getError();

  @NonNull
  TelegramProperties getProperties();
}
