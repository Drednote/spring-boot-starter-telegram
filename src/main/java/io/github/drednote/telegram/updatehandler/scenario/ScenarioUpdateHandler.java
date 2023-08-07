package io.github.drednote.telegram.updatehandler.scenario;

import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;
import io.github.drednote.telegram.updatehandler.UpdateHandler;
import io.github.drednote.telegram.utils.FieldProvider;
import io.github.drednote.telegram.core.ResponseSetter;
import io.github.drednote.telegram.utils.lock.ReadWriteKeyLock;
import io.github.drednote.telegram.utils.lock.SynchronizedReadWriteKeyLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class ScenarioUpdateHandler implements UpdateHandler {

  private final FieldProvider<ScenarioPersister> persister;
  private final ScenarioFactory scenarioFactory;
  private final ReadWriteKeyLock<Long> lock = new SynchronizedReadWriteKeyLock<>();

  public ScenarioUpdateHandler(ScenarioMachineContainer container) {
    this.persister = FieldProvider.create(container.getScenarioPersister());
    this.scenarioFactory = container;
  }

  @Override
  public void onUpdate(ExtendedTelegramUpdateRequest request) throws Exception {
    Long chatId = request.getChatId();
    try {
      lock.writeLock().lock(chatId);
      Scenario scenario = scenarioFactory.createInitial(chatId);
      persister.ifExists(p -> p.restore(scenario));
      Result result = scenario.makeStep(request);
      if (result.isMade()) {
        ResponseSetter.setResponse(request, result.response());
        persister.ifExistsWithException(p -> p.persist(scenario));
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ScenarioException("Interrupt", e);
    } finally {
      lock.writeLock().unlock(chatId);
    }
  }
}
