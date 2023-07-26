package com.github.drednote.telegram.updatehandler.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.drednote.telegram.UpdateUtils;
import com.github.drednote.telegram.core.ActionExecutor;
import com.github.drednote.telegram.core.RequestMappingInfo;
import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.scenario.ScenarioImpl.Node;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ScenarioImplTest {

  final Object mockResponse = new Object();

  @Test
  void shouldMakeSteps() {
    ScenarioImpl scenario = getScenario();

    try {
      Result result = scenario.makeStep(
          new UpdateRequest(UpdateUtils.createCommand("/start"), null, null));

      // result asserts
      assertThat(result.made()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "root");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isFalse();

      result = scenario.makeStep(
          new UpdateRequest(UpdateUtils.createMessage("abc"), null, null));

      // result asserts
      assertThat(result.made()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "child");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isFalse();
    } catch (ScenarioTransitionException e) {
      assertThat(e).isNull();
    }
  }

  @Test
  void shouldNotDoStepsIfNoTransition() {
    ScenarioImpl scenario = getScenario();

    try {
      Result result = scenario.makeStep(
          new UpdateRequest(UpdateUtils.createCommand("/register"), null, null));

      // result asserts
      assertThat(result.made()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root2");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "root2");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isTrue();

      result = scenario.makeStep(
          new UpdateRequest(UpdateUtils.createMessage("abc"), null, null));

      // result asserts
      assertThat(result.made()).isFalse();
      assertThat(result.response()).isNull();

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root2");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "root2");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isTrue();
    } catch (ScenarioTransitionException e) {
      assertThat(e).isNull();
    }
  }

  @Test
  void shouldGoFromCancelToRoot() {
    ScenarioImpl scenario = getScenario();

    try {
      scenario.makeStep(
          new UpdateRequest(UpdateUtils.createCommand("/start"), null, null));

      Result result = scenario.makeStep(
          new UpdateRequest(UpdateUtils.createMessage("abc"), null, null));

      // result asserts
      assertThat(result.made()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "child");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isFalse();

      result = scenario.makeStep(
          new UpdateRequest(UpdateUtils.createMessage("cancel"), null, null));

      // result asserts
      assertThat(result.made()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "root");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isFalse();
    } catch (ScenarioTransitionException e) {
      assertThat(e).isNull();
    }
  }

  @Test
  void shouldThrowRuntimeException() {
    long id = 1;
    Map<String, Node> flat = new HashMap<>();
    List<Node> generated = generateNodes(flat, it -> {
      throw new RuntimeException();
    });
    ScenarioImpl scenario = new ScenarioImpl(id, generated, flat, 1000L);

    assertThatThrownBy(() -> scenario.makeStep(
        new UpdateRequest(UpdateUtils.createCommand("/start"), null, null)))
        .isInstanceOf(ScenarioTransitionException.class)
        .cause().isInstanceOf(RuntimeException.class);
  }

  @Test
  void shouldThrowIllegalStateException() {
    long id = 1;
    Map<String, Node> flat = new HashMap<>();
    List<Node> generated = generateNodes(flat);
    ScenarioImpl scenario = new ScenarioImpl(id, generated, flat, 1000L);

    try {
      scenario.makeStep(
          new UpdateRequest(UpdateUtils.createCommand("/start"), null, null));

      scenario.makeStep(
          new UpdateRequest(UpdateUtils.createMessage("abc"), null, null));
    } catch (ScenarioTransitionException e) {
      assertThat(e).isNull();
    }

    flat.remove("root");
    assertThatThrownBy(() -> scenario.makeStep(
        new UpdateRequest(UpdateUtils.createMessage("cancel"), null, null)))
        .isInstanceOf(ScenarioTransitionException.class)
        .cause().isInstanceOf(IllegalStateException.class);
  }

  private ScenarioImpl getScenario() {
    long id = 1;
    Map<String, Node> flat = new HashMap<>();
    List<Node> generated = generateNodes(flat);
    ScenarioImpl scenario = new ScenarioImpl(id, generated, flat, 1000L);

    // initial asserts
    assertThat(scenario.getId()).isEqualTo(id);
    assertThat(scenario.getName()).isNull();
    assertThat(scenario.step).isNull();
    assertThat(scenario.getCurrentStep()).isInstanceOf(EmptyStep.class);
    assertThat(scenario.isFinished()).isFalse();
    return scenario;
  }

  private List<Node> generateNodes(Map<String, Node> flat) {
    ActionExecutor actionExecutor = it -> mockResponse;

    return generateNodes(flat, actionExecutor);
  }

  private List<Node> generateNodes(Map<String, Node> flat, ActionExecutor actionExecutor) {
    Node root = new Node("root", new RequestMappingInfo("/start", null), actionExecutor, null,
        null);
    flat.put(root.name, root);

    Node root2 = new Node("root2", new RequestMappingInfo("/register", null), actionExecutor, null,
        null);
    flat.put(root2.name, root2);

    Node child = new Node("child", new RequestMappingInfo("**", null), actionExecutor, null, root);
    flat.put(child.name, child);
    root.addChild(child);

    Node cancel = new Node("cancel", new RequestMappingInfo("cancel", null), actionExecutor, "root",
        root);
    child.addChild(cancel);

    return List.of(root, root2);
  }
}