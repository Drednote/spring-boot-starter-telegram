package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.TelegramResponse;
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
public class CompositeTelegramResponse implements TelegramResponse {

  private final List<TelegramResponse> invoked;

  public CompositeTelegramResponse(Collection<TelegramResponse> invoked) {
    this.invoked = invoked == null ? null
        : invoked.stream()
            .filter(handlerResponse -> handlerResponse != this)
            .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
  }

  @Override
  public void process(TelegramUpdateRequest request) throws TelegramApiException {
    for (TelegramResponse response : invoked) {
      response.process(request);
    }
  }
}
