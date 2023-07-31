package com.github.drednote.telegram.core.matcher;

import com.github.drednote.telegram.core.BotRequest;

public interface RequestMatcher {

  boolean matches(BotRequest request);
}
