package com.github.drednote.telegram.filter;

import com.github.drednote.telegram.core.UpdateRequest;
import org.springframework.core.Ordered;

public interface UpdateFilter extends Ordered {

  void filter(UpdateRequest request) throws Exception;
}
