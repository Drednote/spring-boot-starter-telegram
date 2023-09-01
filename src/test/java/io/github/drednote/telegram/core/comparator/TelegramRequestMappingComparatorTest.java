package io.github.drednote.telegram.core.comparator;

import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.handler.controller.TelegramRequestMappingBuilder;
import io.github.drednote.telegram.handler.controller.TelegramRequestMappingBuilder.TelegramRequestMappingMetaData;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

class TelegramRequestMappingComparatorTest {

  final RequestMappingInfoComparator comparator = new RequestMappingInfoComparator(
      new AntPathMatcher());

  @Test
  void name() {
    TelegramRequestMappingBuilder builder = new TelegramRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null,
            new RequestType[]{RequestType.POLL, RequestType.POLL_ANSWER}, null, false));
    List<TelegramRequestMapping> objects = new ArrayList<>();
    builder.forEach(objects::add);

//    RequestMappingInfo first = new RequestMappingInfo(null, RequestType.POLL,
//        Collections.emptySet());
//    RequestMappingInfo second = new RequestMappingInfo(null, RequestType.POLL_ANSWER,
//        Collections.emptySet());
    objects.sort(comparator);
    TelegramRequestMapping telegramRequestMapping = objects.get(0);
    System.out.println("requestMappingInfo = " + telegramRequestMapping);
  }
}