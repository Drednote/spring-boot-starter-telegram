package io.github.drednote.telegram.core.comparator;

import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.handler.controller.UpdateRequestMappingBuilder;
import io.github.drednote.telegram.handler.controller.UpdateRequestMappingBuilder.TelegramRequestMappingMetaData;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

class UpdateRequestMappingComparatorTest {

  final RequestMappingInfoComparator comparator = new RequestMappingInfoComparator(
      new AntPathMatcher());

  @Test
  void name() {
    UpdateRequestMappingBuilder builder = new UpdateRequestMappingBuilder(
        new TelegramRequestMappingMetaData(null,
            new RequestType[]{RequestType.POLL, RequestType.POLL_ANSWER}, null, false));
    List<UpdateRequestMapping> objects = new ArrayList<>();
    builder.forEach(objects::add);

//    RequestMappingInfo first = new RequestMappingInfo(null, RequestType.POLL,
//        Collections.emptySet());
//    RequestMappingInfo second = new RequestMappingInfo(null, RequestType.POLL_ANSWER,
//        Collections.emptySet());
    objects.sort(comparator);
    UpdateRequestMapping updateRequestMapping = objects.get(0);
    System.out.println("requestMappingInfo = " + updateRequestMapping);
  }
}