package io.github.drednote.telegram.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@UtilityClass
public class UpdateRequestUtils {

  /**
   * @return request with mocked other properties than update
   */
  public DefaultUpdateRequest createMockRequest(Update update) {
    TelegramClient absSender = Mockito.mock(TelegramClient.class);

    return new DefaultUpdateRequest(update, absSender, new TelegramProperties(),
        new ObjectMapper());
  }
}
