package io.github.drednote.telegram.updatehandler.response;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.Collection;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * This class provides a way to execute multiple {@code TelegramResponse} instances in a sequential
 * manner. It is typically used to compose multiple response actions and execute them together.
 * <p>
 * Note: It is recommended to avoid using this class directly. Instead, if you need to execute many
 * {@code TelegramResponse} instances, you can return a {@code Collection} of {@code
 * TelegramResponse} directly from your handler method.
 *
 * @author Ivan Galushko
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CompositeTelegramResponse implements TelegramResponse {

  private final List<TelegramResponse> invoked;

  /**
   * Constructs a {@code CompositeTelegramResponse} with a collection of invoked TelegramResponse
   * instances.
   *
   * @param invoked The collection of {@code TelegramResponse} instances to be invoked
   */
  public CompositeTelegramResponse(Collection<TelegramResponse> invoked) {
    Assert.required(invoked, "Collection of TelegramResponse");
    this.invoked = invoked.stream()
        .filter(handlerResponse -> handlerResponse != this)
        .sorted(AnnotationAwareOrderComparator.INSTANCE).toList();
  }

  /**
   * Processes each of the invoked TelegramResponse instances sequentially
   *
   * @param request The TelegramUpdateRequest containing the update information
   * @throws TelegramApiException if processing any of the invoked responses fails
   */
  @Override
  public void process(TelegramUpdateRequest request) throws TelegramApiException {
    for (TelegramResponse response : invoked) {
      response.process(request);
    }
  }
}
