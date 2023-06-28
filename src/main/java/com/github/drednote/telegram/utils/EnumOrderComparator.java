package com.github.drednote.telegram.utils;

import java.util.Comparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

public class EnumOrderComparator implements Comparator<Enum<?>> {

  public static final EnumOrderComparator INSTANCE = new EnumOrderComparator();

  private EnumOrderComparator() {
  }

  @Override
  public int compare(Enum<?> o1, Enum<?> o2) {
    return getValue(o1) - getValue(o2);
  }

  private int getValue(Enum<?> anEnum) {
    if (anEnum == null) {
      return Ordered.LOWEST_PRECEDENCE;
    }
    try {
      Order annotation = anEnum.getClass().getDeclaredField(anEnum.name())
          .getAnnotation(Order.class);
      return annotation != null ? annotation.value() : Ordered.LOWEST_PRECEDENCE;
    } catch (NoSuchFieldException e) {
      return Ordered.LOWEST_PRECEDENCE;
    }
  }
}
