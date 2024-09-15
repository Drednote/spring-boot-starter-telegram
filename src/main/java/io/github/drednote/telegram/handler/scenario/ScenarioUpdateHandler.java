package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.utils.lock.ReadWriteKeyLock;
import io.github.drednote.telegram.utils.lock.SynchronizedReadWriteKeyLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

@BetaApi
@Slf4j
@Order(FilterOrder.HIGHEST_PRECEDENCE)
public class ScenarioUpdateHandler implements UpdateHandler {

    private final long lockMs;
    private final ReadWriteKeyLock<String> lock = new SynchronizedReadWriteKeyLock<>();

    public ScenarioUpdateHandler(long lockMs) {
        this.lockMs = lockMs;
    }

    @Override
    public void onUpdate(UpdateRequest request) throws Exception {
        Scenario<?> scenario = request.getScenario();
        if (scenario != null) {
            ScenarioIdResolver idResolver = scenario.getAccessor().getIdResolver();
            final String id = scenario.getId();
            try {
                lock.writeLock().lock(id, lockMs);
                boolean sendEvent = scenario.sendEvent(request);
                if (sendEvent) {
                    idResolver.saveNewId(request, id);
                }
            } finally {
                lock.writeLock().unlock(id);
            }
        }
    }
}
