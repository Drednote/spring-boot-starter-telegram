package io.github.drednote.telegram.handler.scenario.data;

import io.github.drednote.telegram.core.matcher.RequestMatcher;
import io.github.drednote.telegram.core.request.UpdateRequestMappingAccessor;
import io.github.drednote.telegram.handler.scenario.Action;
import java.util.Set;

public interface State<S> extends RequestMatcher, Action {

    S getId();

    boolean isCallbackQueryState();

    Set<? extends UpdateRequestMappingAccessor> getUpdateRequestMappings();
}
