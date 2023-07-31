package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.BotRequest;
import java.util.Collection;

public interface UpdateFilterProvider {

  Collection<UpdateFilter> resolve(BotRequest request);
}
