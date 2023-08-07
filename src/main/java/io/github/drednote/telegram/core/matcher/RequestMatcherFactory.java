package io.github.drednote.telegram.core.matcher;

import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import java.util.ArrayList;
import java.util.Collection;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestMatcherFactory {

  public RequestMatcher create(TelegramRequestMapping mapping) {
    return new CompositeRequestMatcher(getMatchers(mapping));
  }

  public Collection<RequestMatcher> getMatchers(TelegramRequestMapping info) {
    Collection<RequestMatcher> matchers = new ArrayList<>();

    RequestTypeMatcher base = new RequestTypeMatcher(info);
    matchers.add(base.thenMatching(new TextRequestMatcher(info)));

    return matchers;
  }
}
