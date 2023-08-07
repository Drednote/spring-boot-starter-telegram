package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.HandlerResponse;
import java.util.Collection;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * You should not use this class directly. If you need to execute many {@code HandlerResponse}, just
 * return {@code Collection} of {@code HandlerResponse}
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CompositeHandlerResponse implements HandlerResponse {

  private final List<HandlerResponse> invoked;

  public CompositeHandlerResponse(Collection<HandlerResponse> invoked) {
    this.invoked = invoked == null ? null
        : invoked.stream()
            .filter(handlerResponse -> handlerResponse != this)
            .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
  }

  @Override
  public void process(TelegramUpdateRequest request) throws TelegramApiException {
    for (HandlerResponse response : invoked) {
      response.process(request);
    }
  }
}
