package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.FieldProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.ThrowingSupplier;

@BetaApi
@Slf4j
public final class ScenarioImpl implements Scenario {

  private static final EmptyStep EMPTY_STEP = EmptyStep.INSTANCE;
  private static final ResultImpl EMPTY_RESULT = new ResultImpl(false, null);
  private static final String EXCEPTION_MESSAGE = "During performing an scenario action error occurred";

  final Long chatId;
  final List<Node> starts;
  final Map<String, Node> nodes;
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final FieldProvider<ScenarioMonitor> scenarioMonitor = FieldProvider.empty();

  // optional fields
  @Setter
  long lockMs = 0L;
  /**
   * null if no scenario initiated
   */
  @Nullable
  volatile String name;
  /**
   * null if no scenario initiated
   */
  @Nullable
  StepImpl step;
  /**
   * if a scenario finished
   */
  boolean finished;
  /**
   * history of isMade steps
   */
  List<Node> stepsMade;

  ScenarioImpl(Long chatId, List<Node> starts, Map<String, Node> nodes) {
    this.chatId = chatId;
    this.starts = starts;
    this.nodes = nodes;

    this.finished = false;
    this.stepsMade = new LinkedList<>();
  }

  @Override
  public Long getId() {
    return chatId;
  }

  @Nullable
  @Override
  public String getName() {
    return name;
  }

  @Override
  public Step getCurrentStep() {
    return step == null ? EMPTY_STEP : step;
  }

  @Override
  public boolean isFinished() {
    return this.finished;
  }

  @Override
  public Result makeStep(UpdateRequest request) throws ScenarioException {
    if (this.finished) {
      return EMPTY_RESULT;
    }
    try {
      return withLock(readWriteLock.writeLock(), () -> {
        Node nextNode = findNextNode(request);
        if (nextNode != null) {
          return doMakeStep(request, nextNode);
        } else {
          return EMPTY_RESULT;
        }
      });
    } catch (WrappingException e) {
      throw new ScenarioException(EXCEPTION_MESSAGE, e.getCause());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ScenarioException("Interrupt", e);
    } catch (Exception e) {
      throw new ScenarioException(EXCEPTION_MESSAGE, e);
    }
  }

  private <T> T withLock(Lock localLock, ThrowingSupplier<T> supplier)
      throws InterruptedException, TimeoutException {
    try {
      if (lockMs > 0L) {
        if (!localLock.tryLock(lockMs, TimeUnit.MILLISECONDS)) {
          throw new TimeoutException("Timeout while waiting for lock %s ms".formatted(lockMs));
        }
      } else {
        localLock.lock();
      }
      T result = supplier.get(WrappingException::new);
      localLock.unlock();
      return result;
    } catch (TimeoutException e) {
      throw e;
    } catch (Exception e) {
      localLock.unlock();
      throw e;
    }
  }

  @NonNull
  private ResultImpl doMakeStep(UpdateRequest request, Node nextNode) throws Exception {
    request.setScenario(this);
    Object result = nextNode.action.onAction(request);
    nextNode = tryToGoToRef(nextNode);
    mutate(nextNode);
    return new ResultImpl(true, result);
  }

  @NonNull
  private Node tryToGoToRef(Node nextNode) {
    String nextStepName = nextNode.nextStep;
    if (!StringUtils.isBlank(nextStepName)) {
      nextNode = nodes.get(nextStepName);
      checkNextStep(nextNode, nextStepName, "ref");
    }
    return nextNode;
  }

  private void mutate(Node nextNode) {
    boolean isRoot = nextNode.parent == null;
    if (isRoot) {
      this.name = nextNode.name;
    }
    this.finished = CollectionUtils.isEmpty(nextNode.children);

    StepImpl nextStep = new StepImpl(this, nextNode.action, nextNode.name);
    scenarioMonitor.ifExists(m -> m.madeStep(step, nextStep));

    this.step = nextStep;
    this.stepsMade.add(nextNode);
  }

  private Node findNextNode(UpdateRequest request) {
    if (step == null) {
      return findMatchingNode(request, starts);
    } else {
      String stepName = step.getName();
      Node node = nodes.get(stepName);
      checkNextStep(node, stepName, "saved");
      return findMatchingNode(request, node.children);
    }
  }

  @Nullable
  private Node findMatchingNode(UpdateRequest request, List<Node> nodes) {
    return nodes.stream()
        .filter(node -> node.pattern.matches(request))
        .min(Comparator.comparing(f -> f.pattern))
        .orElse(null);
  }

  void checkNextStep(Node nextNode, String nextStepName, String qualifier) {
    if (nextNode == null) {
      throw new IllegalStateException(
          "Cannot find %s step with the name '%s'. Maybe you delete it from configuration?"
              .formatted(qualifier, nextStepName));
    }
  }

  @Override
  public String toString() {
    return "Scenario %s".formatted(getName());
  }

  public void setScenarioMonitor(ScenarioMonitor scenarioMonitor) {
    this.scenarioMonitor.setField(scenarioMonitor);
  }

  @RequiredArgsConstructor
  static class Node {

    final String name;
    final UpdateRequestMapping pattern;
    final List<Node> children = new ArrayList<>();
    final ActionExecutor action;
    @Nullable
    final String nextStep;

    @Nullable
    final Node parent;

    public void addChild(Node node) {
      children.add(node);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Node node = (Node) o;
      return Objects.equals(name, node.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }
  }
}
