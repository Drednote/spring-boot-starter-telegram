package io.github.drednote.telegram.updatehandler.scenario;

public interface Step extends ActionExecutor {

  String getName();

  Scenario getRoot();

}
