package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.request.BotRequest;
import org.springframework.core.Ordered;

public interface UpdateFilter extends Ordered {

  void filter(BotRequest request) throws Exception;
}
