package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.core.request.MessageType;
import com.github.drednote.telegram.core.request.TelegramRequestMapping;
import com.github.drednote.telegram.core.request.RequestType;
import com.github.drednote.telegram.updatehandler.mvc.annotation.TelegramRequest;
import com.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class TelegramRequestMappingBuilder {

  private static final String DEFAULT_PATTERN = "**";
  private static final String DEFAULT_COMMAND_PATTERN = "/**";
  @NonNull
  private final List<String> patterns;
  @NonNull
  private final List<RequestType> requestTypes;
  @NonNull
  private final Set<MessageType> messageTypes;
  private final boolean exclusiveMessageType;

  public TelegramRequestMappingBuilder(TelegramRequest requestMapping) {
    this(new TelegramRequestMappingMetaData(requestMapping));
  }

  public TelegramRequestMappingBuilder(TelegramRequestMappingMetaData metaData) {
    Assert.notNull(metaData, "BotRequestMappingMetaData");
    this.patterns = createList(metaData.patterns);
    this.requestTypes = createList(metaData.requestTypes);
    this.messageTypes = createEnumSet(metaData.messageTypes, MessageType.class);
    this.exclusiveMessageType = metaData.exclusiveMessageType;
    if (!messageTypes.isEmpty() && requestTypes.isEmpty()) {
      requestTypes.add(RequestType.MESSAGE);
    }
  }

  public void forEach(Consumer<TelegramRequestMapping> consumer) {
    Assert.notNull(consumer, "consumer");
    if (requestTypes.isEmpty() && patterns.isEmpty()) {
      process(null, null, consumer);
    } else if (!requestTypes.isEmpty() && !patterns.isEmpty()) {
      for (String pattern : patterns) {
        for (RequestType requestType : requestTypes) {
          process(pattern, requestType, consumer);
        }
      }
    } else if (!patterns.isEmpty()) {
      for (String pattern : patterns) {
        process(pattern, null, consumer);
      }
    } else {
      for (RequestType requestType : requestTypes) {
        process(null, requestType, consumer);
      }
    }
  }

  private void process(
      @Nullable String pattern, @Nullable RequestType requestType,
      @NonNull Consumer<TelegramRequestMapping> consumer
  ) {
    if (requestType == null && pattern == null) {
      consumer.accept(new TelegramRequestMapping(DEFAULT_PATTERN, null, Collections.emptySet()));
      consumer.accept(new TelegramRequestMapping(DEFAULT_COMMAND_PATTERN, RequestType.MESSAGE,
          Set.of(MessageType.COMMAND)));
    } else if (requestType == RequestType.MESSAGE) {
      if (!messageTypes.isEmpty()) {
        if (exclusiveMessageType) {
          doProcess(pattern, requestType, messageTypes, consumer);
        } else {
          for (MessageType messageType : messageTypes) {
            doProcess(pattern, requestType, Set.of(messageType), consumer);
          }
        }
      } else {
        doProcess(pattern, requestType, Collections.emptySet(), consumer);
      }
    } else {
      doProcess(pattern, requestType, Collections.emptySet(), consumer);
    }
  }

  private void doProcess(
      @Nullable String pattern, @Nullable RequestType requestType,
      @NonNull Set<MessageType> messageTypes,
      @NonNull Consumer<TelegramRequestMapping> consumer
  ) {
    if (pattern == null) {
      if (requestType == RequestType.MESSAGE && messageTypes.contains(MessageType.COMMAND)) {
        consumer.accept(new TelegramRequestMapping(DEFAULT_COMMAND_PATTERN, requestType, messageTypes));
      } else {
        consumer.accept(new TelegramRequestMapping(DEFAULT_PATTERN, requestType, messageTypes));
      }
    } else {
      consumer.accept(new TelegramRequestMapping(pattern, requestType, messageTypes));
    }
  }

  private <T> List<T> createList(@Nullable T[] array) {
    return array == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(array));
  }

  private <T extends Enum<T>> Set<T> createEnumSet(@Nullable T[] array, Class<T> clazz) {
    EnumSet<T> set = EnumSet.noneOf(clazz);
    if (array != null) {
      set.addAll(Arrays.asList(array));
    }
    return set;
  }

  @AllArgsConstructor
  public static class TelegramRequestMappingMetaData {

    private final String[] patterns;
    private final RequestType[] requestTypes;
    private final MessageType[] messageTypes;
    private final boolean exclusiveMessageType;

    public TelegramRequestMappingMetaData(TelegramRequest requestMapping) {
      Assert.notNull(requestMapping, "BotRequestMapping annotation");
      this.patterns = requestMapping.value();
      this.requestTypes = requestMapping.requestType();
      this.messageTypes = requestMapping.messageType();
      this.exclusiveMessageType = requestMapping.exclusiveMessageType();
    }
  }
}
