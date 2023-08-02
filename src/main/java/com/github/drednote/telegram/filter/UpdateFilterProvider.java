package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.request.BotRequest;
import java.util.List;

public interface UpdateFilterProvider {

  List<UpdateFilter> getPreFilters(BotRequest request);

  List<UpdateFilter> getPostFilters(BotRequest request);
}
