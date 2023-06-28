package com.github.drednote.telegram.core;

import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
public enum RequestType {
  MESSAGE(Update::hasMessage,
      update -> Optional.of(update)
          .map(Update::getMessage)
          .map(Message::getText)
          .orElse(null)
  ),
  COMMAND(update ->
      RequestType.MESSAGE.isFitRequestType.apply(update) && update.getMessage().isCommand(),
      RequestType.MESSAGE.textFunction),

  ALL(update -> true, update -> null);

  final Function<Update, Boolean> isFitRequestType;
  final Function<Update, String> textFunction;
}
