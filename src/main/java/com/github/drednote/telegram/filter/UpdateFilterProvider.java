package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.UpdateRequest;
import java.util.Collection;

public interface UpdateFilterProvider {

  Collection<UpdateFilter> resolve(UpdateRequest request);
}
