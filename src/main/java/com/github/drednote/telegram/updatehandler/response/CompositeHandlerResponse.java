package com.github.drednote.telegram.updatehandler.response;

import com.github.drednote.telegram.core.BotRequest;
import com.github.drednote.telegram.updatehandler.HandlerResponse;
import java.util.Collection;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class CompositeHandlerResponse extends AbstractHandlerResponse {

  private final List<HandlerResponse> invoked;

  public CompositeHandlerResponse(Collection<HandlerResponse> invoked) {
    super(null, null);
    this.invoked = invoked == null ? null
        : invoked.stream()
            .filter(handlerResponse -> handlerResponse != this)
            .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
  }

  @Override
  public void process(BotRequest request) throws TelegramApiException {
    for (HandlerResponse response : invoked) {
      response.process(request);
    }
  }
}
