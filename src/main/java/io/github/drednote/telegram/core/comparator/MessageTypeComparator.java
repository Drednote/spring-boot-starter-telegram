package io.github.drednote.telegram.core.comparator;

import io.github.drednote.telegram.core.request.MessageType;
import java.util.Set;

/**
 * A comparator implementation for comparing sets of {@link MessageType}. This comparator compares
 * sets based on their sizes in descending order
 *
 * @author Galushko Ivan
 */
public final class MessageTypeComparator extends DefaultComparator<Set<MessageType>> {

  /**
   * The singleton instance of the {@code MessageTypeComparator}
   */
  public static final MessageTypeComparator INSTANCE = new MessageTypeComparator();

  private MessageTypeComparator() {
  }

  /**
   * Compares two sets of {@link MessageType} based on their sizes in descending order
   *
   * @param o1 The first set of MessageType to be compared, not null
   * @param o2 The second set of MessageType to be compared, not null
   * @return A negative integer, zero, or a positive integer as the second set's size is greater
   * than, equal to, or less than the first set's size
   */
  public int doCompare(Set<MessageType> o1, Set<MessageType> o2) {
    return o2.size() - o1.size();
  }
}
