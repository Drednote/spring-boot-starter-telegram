package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.ActionExecutor;

public interface Step extends ActionExecutor {

  String getName();

  Scenario getRoot();

}
