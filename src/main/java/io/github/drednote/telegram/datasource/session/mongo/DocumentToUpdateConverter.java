package io.github.drednote.telegram.datasource.session.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.exception.type.TelegramException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

@ReadingConverter
public class DocumentToUpdateConverter implements Converter<Document, Update> {

    private final ObjectMapper objectMapper;

    public DocumentToUpdateConverter(@Nullable ObjectMapper objectMapper) {
        this.objectMapper =
            objectMapper != null ? objectMapper : new ObjectMapper().findAndRegisterModules();
    }

    @Override
    public Update convert(Document source) {
        try {
            String json = source.toJson();
            return objectMapper.readValue(json, Update.class);
        } catch (JsonProcessingException e) {
            throw new TelegramException("Cannot deserialize Telegram Update to mongo repository", e) {};
        }
    }

    private static Document replaceUnderscoreIdWithId(Document document) {
        Document newDoc = new Document();

        for (Map.Entry<String, Object> entry : document.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            String newKey;
            if (key.equals("_id")) {
                newKey = "id";
            } else {
                newKey = convertCamelCaseToSnake(key);
            }
            Object newValue = transformValue(value);

            newDoc.put(newKey, newValue);
        }

        return newDoc;
    }

    private static String convertCamelCaseToSnake(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append("_").append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private static Object transformValue(Object value) {
        if (value instanceof Document) {
            return replaceUnderscoreIdWithId((Document) value);
        } else if (value instanceof List<?> list) {
            List<Object> newList = new ArrayList<>();

            for (Object item : list) {
                newList.add(transformValue(item));
            }

            return newList;
        } else {
            return value;
        }
    }
}