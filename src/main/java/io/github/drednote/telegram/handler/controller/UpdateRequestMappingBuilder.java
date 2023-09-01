package io.github.drednote.telegram.handler.controller;

import io.github.drednote.telegram.core.annotation.TelegramRequest;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.utils.Assert;
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

/**
 * The {@code UpdateRequestMappingBuilder} class is responsible for building Telegram request
 * mappings based on provided metadata. It allows constructing mappings with various combinations of
 * patterns, request types, and message types.
 * <p>
 * It accepts a {@link TelegramRequestMappingMetaData} or {@link TelegramRequest} as input and
 * generates multiple {@link UpdateRequestMapping} instances based on the provided data. The
 * generated mappings can then be processed using a consumer.
 *
 * @author Ivan Galushko
 * @see UpdateRequestMapping
 */
public class UpdateRequestMappingBuilder {

  private static final String DEFAULT_PATTERN = "**";
  private static final String DEFAULT_COMMAND_PATTERN = "/**";
  @NonNull
  private final List<String> patterns;
  @NonNull
  private final List<RequestType> requestTypes;
  @NonNull
  private final Set<MessageType> messageTypes;
  private final boolean exclusiveMessageType;

  /**
   * Constructs a {@code UpdateRequestMappingBuilder} using metadata from a
   * {@link TelegramRequest}.
   *
   * @param requestMapping The Telegram request mapping annotation, not null
   */
  public UpdateRequestMappingBuilder(TelegramRequest requestMapping) {
    this(new TelegramRequestMappingMetaData(requestMapping));
  }

  /**
   * Constructs a {@code UpdateRequestMappingBuilder} using provided metadata.
   *
   * @param metaData The metadata for constructing request mappings, not null
   */
  public UpdateRequestMappingBuilder(TelegramRequestMappingMetaData metaData) {
    Assert.required(metaData, "TelegramRequestMappingMetaData");
    this.patterns = createList(metaData.patterns);
    this.requestTypes = createList(metaData.requestTypes);
    this.messageTypes = createEnumSet(metaData.messageTypes, MessageType.class);
    this.exclusiveMessageType = metaData.exclusiveMessageType;
    if (!messageTypes.isEmpty() && requestTypes.isEmpty()) {
      requestTypes.add(RequestType.MESSAGE);
    }
  }

  /**
   * Processes and applies the provided consumer to each generated {@link UpdateRequestMapping}.
   *
   * @param consumer The consumer to apply to each mapping, not null
   */
  public void forEach(Consumer<UpdateRequestMapping> consumer) {
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
      @NonNull Consumer<UpdateRequestMapping> consumer
  ) {
    if (requestType == null && pattern == null) {
      consumer.accept(new UpdateRequestMapping(DEFAULT_PATTERN, null, Collections.emptySet()));
      consumer.accept(new UpdateRequestMapping(DEFAULT_COMMAND_PATTERN, RequestType.MESSAGE,
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
      @NonNull Consumer<UpdateRequestMapping> consumer
  ) {
    if (pattern == null) {
      if (requestType == RequestType.MESSAGE && messageTypes.contains(MessageType.COMMAND)) {
        consumer.accept(
            new UpdateRequestMapping(DEFAULT_COMMAND_PATTERN, requestType, messageTypes));
      } else {
        consumer.accept(new UpdateRequestMapping(DEFAULT_PATTERN, requestType, messageTypes));
      }
    } else {
      consumer.accept(new UpdateRequestMapping(pattern, requestType, messageTypes));
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

  /**
   * The {@code TelegramRequestMappingMetaData} class represents the metadata extracted from a
   * {@link TelegramRequest} annotation.
   *
   * @author Ivan Galushko
   */
  @AllArgsConstructor
  public static class TelegramRequestMappingMetaData {

    private final String[] patterns;
    private final RequestType[] requestTypes;
    private final MessageType[] messageTypes;
    private final boolean exclusiveMessageType;

    /**
     * Constructs a {@code TelegramRequestMappingMetaData} using a {@link TelegramRequest}
     * annotation.
     *
     * @param requestMapping The Telegram request mapping annotation, not null
     */
    public TelegramRequestMappingMetaData(TelegramRequest requestMapping) {
      Assert.required(requestMapping, "TelegramRequest annotation");
      this.patterns = requestMapping.value();
      this.requestTypes = requestMapping.requestType();
      this.messageTypes = requestMapping.messageType();
      this.exclusiveMessageType = requestMapping.exclusiveMessageType();
    }
  }
}
