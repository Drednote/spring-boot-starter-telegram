package io.github.drednote.telegram.testsupport;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.TelegramProperties;
import io.github.drednote.telegram.core.request.DefaultTelegramUpdateRequest;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

@UtilityClass
public class UpdateRequestUtils {

  /**
   * @return request with mocked other properties than update
   */
  public DefaultTelegramUpdateRequest createMockRequest(Update update) {
    AbsSender absSender = Mockito.mock(AbsSender.class);

    return new DefaultTelegramUpdateRequest(update, absSender, new TelegramProperties(),
        new ObjectMapper());
  }
}
