package com.github.drednote.telegram.core.matcher;

import com.github.drednote.telegram.core.UpdateRequest;

public interface RequestMatcher {

  boolean matches(UpdateRequest request);
}
