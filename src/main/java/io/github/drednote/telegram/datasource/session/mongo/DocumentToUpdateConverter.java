package io.github.drednote.telegram.datasource.session.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.exception.type.TelegramException;
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
}