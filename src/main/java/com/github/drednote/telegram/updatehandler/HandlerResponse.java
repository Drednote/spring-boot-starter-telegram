package com.github.drednote.telegram.updatehandler;

import com.github.drednote.telegram.core.UpdateRequest;
import java.io.IOException;
import org.springframework.core.Ordered;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface HandlerResponse extends Ordered {

  /**
   * Sending method
   *
   * @throws TelegramApiException if sending failed
   * @throws IOException          if error occurred while mapping
   */
  void process(UpdateRequest updateRequest) throws TelegramApiException, IOException;
}
