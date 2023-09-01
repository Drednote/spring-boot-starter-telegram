package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Abstract base class for implementing custom Telegram response actions.
 *
 * @author Ivan Galushko
 */
public abstract class AbstractTelegramResponse implements TelegramResponse {

  /**
   * Sends a text message to the specified chat using the provided string
   *
   * @param string  The text to send
   * @param request The update request containing the chat information
   * @return The sent message
   * @throws TelegramApiException if sending the message fails
   */
  protected Message sendString(String string, UpdateRequest request)
      throws TelegramApiException {
    AbsSender absSender = request.getAbsSender();
    Long chatId = request.getChatId();
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(chatId);
    sendMessage.setText(string);
    return absSender.execute(sendMessage);
  }
}
