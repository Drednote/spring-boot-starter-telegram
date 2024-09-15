package io.github.drednote.telegram.handler.scenario;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.handler.scenario.data.State;

public interface Scenario<S> {

    String getId();

    State<S> getState();

    boolean sendEvent(UpdateRequest request);

    boolean matches(UpdateRequest request);

    boolean isTerminated();

    ScenarioAccessor<S> getAccessor();
}
