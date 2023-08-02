package com.github.drednote.telegram.session;

import com.github.drednote.telegram.core.request.ExtendedBotRequest;
import com.github.drednote.telegram.utils.Assert;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;

@UtilityClass
public class BotSessionContext {

  private static final Map<Integer, ExtendedBotRequest> byUpdateId = new ConcurrentHashMap<>();
  private static final Map<Long, Integer> pairs = new ConcurrentHashMap<>();

  public void saveRequest(ExtendedBotRequest request) {
    Assert.notNull(request, "request");
    Integer id = request.getId();
    byUpdateId.put(id, request);
    pairs.put(Thread.currentThread().getId(), id);
  }

  @NonNull
  public ExtendedBotRequest getRequest() {
    return Optional.of(Thread.currentThread().getId())
        .map(pairs::get)
        .map(byUpdateId::get)
        .orElseThrow(() -> new IllegalStateException("No thread-bound bot request found: " +
            "Are you referring to request outside of an actual bot request, " +
            "or processing a request outside of the originally receiving thread?"));
  }

  public void removeRequest() {
    Integer updateId = pairs.get(Thread.currentThread().getId());
    byUpdateId.remove(updateId);
  }
}
