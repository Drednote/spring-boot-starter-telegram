package io.github.drednote.telegram.datasource.session.mongo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.exception.type.TelegramException;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

@WritingConverter
public class UpdateToDocumentConverter implements Converter<Update, Document> {

    private final ObjectMapper objectMapper;

    public UpdateToDocumentConverter(@Nullable ObjectMapper objectMapper) {
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper().findAndRegisterModules();
    }

    @Override
    public Document convert(Update source) {
        try {
            String json = objectMapper.writeValueAsString(source);
            return Document.parse(json);
        } catch (JsonProcessingException e) {
            throw new TelegramException("Cannot serialize Telegram Update to mongo repository", e) {};
        }
    }
}