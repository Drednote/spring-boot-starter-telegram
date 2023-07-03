package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.UpdateRequest;
import com.github.drednote.telegram.updatehandler.scenario.Scenario.Cancel;
import com.github.drednote.telegram.updatehandler.scenario.Scenario.Step;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

public class ScenarioBuilder {

  private String startCommand;
  Cancel cancel;
  final List<Step> steps = new LinkedList<>();

  public ScenarioBuilder startCommand(String s) {
    this.startCommand = s;
    return this;
  }

  public StepBuilder step() {
    return new StepBuilder(this);
  }

  public CancelBuilder withCancel() {
    return new CancelBuilder(this);
  }

  public Scenario build() {
    return new Scenario(startCommand, steps, cancel);
  }

  @RequiredArgsConstructor
  public class StepBuilder {

    private final ScenarioBuilder scenarioBuilder;
    private Function<UpdateRequest, ?> request;

    public StepBuilder withRequest(Function<UpdateRequest, ?> request) {
      this.request = request;
      return this;
    }

    public ScenarioBuilder next() {
      steps.add(new Step(request));
      return scenarioBuilder;
    }
  }

  @RequiredArgsConstructor
  public class CancelBuilder {

    private final ScenarioBuilder scenarioBuilder;
    private String command;
    private Function<UpdateRequest, ?> action;


    public CancelBuilder command(String command) {
      this.command = command;
      return this;
    }

    public CancelBuilder action(Function<UpdateRequest, ?> action) {
      this.action = action;
      return this;
    }

    public ScenarioBuilder finish() {
      scenarioBuilder.cancel = new Cancel(command, action);
      return scenarioBuilder;
    }

  }
}
