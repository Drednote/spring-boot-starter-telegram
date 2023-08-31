package io.github.drednote.telegram.updatehandler.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.updatehandler.controller.TelegramRequestMappingBuilder.TelegramRequestMappingMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TelegramRequestMappingBuilderTest {

  @Test
  void shouldMatchEverything() {
    TelegramRequestMappingBuilder commandBuilder = new TelegramRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null, null, null, false));
    ArrayList<TelegramRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(2)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "**")
        .hasFieldOrPropertyWithValue("requestType", null)
        .hasFieldOrPropertyWithValue("messageTypes", Collections.EMPTY_SET);
    assertThat(infos).element(1)
        .hasFieldOrPropertyWithValue("pattern", "/**")
        .hasFieldOrPropertyWithValue("requestType", RequestType.MESSAGE)
        .hasFieldOrPropertyWithValue("messageTypes", Set.of(MessageType.COMMAND));
  }

  @Test
  void shouldCorrectCreateMappingIfExistsAllFields() {
    TelegramRequestMappingBuilder commandBuilder = new TelegramRequestMappingBuilder(
        new TelegramRequestMappingMetaData(new String[]{"hi"}, new RequestType[]{RequestType.MESSAGE},
            new MessageType[]{MessageType.COMMAND}, false));

    ArrayList<TelegramRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "hi")
        .hasFieldOrPropertyWithValue("requestType", RequestType.MESSAGE)
        .hasFieldOrPropertyWithValue("messageTypes", Set.of(MessageType.COMMAND));
  }

  @Test
  void shouldAddMessageIfCommand() {
    TelegramRequestMappingBuilder commandBuilder = new TelegramRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null, null, new MessageType[]{MessageType.COMMAND}, false));
    ArrayList<TelegramRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "/**")
        .hasFieldOrPropertyWithValue("requestType", RequestType.MESSAGE)
        .hasFieldOrPropertyWithValue("messageTypes", Set.of(MessageType.COMMAND));
  }

  @Test
  void shouldAddPatternIfRequestTypeNotNull() {
    TelegramRequestMappingBuilder commandBuilder = new TelegramRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null, new RequestType[]{RequestType.POLL}, null, false));
    ArrayList<TelegramRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "**")
        .hasFieldOrPropertyWithValue("requestType", RequestType.POLL)
        .hasFieldOrPropertyWithValue("messageTypes", Collections.EMPTY_SET);
  }

  @Test
  void shouldCorrectCreateMappingIfExistsOnlyPattern() {
    TelegramRequestMappingBuilder commandBuilder = new TelegramRequestMappingBuilder(
        new TelegramRequestMappingMetaData(new String[]{"hi"}, null,
            null, false));

    ArrayList<TelegramRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "hi")
        .hasFieldOrPropertyWithValue("requestType", null)
        .hasFieldOrPropertyWithValue("messageTypes", Collections.EMPTY_SET);
  }

  @Test
  void shouldMapToTwoMappings() {
    TelegramRequestMappingBuilder commandBuilder = new TelegramRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null,
            new RequestType[]{RequestType.MESSAGE, RequestType.POLL},
            new MessageType[]{MessageType.COMMAND}, false));

    ArrayList<TelegramRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(2)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "/**")
        .hasFieldOrPropertyWithValue("requestType", RequestType.MESSAGE)
        .hasFieldOrPropertyWithValue("messageTypes", Set.of(MessageType.COMMAND));
    assertThat(infos).element(1)
        .hasFieldOrPropertyWithValue("pattern", "**")
        .hasFieldOrPropertyWithValue("requestType", RequestType.POLL)
        .hasFieldOrPropertyWithValue("messageTypes", Collections.EMPTY_SET);
  }

  @Test
  void shouldMapToOneExclusiveMapping() {
    TelegramRequestMappingBuilder commandBuilder = new TelegramRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null,
            new RequestType[]{RequestType.MESSAGE},
            new MessageType[]{MessageType.COMMAND, MessageType.PHOTO}, true));

    ArrayList<TelegramRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "/**")
        .hasFieldOrPropertyWithValue("requestType", RequestType.MESSAGE)
        .hasFieldOrPropertyWithValue("messageTypes", Set.of(MessageType.COMMAND, MessageType.PHOTO));
  }
}