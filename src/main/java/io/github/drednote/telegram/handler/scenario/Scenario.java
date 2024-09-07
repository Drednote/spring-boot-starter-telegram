package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.data.State;
import io.github.drednote.telegram.handler.scenario.data.Transition;
import java.util.List;

public interface Scenario<S> {

    String getId();

    State<S> getState();

    boolean sendEvent(UpdateRequest request);

    boolean matches(UpdateRequest request);

    boolean isTerminated();

    ScenarioAccessor<S> getAccessor();

    List<? extends Transition<S>> getTransitionsHistory();
}
