package io.github.drednote.telegram.datasource.permission.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Set;

@Converter
public class ArrayAttributeConverter implements AttributeConverter<Set<String>, String> {

  private static final String SPLIT_CHAR = ";";

  @Override
  public String convertToDatabaseColumn(Set<String> stringList) {
    return stringList != null ? String.join(SPLIT_CHAR, stringList) : "";
  }

  @Override
  public Set<String> convertToEntityAttribute(String string) {
    return string != null ? Set.of(string.split(SPLIT_CHAR)) : Set.of();
  }
}