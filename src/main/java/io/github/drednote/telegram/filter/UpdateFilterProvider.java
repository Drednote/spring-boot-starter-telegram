package io.github.drednote.telegram.filter;

import io.github.drednote.telegram.core.request.ExtendedTelegramUpdateRequest;
import java.util.List;

public interface UpdateFilterProvider {

  List<UpdateFilter> getPreFilters(ExtendedTelegramUpdateRequest request);

  List<UpdateFilter> getPostFilters(ExtendedTelegramUpdateRequest request);
}
