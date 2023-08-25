package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import io.github.drednote.telegram.filter.post.PostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PreUpdateFilter;
import java.util.List;

public interface UpdateFilterProvider {

  List<PreUpdateFilter> getPreFilters(TelegramUpdateRequest request);

  List<PostUpdateFilter> getPostFilters(TelegramUpdateRequest request);
}
