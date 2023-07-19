package com.github.drednote.telegram.updatehandler.mvc;

import com.github.drednote.telegram.utils.RequestTypeComparator;
import java.util.Comparator;

public class CompositeMappingInfoComparator implements Comparator<BotRequestMappingInfo> {

  private final RequestTypeComparator requestTypeComparator = new RequestTypeComparator();

  @Override
  public int compare(BotRequestMappingInfo o1, BotRequestMappingInfo o2) {
    int compare = requestTypeComparator.compare(o1.getType(), o2.getType());
    return compare == 0 ? o1.getComparator().compare(o1.getPattern(), o2.getPattern()) : compare;
  }
}
