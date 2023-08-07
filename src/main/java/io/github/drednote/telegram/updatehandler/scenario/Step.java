package io.github.drednote.telegram.updatehandler.scenario;

import io.github.drednote.telegram.core.ActionExecutor;

public interface Step extends ActionExecutor {

  String getName();

  Scenario getRoot();

}
