package io.github.drednote.telegram.session;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

/**
 * Utility class for managing Telegram bot update request contexts and associated beans.
 *
 * <p>This abstract class provides a set of static methods to manage and access
 * Telegram bot update request contexts along with their associated beans. It uses thread-local
 * storage to store the current update request context for each thread processing bot requests.
 *
 * <p>The class supports storing and retrieving the {@link TelegramUpdateRequest}. It also provides
 * methods for safely removing the update request and optionally destroying associated beans.
 *
 * <p>The main purpose of this class is to work with {@link TelegramRequestScope}, to have ability
 * to create beans with Telegram request scope. See {@link TelegramScope}.
 *
 * <p>You should not use this class directly. The only one purpose to use it if you creating
 * sub-threads within the main thread, you will need to manually bind the {@code
 * TelegramUpdateRequest} to the new thread by calling {@link #saveRequest(TelegramUpdateRequest)}.
 * <b>If you do this, do not forget to call {@link #removeRequest(boolean)} with parameter {@code
 * false} on every sub-thread when it is finished</b>
 *
 * @author Ivan Galushko
 * @see TelegramUpdateRequest
 * @see TelegramRequestScope
 * @see TelegramScope
 */
public abstract class UpdateRequestContext implements ApplicationContextAware {

  /**
   * key = thread id
   */
  private static final Map<Long, TelegramUpdateRequest> requests = new ConcurrentHashMap<>();
  /**
   * key = update id
   */
  private static final Map<Integer, List<String>> beanNames = new ConcurrentHashMap<>();
  private static ConfigurableBeanFactory factory;

  UpdateRequestContext() {
  }

  /**
   * Saves the provided Telegram update request in the current thread's context.
   *
   * <p><b>Do not call this method manually if you not creating sub-threads</b>
   *
   * @param request The Telegram update request to be saved
   */
  public static void saveRequest(TelegramUpdateRequest request) {
    Assert.notNull(request, "request");
    requests.put(Thread.currentThread().getId(), request);
  }

  /**
   * Retrieves the Telegram update request from the current thread's context.
   *
   * @return The Telegram update request associated with the current thread
   * @throws IllegalStateException if no update request is found for the current thread
   */
  @NonNull
  static TelegramUpdateRequest getRequest() {
    return Optional.of(Thread.currentThread().getId())
        .map(requests::get)
        .orElseThrow(() -> new IllegalStateException("No thread-bound bot request found: " +
            "Are you referring to request outside of an actual bot request, " +
            "or processing a request outside of the originally receiving thread? "
            + "Check the documentation of this class to solve the problem"));
  }

  /**
   * Removes the Telegram update request from the current thread's context.
   *
   * <p><b>Do not call this method manually if you not creating sub-threads. If you need to remove
   * request after sub-thread is finished, call with {@code false} parameter</b>
   *
   * @param destroyBeans Whether to destroy associated beans
   */
  public static void removeRequest(boolean destroyBeans) {
    if (destroyBeans) {
      TelegramUpdateRequest request = getRequest();
      synchronized (request) {
        List<String> names = beanNames.remove(request.getId());
        for (String name : names) {
          factory.destroyScopedBean(name);
        }
      }
    }
    requests.remove(Thread.currentThread().getId());
  }

  /**
   * Saves the name of a bean associated with the current update request.
   *
   * @param name The name of the bean to be saved
   */
  static void saveBeanName(@NonNull String name) {
    Assert.notEmpty(name, "name");

    TelegramUpdateRequest request = getRequest();
    synchronized (request) {
      List<String> names = beanNames.computeIfAbsent(request.getId(), key -> new ArrayList<>());
      names.add(name);
    }
  }

  @Override
  public void setApplicationContext(@NonNull ApplicationContext applicationContext)
      throws BeansException {
    this.factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
  }
}
