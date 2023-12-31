package io.github.drednote.telegram.handler.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import io.github.drednote.telegram.core.request.UpdateRequestMapping;
import io.github.drednote.telegram.support.UpdateRequestUtils;
import io.github.drednote.telegram.support.UpdateUtils;
import io.github.drednote.telegram.handler.scenario.ScenarioImpl.Node;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class ScenarioImplTest {

  final Object mockResponse = new Object();

  @Test
  void shouldMakeSteps() {
    ScenarioImpl scenario = getScenario();

    try {
      Result result = makeStep(scenario, "/start");

      // result asserts
      assertThat(result.isMade()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "root");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isFalse();

      result = scenario.makeStep(
          new DefaultUpdateRequest(
              UpdateRequestUtils.createMockRequest(UpdateUtils.createMessage("abc"))));

      // result asserts
      assertThat(result.isMade()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "child");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isFalse();
    } catch (ScenarioException e) {
      assertThat(e).isNull();
    }
  }

  @Test
  void shouldNotDoStepsIfNoTransition() {
    ScenarioImpl scenario = getScenario();

    try {
      Result result = makeStep(scenario, "/register");

      // result asserts
      assertThat(result.isMade()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root2");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "root2");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isTrue();

      result = scenario.makeStep(
          new DefaultUpdateRequest(
              UpdateRequestUtils.createMockRequest(UpdateUtils.createMessage("abc"))));

      // result asserts
      assertThat(result.isMade()).isFalse();
      assertThat(result.response()).isNull();

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root2");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "root2");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isTrue();
    } catch (ScenarioException e) {
      assertThat(e).isNull();
    }
  }

  @Test
  void shouldGoFromCancelToRoot() {
    ScenarioImpl scenario = getScenario();

    try {
      makeStep(scenario, "/start");

      Result result = scenario.makeStep(
          new DefaultUpdateRequest(
              UpdateRequestUtils.createMockRequest(UpdateUtils.createMessage("abc"))));

      // result asserts
      assertThat(result.isMade()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "child");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isFalse();

      result = scenario.makeStep(
          new DefaultUpdateRequest(
              UpdateRequestUtils.createMockRequest(UpdateUtils.createMessage("cancel"))));

      // result asserts
      assertThat(result.isMade()).isTrue();
      assertThat(result.response()).isEqualTo(mockResponse);

      // scenario asserts
      assertThat(scenario.getName()).isEqualTo("root");
      assertThat(scenario.step).isNotNull().hasFieldOrPropertyWithValue("name", "root");
      assertThat(scenario.getCurrentStep()).isInstanceOf(StepImpl.class);
      assertThat(scenario.isFinished()).isFalse();
    } catch (ScenarioException e) {
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
    ScenarioImpl scenario = new ScenarioImpl(id, generated, flat);

    assertThatThrownBy(() -> makeStep(scenario, "/start"))
        .isInstanceOf(ScenarioException.class)
        .cause().isInstanceOf(RuntimeException.class);
  }

  @Test
  void shouldThrowIllegalStateException() {
    long id = 1;
    Map<String, Node> flat = new HashMap<>();
    List<Node> generated = generateNodes(flat);
    ScenarioImpl scenario = new ScenarioImpl(id, generated, flat);

    try {
      makeStep(scenario, "/start");

      scenario.makeStep(
          new DefaultUpdateRequest(
              UpdateRequestUtils.createMockRequest(UpdateUtils.createMessage("abc"))));
    } catch (ScenarioException e) {
      assertThat(e).isNull();
    }

    flat.remove("root");
    assertThatThrownBy(() -> scenario.makeStep(
        new DefaultUpdateRequest(
            UpdateRequestUtils.createMockRequest(UpdateUtils.createMessage("cancel")))))
        .isInstanceOf(ScenarioException.class)
        .cause().isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldMadeOnlyOneStep() {
    ScenarioImpl scenario = getScenario();
    var first = CompletableFuture.supplyAsync(() -> makeStep(scenario, "/start"));
    var second = CompletableFuture.supplyAsync(() -> makeStep(scenario, "/register"));

    CompletableFuture.allOf(first, second).join();
    assertThat(scenario.stepsMade).hasSize(1);
  }

  @SneakyThrows
  private static Result makeStep(ScenarioImpl scenario, String step) {
    return scenario.makeStep(
        new DefaultUpdateRequest(
            UpdateRequestUtils.createMockRequest(UpdateUtils.createCommand(step))));
  }

  private ScenarioImpl getScenario() {
    long id = 1;
    Map<String, Node> flat = new HashMap<>();
    List<Node> generated = generateNodes(flat);
    ScenarioImpl scenario = new ScenarioImpl(id, generated, flat);

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
    Node root = new Node("root", new UpdateRequestMapping("/start", null, Collections.emptySet()),
        actionExecutor, null,
        null);
    flat.put(root.name, root);

    Node root2 = new Node("root2",
        new UpdateRequestMapping("/register", null, Collections.emptySet()), actionExecutor, null,
        null);
    flat.put(root2.name, root2);

    Node child = new Node("child", new UpdateRequestMapping("**", null, Collections.emptySet()),
        actionExecutor, null, root);
    flat.put(child.name, child);
    root.addChild(child);

    Node cancel = new Node("cancel",
        new UpdateRequestMapping("cancel", null, Collections.emptySet()), actionExecutor, "root",
        root);
    child.addChild(cancel);

    return List.of(root, root2);
  }
}