package io.github.drednote.telegram.handler.advancedscenario.core.data;

import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioEntity;
import io.github.drednote.telegram.handler.advancedscenario.core.data.interfaces.IAdvancedScenarioStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class InMemoryAdvancedScenarioStorage implements IAdvancedScenarioStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryAdvancedScenarioStorage.class);

    private final Map<String, IAdvancedScenarioEntity> data = new ConcurrentHashMap<>();
    private static final int MAX_SIZE = 1000;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public void save(IAdvancedScenarioEntity entity) {
        lock.lock();
        try {
            String key = entity.getKey();
            if (data.containsKey(key)) {
                log.info("Updating existing record for key: {}", key);
            } else {
                if (data.size() >= MAX_SIZE) {
                    removeOldestEntry();
                }
            }
            data.put(key, entity);
            log.info("Record saved: {}", entity);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<IAdvancedScenarioEntity> findById(String key) {
        return Optional.ofNullable(data.get(key));
    }

    @Override
    public void deleteById(String key) {
        lock.lock();
        try {
            if (data.containsKey(key)) {
                data.remove(key);
                log.info("Record with key {} deleted", key);
            } else {
                log.warn("Record with key {} not found", key);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        return data.size();
    }

    private void removeOldestEntry() {
        lock.lock();
        try {
            Instant oldestTimestamp = Instant.MAX;
            String oldestKey = null;

            for (Map.Entry<String, IAdvancedScenarioEntity> entry : data.entrySet()) {
                Instant currentTimestamp = entry.getValue().getChangeDate();
                if (currentTimestamp.isBefore(oldestTimestamp)) {
                    oldestTimestamp = currentTimestamp;
                    oldestKey = entry.getKey();
                }
            }

            if (oldestKey != null) {
                data.remove(oldestKey);
                log.info("Old record with key {} removed", oldestKey);
            }
        } finally {
            lock.unlock();
        }
    }
}
