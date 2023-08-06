package com.github.drednote.telegram.core.comparator;

import com.github.drednote.telegram.core.request.MessageType;
import java.util.Set;

public final class MessageTypeComparator extends DefaultComparator<Set<MessageType>> {

  public static final MessageTypeComparator INSTANCE = new MessageTypeComparator();

  private MessageTypeComparator() {
  }

  public int doCompare(Set<MessageType> o1, Set<MessageType> o2) {
    return o2.size() - o1.size();
  }
}
