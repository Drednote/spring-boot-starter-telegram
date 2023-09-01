package io.github.drednote.telegram.core;

import io.github.drednote.telegram.core.request.UpdateRequest;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;

/**
 * Custom Spring scope implementation for managing Telegram bot request-scoped beans.
 *
 * <p>This class implements the Spring {@link Scope} interface and provides custom behavior
 * for managing beans within a Telegram bot request scope. It allows for the creation, retrieval,
 * and removal of request-scoped beans while handling bean destruction callbacks.
 *
 * <p>The class stores request-scoped objects and their associated destruction callbacks
 * in concurrent hash maps for thread-safe access and management.
 *
 * <p>The Telegram request scope is designed to be used within a Spring application context
 * to manage beans associated with a {@link UpdateRequest}. Beans within this scope are
 * created and managed for the duration of a {@code UpdateRequest}
 *
 * @author Ivan Galushko
 * @see UpdateRequestContext
 * @see UpdateRequest
 */
public class TelegramRequestScope implements Scope {

  public static final String BOT_SCOPE_NAME = "bot-request";
  private final Map<BeanKey, Object> scopedObjects = new ConcurrentHashMap<>();
  private final Map<BeanKey, Runnable> destructionCallbacks = new ConcurrentHashMap<>();

  /**
   * @see Scope#get(String, ObjectFactory)
   */
  @Override
  public Object get(String name, ObjectFactory<?> objectFactory) {
    BeanKey key = createBeanKey(name);
    return scopedObjects.computeIfAbsent(key, it -> {
      UpdateRequestContext.saveBeanName(name);
      return objectFactory.getObject();
    });
  }

  /**
   * @see Scope#remove(String)
   */
  @Nullable
  @Override
  public Object remove(String name) {
    BeanKey key = createBeanKey(name);
    Optional.ofNullable(destructionCallbacks.remove(key)).ifPresent(Runnable::run);
    return scopedObjects.remove(key);
  }

  /**
   * @see Scope#registerDestructionCallback(String, Runnable)
   */
  @Override
  public void registerDestructionCallback(String name, Runnable callback) {
    BeanKey beanKey = createBeanKey(name);
    destructionCallbacks.put(beanKey, callback);
  }

  /**
   * Creates a bean key for identifying session-scoped beans.
   *
   * @param name The name of the bean.
   * @return A unique bean key based on the update ID and bean name.
   */
  private BeanKey createBeanKey(String name) {
    UpdateRequest botRequest = UpdateRequestContext.getRequest();
    return new BeanKey(botRequest.getId(), name);
  }

  /**
   * @see Scope#resolveContextualObject(String)
   */
  @Nullable
  @Override
  public Object resolveContextualObject(String key) {
    if (BOT_SCOPE_NAME.equals(key)) {
      return UpdateRequestContext.getRequest();
    }
    return null;
  }

  @Nullable
  @Override
  public String getConversationId() {
    return "tenant";
  }

  private record BeanKey(Integer updateId, String name) {}
}
