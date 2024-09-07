package io.github.drednote.telegram.handler.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.UpdateRequestMappingBuilder;
import io.github.drednote.telegram.core.request.UpdateRequestMappingBuilder.TelegramRequestMappingMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UpdateRequestMappingBuilderTest {

  @Test
  void shouldMatchEverything() {
    UpdateRequestMappingBuilder commandBuilder = new UpdateRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null, null, null, false));
    ArrayList<UpdateRequestMapping> infos = new ArrayList<>();
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
    UpdateRequestMappingBuilder commandBuilder = new UpdateRequestMappingBuilder(
        new TelegramRequestMappingMetaData(new String[]{"hi"}, new RequestType[]{RequestType.MESSAGE},
            new MessageType[]{MessageType.COMMAND}, false));

    ArrayList<UpdateRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "hi")
        .hasFieldOrPropertyWithValue("requestType", RequestType.MESSAGE)
        .hasFieldOrPropertyWithValue("messageTypes", Set.of(MessageType.COMMAND));
  }

  @Test
  void shouldAddMessageIfCommand() {
    UpdateRequestMappingBuilder commandBuilder = new UpdateRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null, null, new MessageType[]{MessageType.COMMAND}, false));
    ArrayList<UpdateRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "/**")
        .hasFieldOrPropertyWithValue("requestType", RequestType.MESSAGE)
        .hasFieldOrPropertyWithValue("messageTypes", Set.of(MessageType.COMMAND));
  }

  @Test
  void shouldAddPatternIfRequestTypeNotNull() {
    UpdateRequestMappingBuilder commandBuilder = new UpdateRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null, new RequestType[]{RequestType.POLL}, null, false));
    ArrayList<UpdateRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "**")
        .hasFieldOrPropertyWithValue("requestType", RequestType.POLL)
        .hasFieldOrPropertyWithValue("messageTypes", Collections.EMPTY_SET);
  }

  @Test
  void shouldCorrectCreateMappingIfExistsOnlyPattern() {
    UpdateRequestMappingBuilder commandBuilder = new UpdateRequestMappingBuilder(
        new TelegramRequestMappingMetaData(new String[]{"hi"}, null,
            null, false));

    ArrayList<UpdateRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "hi")
        .hasFieldOrPropertyWithValue("requestType", null)
        .hasFieldOrPropertyWithValue("messageTypes", Collections.EMPTY_SET);
  }

  @Test
  void shouldMapToTwoMappings() {
    UpdateRequestMappingBuilder commandBuilder = new UpdateRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null,
            new RequestType[]{RequestType.MESSAGE, RequestType.POLL},
            new MessageType[]{MessageType.COMMAND}, false));

    ArrayList<UpdateRequestMapping> infos = new ArrayList<>();
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
    UpdateRequestMappingBuilder commandBuilder = new UpdateRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null,
            new RequestType[]{RequestType.MESSAGE},
            new MessageType[]{MessageType.COMMAND, MessageType.PHOTO}, true));

    ArrayList<UpdateRequestMapping> infos = new ArrayList<>();
    commandBuilder.forEach(infos::add);

    assertThat(infos).hasSize(1)
        .element(0)
        .hasFieldOrPropertyWithValue("pattern", "/**")
        .hasFieldOrPropertyWithValue("requestType", RequestType.MESSAGE)
        .hasFieldOrPropertyWithValue("messageTypes", Set.of(MessageType.COMMAND, MessageType.PHOTO));
  }
}