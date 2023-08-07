package io.github.drednote.telegram.core.comparator;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.TelegramRequestMapping;
import io.github.drednote.telegram.core.request.RequestType;
import java.util.Comparator;
import java.util.Set;
import org.springframework.util.PathMatcher;

public class RequestMappingInfoComparator extends DefaultComparator<TelegramRequestMapping> {

  private final Comparator<String> patternComparator;
  private static final Comparator<RequestType> requestTypeComparator = RequestTypeComparator.INSTANCE;
  private static final Comparator<Set<MessageType>> messageTypeComparator = MessageTypeComparator.INSTANCE;

  public RequestMappingInfoComparator(PathMatcher pathMatcher) {
    this.patternComparator = new PatternComparator(pathMatcher);
  }

  @Override
  public int doCompare(TelegramRequestMapping first, TelegramRequestMapping second) {
    int compare = requestTypeComparator.compare(first.getRequestType(), second.getRequestType());
    if (compare == 0) {
      compare = messageTypeComparator.compare(first.getMessageTypes(), second.getMessageTypes());
    }
    if (compare == 0) {
      compare = patternComparator.compare(first.getPattern(), second.getPattern());
    }
    return compare;
  }
}
