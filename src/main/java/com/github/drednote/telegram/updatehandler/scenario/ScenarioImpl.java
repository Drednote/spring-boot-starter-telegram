package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.ActionExecutor;
import com.github.drednote.telegram.core.RequestMappingInfo;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

@Slf4j
public final class ScenarioImpl implements Scenario {

  private static final EmptyStep EMPTY_STEP = EmptyStep.INSTANCE;
  private static final ResultImpl EMPTY_RESULT = new ResultImpl(false, null);
  private final Long chatId;
  private final List<Node> starts;
  private final Map<String, Node> nodes;
  private final long lockMs;
  private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

  /**
   * null if no scenario initiated
   */
  @Nullable
  String name;
  /**
   * null if no scenario initiated
   */
  @Nullable
  Step step;

  /**
   * if a scenario finished
   */
  boolean finished;

  ScenarioImpl(Long chatId, List<Node> starts, Map<String, Node> nodes, long lockMs) {
    this.chatId = chatId;
    this.starts = starts;
    this.nodes = nodes;

    this.finished = false;
    this.lockMs = lockMs;
  }

  @Override
  public Long getId() {
    return chatId;
  }

  @Nullable
  @Override
  public String getName() {
    readWriteLock.readLock().lock();
    String localName = name;
    readWriteLock.readLock().unlock();
    return localName;
  }

  @Override
  public Step getCurrentStep() {
    readWriteLock.readLock().lock();
    Step localStep = step == null ? EMPTY_STEP : step;
    readWriteLock.readLock().unlock();
    return localStep;
  }

  @Override
  public boolean isFinished() {
    return this.finished;
  }

  @Override
  public Result makeStep(UpdateRequest request) throws ScenarioTransitionException {
    if (this.finished) {
      return EMPTY_RESULT;
    }
    try {
      if (readWriteLock.writeLock().tryLock(lockMs, TimeUnit.MILLISECONDS)) {
        Node nextNode = findNextNode(request);
        if (nextNode != null) {
          return doMakeStep(request, nextNode);
        }
      } else {
        throw new ScenarioTransitionException("Timeout while waiting for lock",
            new TimeoutException());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ScenarioTransitionException("Interrupt", e);
    } catch (Exception e) {
      throw new ScenarioTransitionException(
          "During performing an scenario action error occurred", e);
    } finally {
      readWriteLock.writeLock().unlock();
    }
    return EMPTY_RESULT;
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
    this.step = new StepImpl(this, nextNode.action, nextNode.name);
  }

  private Node findNextNode(UpdateRequest request) {
    if (step == null) {
      return findMatchingNode(request, starts);
    } else {
      Assert.notNull(step, "step");
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

  private void checkNextStep(Node nextNode, String nextStepName, String qualifier) {
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

  @RequiredArgsConstructor
  static class Node {

    final String name;
    final RequestMappingInfo pattern;
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
