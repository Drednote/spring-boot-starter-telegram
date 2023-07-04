package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CompositeHandlerResponse implements HandlerResponse {

  private final List<HandlerResponse> invoked;

  public CompositeHandlerResponse(Collection<HandlerResponse> invoked) {
    this.invoked = invoked == null ? null
        : invoked.stream()
            .filter(handlerResponse -> handlerResponse != this)
            .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
  }

  @Override
  public void process(UpdateRequest request) throws TelegramApiException, IOException {
    for (HandlerResponse response : invoked) {
      response.process(request);
    }
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
