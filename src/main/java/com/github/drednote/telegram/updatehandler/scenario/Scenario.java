package com.github.drednote.telegram.updatehandler.scenario;

import com.github.drednote.telegram.core.UpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Scenario {

  private final String startCommand;
  @Getter
  private final List<Step> steps;
  @Getter
  private final Cancel cancel;

  public boolean isMatch(UpdateRequest request) {
    return Optional.ofNullable(startCommand)
        .map(c -> c.equals(request.getText()))
        .orElse(false);
  }

  public boolean isCancel(UpdateRequest request) {
    return Optional.ofNullable(cancel)
        .map(Cancel::getCommand)
        .map(c -> c.equals(request.getText()))
        .orElse(false);
  }

  @Override
  public String toString() {
    return "Scenario(startCommand = %s)".formatted(startCommand);
  }

  @RequiredArgsConstructor
  public static class Step {

    @Getter
    private final Function<UpdateRequest, ?> request;
  }

  @Getter(AccessLevel.PACKAGE)
  @RequiredArgsConstructor
  public static class Cancel {

    private final String command;
    private final Function<UpdateRequest, ?> action;

  }
}
