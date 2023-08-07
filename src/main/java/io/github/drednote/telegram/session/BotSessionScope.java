package io.github.drednote.telegram.session;

import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class BotSessionScope implements Scope {

  public static final String BOT_SCOPE_NAME = "bot-session";
  private final Map<BeanKey, Object> scopedObjects = new ConcurrentHashMap<>();
  private final Map<BeanKey, Runnable> destructionCallbacks = new ConcurrentHashMap<>();

  @NonNull
  @Override
  public Object get(@NonNull String name, @NonNull ObjectFactory<?> objectFactory) {
    BeanKey key = createBeanKey(name);
    return scopedObjects.computeIfAbsent(key, it -> {
      BotSessionContext.saveBeanName(name);
      return objectFactory.getObject();
    });
  }

  @Nullable
  @Override
  public Object remove(@NonNull String name) {
    BeanKey key = createBeanKey(name);
    Optional.ofNullable(destructionCallbacks.remove(key)).ifPresent(Runnable::run);
    return scopedObjects.remove(key);
  }

  @Override
  public void registerDestructionCallback(@NonNull String name, @NonNull Runnable callback) {
    BeanKey beanKey = createBeanKey(name);
    destructionCallbacks.put(beanKey, callback);
  }

  @NonNull
  private BeanKey createBeanKey(@NonNull String name) {
    ExtendedTelegramUpdateRequest botRequest = BotSessionContext.getRequest();
    return new BeanKey(botRequest.getId(), name);
  }

  @Nullable
  @Override
  public Object resolveContextualObject(@NonNull String key) {
    if (BOT_SCOPE_NAME.equals(key)) {
      return BotSessionContext.getRequest();
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
