package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.UpdateHandler;
import io.github.drednote.telegram.utils.lock.ReadWriteKeyLock;
import io.github.drednote.telegram.utils.lock.SynchronizedReadWriteKeyLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@BetaApi
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
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
            final String id = scenario.getId();
            try {
                lock.writeLock().lock(id, lockMs);
                scenario.sendEvent(request);
            } finally {
                lock.writeLock().unlock(id);
            }
        }
    }
}
