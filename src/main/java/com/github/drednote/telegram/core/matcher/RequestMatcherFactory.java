package com.github.drednote.telegram.core.matcher;

import com.github.drednote.telegram.core.request.RequestMappingInfo;
import java.util.ArrayList;
import java.util.Collection;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestMatcherFactory {

  public RequestMatcher create(RequestMappingInfo mapping) {
    return new CompositeRequestMatcher(getMatchers(mapping));
  }

  public Collection<RequestMatcher> getMatchers(RequestMappingInfo info) {
    Collection<RequestMatcher> matchers = new ArrayList<>();

    matchers.add(new TextRequestMatcher(info));

    return matchers;
  }
}
