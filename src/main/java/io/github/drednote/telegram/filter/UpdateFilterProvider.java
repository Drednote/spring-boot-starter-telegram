package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.util.List;

public interface UpdateFilterProvider {

  List<PreUpdateFilter> getPreFilters(TelegramUpdateRequest request);

  List<PostUpdateFilter> getPostFilters(TelegramUpdateRequest request);
}
