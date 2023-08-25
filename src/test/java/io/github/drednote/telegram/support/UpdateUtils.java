package io.github.drednote.telegram.support;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@UtilityClass
public class UpdateUtils {

  public Update createEmpty() {
    Update update = new Update();

    User user = new User();
    user.setId(2L);

    Message message = new Message();
    message.setFrom(user);
    message.setChat(new Chat(1L, "simple"));
    update.setMessage(message);
    update.setUpdateId(1);

    return update;
  }

  public Update createCommand(String command) {
    Update update = createEmpty();

    Message message = update.getMessage();
    message.setText(command);
    message.setEntities(List.of(new MessageEntity(EntityType.BOTCOMMAND, 0, command.length())));

    return update;
  }

  public Update createMessage(String text) {
    Update update = createEmpty();

    Message message = update.getMessage();
    message.setText(text);

    return update;
  }
}
