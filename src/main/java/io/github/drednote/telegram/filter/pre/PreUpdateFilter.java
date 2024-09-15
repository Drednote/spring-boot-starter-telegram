package io.github.drednote.telegram.filter.pre;

import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.filter.UpdateFilterMatcher;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * Represents a pre-update filter for Telegram update requests.
 *
 * <p>This interface extends the {@link UpdateFilterMatcher} interface and is responsible for
 * performing pre-filtering actions on an incoming Telegram update request before it has been
 * processed by update handlers.
 *
 * <p>The {@link #getPreOrder()} method specifies the order in which this pre-update filter
 * should be executed relative to other pre-update filters. A lower value indicates a higher
 * priority, and the default order is set to {@link Ordered#LOWEST_PRECEDENCE}.
 *
 * @author Ivan Galushko
 * @apiNote <b>Can be Telegram Scope for keeping context during update handler</b>
 * @see TelegramScope
 */
public interface PreUpdateFilter extends UpdateFilterMatcher {

  /**
   * Pre-filters the incoming Telegram update request before it has been processed by update
   * handlers.
   *
   * @param request The incoming Telegram update request to be pre-filtered
   */
  void preFilter(@NonNull UpdateRequest request);

  /**
   * Gets the pre-update filter's execution order.
   *
   * @return The order in which this pre-update filter should be executed
   */
  default int getPreOrder() {
    return FilterOrder.LOWEST_PRECEDENCE;
  }
}