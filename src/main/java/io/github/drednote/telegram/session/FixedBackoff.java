package io.github.drednote.telegram.session;

import org.telegram.telegrambots.longpolling.interfaces.BackOff;

/**
 * Implementation of the {@link BackOff} interface that provides a fixed backoff interval.
 *
 * @author Ivan Galushko
 */
public class FixedBackoff implements BackOff {

  /**
   * Resets the backoff state.
   *
   * <p>This method does nothing in the case of the fixed backoff strategy, as the backoff
   * interval remains constant and does not change over time.
   */
  @Override
  public void reset() {
    // do nothing
  }

  /**
   * Returns the fixed backoff interval in milliseconds.
   *
   * <p>This method always returns the same fixed interval value, allowing for a consistent
   * delay between retries.
   *
   * @return The fixed backoff interval in milliseconds
   */
  @Override
  public long nextBackOffMillis() {
    return 500;
  }
}
