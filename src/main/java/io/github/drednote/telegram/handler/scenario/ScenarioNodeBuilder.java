package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.annotation.BetaApi;
import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioDefinition;
import io.github.drednote.telegram.handler.scenario.configurer.StepDefinition;
import io.github.drednote.telegram.handler.scenario.ScenarioImpl.Node;
import io.github.drednote.telegram.utils.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

@BetaApi
final class ScenarioNodeBuilder {

  @Getter
  private final Map<String, Node> flatNodes;
  @Getter
  private final List<Node> scenarios;

  ScenarioNodeBuilder(LinkedList<ScenarioDefinition> definitions) {
    this.scenarios = new ArrayList<>();
    this.flatNodes = new HashMap<>();
    for (ScenarioDefinition scenario : definitions) {
      String startCommand = scenario.startCommand();
      LinkedList<StepDefinition> steps = scenario.steps();
      ActionExecutor action = scenario.action();

      Assert.notEmpty(startCommand, "Scenario start command");
      Assert.notNull(action, "Scenario action");
      Assert.notEmpty(steps, "Scenario steps");

      String id = StringUtils.isBlank(scenario.name())
          ? truncateSlash(startCommand)
          : scenario.name();
      Node scenarioNode = new Node(
          id, new TelegramRequestMapping(startCommand, RequestType.MESSAGE,
          Set.of(MessageType.COMMAND)), action, null, null);
      addToFlat(scenarioNode);
      addChildren(steps, scenarioNode);
      this.scenarios.add(scenarioNode);
    }
  }

  @NonNull
  private String truncateSlash(String startCommand) {
    return startCommand.startsWith("/") ? startCommand.substring(1) : startCommand;
  }

  private void addChildren(LinkedList<StepDefinition> steps, Node parentNode) {
    int i = 0;
    for (StepDefinition step : steps) {
      List<TelegramRequestMapping> patterns = createEmptyIfNeed(step.pattern());
      for (TelegramRequestMapping mappingInfo : patterns) {
        ActionExecutor action = step.action();
        Assert.notNull(action, "Scenario action");

        String name = StringUtils.isBlank(step.name())
            ? generateStepName(parentNode.name, i++)
            : step.name();
        Node node = new Node(name, mappingInfo, action, step.refToStep(), parentNode);
        addToFlat(node);
        parentNode.addChild(node);
        if (!CollectionUtils.isEmpty(step.steps())) {
          addChildren(step.steps(), node);
        }
      }
    }
  }

  @NonNull
  private List<TelegramRequestMapping> createEmptyIfNeed(
      List<TelegramRequestMapping> patterns) {
    if (patterns == null) {
      patterns = new ArrayList<>();
    }
    if (patterns.isEmpty()) {
      patterns.add(new TelegramRequestMapping("**", null, Collections.emptySet()));
    }
    return patterns;
  }

  @NonNull
  private String generateStepName(String parentName, int i) {
    return parentName + "_s" + i;
  }

  private void addToFlat(Node node) {
    if (flatNodes.containsKey(node.name)) {
      throw new IllegalArgumentException(
          "Scenario step with name '%s' already exists. Name must be unique".formatted(node));
    }
    flatNodes.put(node.name, node);
  }
}