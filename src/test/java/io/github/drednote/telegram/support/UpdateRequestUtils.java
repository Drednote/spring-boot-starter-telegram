package io.github.drednote.telegram.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.request.DefaultUpdateRequest;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

@UtilityClass
public class UpdateRequestUtils {

  /**
   * @return request with mocked other properties than update
   */
  public DefaultUpdateRequest createMockRequest(Update update) {
    AbsSender absSender = Mockito.mock(AbsSender.class);

    return new DefaultUpdateRequest(update, absSender, new TelegramProperties(),
        new ObjectMapper());
  }
}
