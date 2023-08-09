package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.util.List;

public interface UpdateFilterProvider {

  List<UpdateFilter> getPreFilters(TelegramUpdateRequest request);

  List<UpdateFilter> getPostFilters(TelegramUpdateRequest request);
}
