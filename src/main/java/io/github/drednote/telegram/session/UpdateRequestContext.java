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

  public static void saveRequest(TelegramUpdateRequest request) {
    Assert.notNull(request, "request");
    requests.put(Thread.currentThread().getId(), request);
  }

  @NonNull
  public static TelegramUpdateRequest getRequest() {
    return Optional.of(Thread.currentThread().getId())
        .map(requests::get)
        .orElseThrow(() -> new IllegalStateException("No thread-bound bot request found: " +
            "Are you referring to request outside of an actual bot request, " +
            "or processing a request outside of the originally receiving thread?"));
  }

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
