package io.github.drednote.telegram.updatehandler.scenario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.updatehandler.scenario.ScenarioImpl.Node;
import io.github.drednote.telegram.updatehandler.scenario.configurer.ScenarioDefinition;
import io.github.drednote.telegram.updatehandler.scenario.configurer.StepDefinition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

@Disabled
class ScenarioNodeBuilderTest {

  @Test
  void shouldReturnEmptyIfDefinitionsAreEmpty() {
    ScenarioNodeBuilder actualScenarioNodeBuilder = new ScenarioNodeBuilder(new LinkedList<>());
    assertTrue(actualScenarioNodeBuilder.getFlatNodes().isEmpty());
    assertTrue(actualScenarioNodeBuilder.getScenarios().isEmpty());
  }

  @Test
  void shouldThrowIllegalArgumentExceptionIfStepsEmpty() {
    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
    ActionExecutor action = mock(ActionExecutor.class);
    definitions.add(new ScenarioDefinition("Start Command", "Name", action, new LinkedList<>()));
    assertThrows(IllegalArgumentException.class, () -> new ScenarioNodeBuilder(definitions));
  }

  @Test
  void shouldThrowExceptionIfActionNull() {
    LinkedList<StepDefinition> steps = new LinkedList<>();
    ArrayList<TelegramRequestMapping> pattern = new ArrayList<>();
    steps
        .add(new StepDefinition("Scenario start command", pattern, null, new LinkedList<>(),
            "Scenario start command"));
    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name",
        mock(ActionExecutor.class),
        steps);

    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
    definitions.add(scenarioDefinition);
    assertThrows(IllegalArgumentException.class, () -> new ScenarioNodeBuilder(definitions));
  }

  /**
   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
   */
  @Test
  void testConstructor5() {
    LinkedList<StepDefinition> steps = new LinkedList<>();
    ArrayList<TelegramRequestMapping> pattern = new ArrayList<>();
    ActionExecutor action = mock(ActionExecutor.class);
    steps.add(
        new StepDefinition("Scenario start command", pattern, action, new LinkedList<>(),
            "Scenario start command"));
    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name",
        mock(ActionExecutor.class),
        steps);

    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
    definitions.add(scenarioDefinition);
    ScenarioNodeBuilder actualScenarioNodeBuilder = new ScenarioNodeBuilder(definitions);
    Map<String, Node> flatNodes = actualScenarioNodeBuilder.getFlatNodes();
    assertEquals(2, flatNodes.size());
    List<Node> scenarios = actualScenarioNodeBuilder.getScenarios();
    assertEquals(1, scenarios.size());
    Node getResult = scenarios.get(0);
    assertNull(getResult.parent);
    Node getResult2 = flatNodes.get("Scenario start command");
    assertTrue(getResult2.children.isEmpty());
    assertEquals(1, getResult.children.size());
    assertEquals("Name", getResult.name);
    assertEquals("Scenario start command", getResult2.name);
    assertSame(getResult, getResult2.parent);
    TelegramRequestMapping telegramRequestMapping = getResult.pattern;
    assertEquals("Start Command", telegramRequestMapping.getPattern());
    assertNull(telegramRequestMapping.getMessageTypes());
    TelegramRequestMapping telegramRequestMapping2 = getResult2.pattern;
    assertTrue(telegramRequestMapping2.getPathMatcher() instanceof AntPathMatcher);
    assertEquals("**", telegramRequestMapping2.getPattern());
    assertNull(telegramRequestMapping2.getMessageTypes());
    assertTrue(telegramRequestMapping.getPathMatcher() instanceof AntPathMatcher);
    assertEquals(1, definitions.get(0).steps().get(0).pattern().size());
  }

  /**
   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
   */
  @Test
  void testConstructor6() {
    LinkedList<StepDefinition> steps = new LinkedList<>();
    ArrayList<TelegramRequestMapping> pattern = new ArrayList<>();
    ActionExecutor action = mock(ActionExecutor.class);
    steps.add(
        new StepDefinition("Scenario start command", pattern, action, new LinkedList<>(),
            "Scenario start command"));
    ArrayList<TelegramRequestMapping> pattern2 = new ArrayList<>();
    ActionExecutor action2 = mock(ActionExecutor.class);
    steps.add(
        new StepDefinition("Scenario start command", pattern2, action2, new LinkedList<>(),
            "Scenario start command"));
    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name",
        mock(ActionExecutor.class),
        steps);

    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
    definitions.add(scenarioDefinition);
    assertThrows(IllegalArgumentException.class, () -> new ScenarioNodeBuilder(definitions));
  }

  /**
   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
   */
  @Test
  void testConstructor7() {
    LinkedList<StepDefinition> steps = new LinkedList<>();
    ArrayList<TelegramRequestMapping> pattern = new ArrayList<>();
    ActionExecutor action = mock(ActionExecutor.class);
    steps.add(
        new StepDefinition(null, pattern, action, new LinkedList<>(), "Scenario start command"));
    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name",
        mock(ActionExecutor.class),
        steps);

    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
    definitions.add(scenarioDefinition);
    ScenarioNodeBuilder actualScenarioNodeBuilder = new ScenarioNodeBuilder(definitions);
    Map<String, Node> flatNodes = actualScenarioNodeBuilder.getFlatNodes();
    assertEquals(2, flatNodes.size());
    assertEquals(1, actualScenarioNodeBuilder.getScenarios().size());
    Node getResult = flatNodes.get("Name");
    assertNull(getResult.parent);
    Node getResult2 = flatNodes.get("Name_s0");
    assertSame(getResult, getResult2.parent);
    assertEquals(1, getResult.children.size());
    assertEquals("Name_s0", getResult2.name);
    assertEquals("Name", getResult.name);
    assertTrue(getResult2.children.isEmpty());
    TelegramRequestMapping telegramRequestMapping = getResult2.pattern;
    assertTrue(telegramRequestMapping.getPathMatcher() instanceof AntPathMatcher);
    assertNull(telegramRequestMapping.getMessageTypes());
    TelegramRequestMapping telegramRequestMapping2 = getResult.pattern;
    assertTrue(telegramRequestMapping2.getPathMatcher() instanceof AntPathMatcher);
    assertEquals("Start Command", telegramRequestMapping2.getPattern());
    assertNull(telegramRequestMapping2.getMessageTypes());
    assertEquals("**", telegramRequestMapping.getPattern());
    assertEquals(1, definitions.get(0).steps().get(0).pattern().size());
  }

  /**
   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
   */
  @Test
  void testConstructor9() {
    LinkedList<StepDefinition> steps = new LinkedList<>();
    ArrayList<TelegramRequestMapping> pattern = new ArrayList<>();
    ActionExecutor action = mock(ActionExecutor.class);
    steps.add(
        new StepDefinition("Scenario start command", pattern, action, new LinkedList<>(),
            "Scenario start command"));
    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", null,
        mock(ActionExecutor.class),
        steps);

    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
    definitions.add(scenarioDefinition);
    ScenarioNodeBuilder actualScenarioNodeBuilder = new ScenarioNodeBuilder(definitions);
    Map<String, Node> flatNodes = actualScenarioNodeBuilder.getFlatNodes();
    assertEquals(2, flatNodes.size());
    List<Node> scenarios = actualScenarioNodeBuilder.getScenarios();
    assertEquals(1, scenarios.size());
    Node getResult = scenarios.get(0);
    assertNull(getResult.parent);
    Node getResult2 = flatNodes.get("Scenario start command");
    assertTrue(getResult2.children.isEmpty());
    assertEquals(1, getResult.children.size());
    assertEquals("Start Command", getResult.name);
    assertEquals("Scenario start command", getResult2.name);
    assertSame(getResult, getResult2.parent);
    TelegramRequestMapping telegramRequestMapping = getResult.pattern;
    assertEquals("Start Command", telegramRequestMapping.getPattern());
    assertNull(telegramRequestMapping.getMessageTypes());
    TelegramRequestMapping telegramRequestMapping2 = getResult2.pattern;
    assertTrue(telegramRequestMapping2.getPathMatcher() instanceof AntPathMatcher);
    assertEquals("**", telegramRequestMapping2.getPattern());
    assertNull(telegramRequestMapping2.getMessageTypes());
    assertTrue(telegramRequestMapping.getPathMatcher() instanceof AntPathMatcher);
    assertEquals(1, definitions.get(0).steps().get(0).pattern().size());
  }

  /**
   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
   */
  @Test
  @Disabled("TODO: Complete this test")
  void testConstructor10() {
    // TODO: Complete this test.
    //   Reason: R013 No inputs found that don't throw a trivial exception.
    //   Diffblue Cover tried to run the arrange/act section, but the method under
    //   test threw
    //   java.lang.IllegalArgumentException: 'Scenario action' must not be null
    //       at com.github.drednote.telegram.utils.Assert.notNull(Assert.java:12)
    //       at com.github.drednote.telegram.updatehandler.scenario.ScenarioNodeBuilder.<init>(ScenarioNodeBuilder.java:35)
    //   See https://diff.blue/R013 to resolve this issue.

    LinkedList<StepDefinition> steps = new LinkedList<>();
    ArrayList<TelegramRequestMapping> pattern = new ArrayList<>();
    ActionExecutor action = mock(ActionExecutor.class);
    steps.add(
        new StepDefinition("Scenario start command", pattern, action, new LinkedList<>(),
            "Scenario start command"));
    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name", null,
        steps);

    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
    definitions.add(scenarioDefinition);
    new ScenarioNodeBuilder(definitions);
  }

//  /**
//   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
//   */
//  @Test
//  void testConstructor11() {
//    ArrayList<RequestMappingInfo> pattern = new ArrayList<>();
//    RequestMappingInfo requestMappingInfo = new RequestMappingInfo(
//        "Scenario start command",
//        RequestType.COMMAND);
//
//    pattern.add(requestMappingInfo);
//    ActionExecutor action = mock(ActionExecutor.class);
//    StepDefinition stepDefinition = new StepDefinition("Scenario start command", pattern, action,
//        new LinkedList<>(),
//        "Scenario start command");
//
//    LinkedList<StepDefinition> steps = new LinkedList<>();
//    steps.add(stepDefinition);
//    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name",
//        mock(ActionExecutor.class),
//        steps);
//
//    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
//    definitions.add(scenarioDefinition);
//    ScenarioNodeBuilder actualScenarioNodeBuilder = new ScenarioNodeBuilder(definitions);
//    Map<String, Node> flatNodes = actualScenarioNodeBuilder.getFlatNodes();
//    assertEquals(2, flatNodes.size());
//    List<Node> scenarios = actualScenarioNodeBuilder.getScenarios();
//    assertEquals(1, scenarios.size());
//    Node getResult = scenarios.get(0);
//    assertNull(getResult.parent);
//    Node getResult2 = flatNodes.get("Scenario start command");
//    assertSame(requestMappingInfo, getResult2.pattern);
//    assertTrue(getResult2.children.isEmpty());
//    assertEquals(1, getResult.children.size());
//    assertEquals("Name", getResult.name);
//    assertEquals("Scenario start command", getResult2.name);
//    assertSame(getResult, getResult2.parent);
//    RequestMappingInfo requestMappingInfo2 = getResult.pattern;
//    assertEquals("Start Command", requestMappingInfo2.getPattern());
//    assertNull(requestMappingInfo2.getMessageType());
//    assertTrue(requestMappingInfo2.getPathMatcher() instanceof AntPathMatcher);
//  }
//
//  /**
//   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
//   */
//  @Test
//  void testConstructor12() {
//    ArrayList<RequestMappingInfo> pattern = new ArrayList<>();
//    pattern.add(new RequestMappingInfo("Scenario start command", RequestType.COMMAND));
//    pattern.add(new RequestMappingInfo("Scenario start command", RequestType.COMMAND));
//    ActionExecutor action = mock(ActionExecutor.class);
//    StepDefinition stepDefinition = new StepDefinition("Scenario start command", pattern, action,
//        new LinkedList<>(),
//        "Scenario start command");
//
//    LinkedList<StepDefinition> steps = new LinkedList<>();
//    steps.add(stepDefinition);
//    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name",
//        mock(ActionExecutor.class),
//        steps);
//
//    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
//    definitions.add(scenarioDefinition);
//    assertThrows(IllegalArgumentException.class, () -> new ScenarioNodeBuilder(definitions));
//  }
//
//  /**
//   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
//   */
//  @Test
//  void testConstructor14() {
//    LinkedList<StepDefinition> steps = new LinkedList<>();
//    ArrayList<RequestMappingInfo> pattern = new ArrayList<>();
//    ActionExecutor action = mock(ActionExecutor.class);
//    steps.add(
//        new StepDefinition("Scenario start command", pattern, action, new LinkedList<>(),
//            "Scenario start command"));
//    StepDefinition stepDefinition = new StepDefinition("Scenario start command", new ArrayList<>(),
//        mock(ActionExecutor.class), steps, "Scenario start command");
//
//    LinkedList<StepDefinition> steps2 = new LinkedList<>();
//    steps2.add(stepDefinition);
//    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name",
//        mock(ActionExecutor.class),
//        steps2);
//
//    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
//    definitions.add(scenarioDefinition);
//    assertThrows(IllegalArgumentException.class, () -> new ScenarioNodeBuilder(definitions));
//  }
//
//  /**
//   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
//   */
//  @Test
//  void testConstructor15() {
//    LinkedList<StepDefinition> steps = new LinkedList<>();
//    ArrayList<RequestMappingInfo> pattern = new ArrayList<>();
//    ActionExecutor action = mock(ActionExecutor.class);
//    steps.add(new StepDefinition("Scenario action", pattern, action, new LinkedList<>(),
//        "Scenario start command"));
//    StepDefinition stepDefinition = new StepDefinition("Scenario start command", new ArrayList<>(),
//        mock(ActionExecutor.class), steps, "Scenario start command");
//
//    LinkedList<StepDefinition> steps2 = new LinkedList<>();
//    steps2.add(stepDefinition);
//    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name",
//        mock(ActionExecutor.class),
//        steps2);
//
//    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
//    definitions.add(scenarioDefinition);
//    ScenarioNodeBuilder actualScenarioNodeBuilder = new ScenarioNodeBuilder(definitions);
//    Map<String, Node> flatNodes = actualScenarioNodeBuilder.getFlatNodes();
//    assertEquals(3, flatNodes.size());
//    List<Node> scenarios = actualScenarioNodeBuilder.getScenarios();
//    assertEquals(1, scenarios.size());
//    Node getResult = flatNodes.get("Scenario start command");
//    Node getResult2 = flatNodes.get("Scenario action");
//    assertSame(getResult, getResult2.parent);
//    RequestMappingInfo requestMappingInfo = getResult2.pattern;
//    RequestMappingInfo requestMappingInfo2 = getResult.pattern;
//    assertEquals(requestMappingInfo, requestMappingInfo2);
//    Node getResult3 = scenarios.get(0);
//    assertEquals(1, getResult3.children.size());
//    assertEquals(1, getResult.children.size());
//    assertEquals("Name", getResult3.name);
//    assertNull(getResult3.parent);
//    assertEquals("Scenario start command", getResult.name);
//    assertTrue(getResult2.children.isEmpty());
//    assertSame(getResult3, getResult.parent);
//    assertEquals("Scenario action", getResult2.name);
//    RequestMappingInfo requestMappingInfo3 = getResult3.pattern;
//    assertEquals("Start Command", requestMappingInfo3.getPattern());
//    assertTrue(requestMappingInfo.getPathMatcher() instanceof AntPathMatcher);
//    assertTrue(requestMappingInfo2.getPathMatcher() instanceof AntPathMatcher);
//    assertEquals("**", requestMappingInfo2.getPattern());
//    assertEquals("**", requestMappingInfo.getPattern());
//    assertNull(requestMappingInfo.getMessageType());
//    assertTrue(requestMappingInfo3.getPathMatcher() instanceof AntPathMatcher);
//    assertNull(requestMappingInfo3.getMessageType());
//    assertNull(requestMappingInfo2.getMessageType());
//    StepDefinition getResult4 = definitions.get(0).steps().get(0);
//    assertEquals(1, getResult4.pattern().size());
//    assertEquals(1, getResult4.steps().get(0).pattern().size());
//  }
//
//  /**
//   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
//   */
//  @Test
//  void testConstructor16() {
//    LinkedList<StepDefinition> steps = new LinkedList<>();
//    ArrayList<RequestMappingInfo> pattern = new ArrayList<>();
//    ActionExecutor action = mock(ActionExecutor.class);
//    steps.add(
//        new StepDefinition("Scenario start command", pattern, action, new LinkedList<>(),
//            "Scenario start command"));
//    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("/", null,
//        mock(ActionExecutor.class), steps);
//
//    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
//    definitions.add(scenarioDefinition);
//    ScenarioNodeBuilder actualScenarioNodeBuilder = new ScenarioNodeBuilder(definitions);
//    Map<String, Node> flatNodes = actualScenarioNodeBuilder.getFlatNodes();
//    assertEquals(2, flatNodes.size());
//    List<Node> scenarios = actualScenarioNodeBuilder.getScenarios();
//    assertEquals(1, scenarios.size());
//    Node getResult = scenarios.get(0);
//    assertNull(getResult.parent);
//    Node getResult2 = flatNodes.get("Scenario start command");
//    assertTrue(getResult2.children.isEmpty());
//    assertEquals(1, getResult.children.size());
//    assertEquals("", getResult.name);
//    assertEquals("Scenario start command", getResult2.name);
//    assertSame(getResult, getResult2.parent);
//    RequestMappingInfo requestMappingInfo = getResult.pattern;
//    assertEquals("/", requestMappingInfo.getPattern());
//    assertNull(requestMappingInfo.getMessageType());
//    RequestMappingInfo requestMappingInfo2 = getResult2.pattern;
//    assertTrue(requestMappingInfo2.getPathMatcher() instanceof AntPathMatcher);
//    assertEquals("**", requestMappingInfo2.getPattern());
//    assertNull(requestMappingInfo2.getMessageType());
//    assertTrue(requestMappingInfo.getPathMatcher() instanceof AntPathMatcher);
//    assertEquals(1, definitions.get(0).steps().get(0).pattern().size());
//  }
//
//  /**
//   * Method under test: {@link ScenarioNodeBuilder#ScenarioNodeBuilder(LinkedList)}
//   */
//  @Test
//  void testConstructor17() {
//    ArrayList<RequestMappingInfo> pattern = new ArrayList<>();
//    pattern.add(new RequestMappingInfo("Scenario start command", RequestType.COMMAND));
//    pattern.add(null);
//    ActionExecutor action = mock(ActionExecutor.class);
//    StepDefinition stepDefinition = new StepDefinition("Scenario start command", pattern, action,
//        new LinkedList<>(),
//        "Scenario start command");
//
//    LinkedList<StepDefinition> steps = new LinkedList<>();
//    steps.add(stepDefinition);
//    ScenarioDefinition scenarioDefinition = new ScenarioDefinition("Start Command", "Name",
//        mock(ActionExecutor.class),
//        steps);
//
//    LinkedList<ScenarioDefinition> definitions = new LinkedList<>();
//    definitions.add(scenarioDefinition);
//    assertThrows(IllegalArgumentException.class, () -> new ScenarioNodeBuilder(definitions));
//  }
}

