package com.github.drednote.telegram;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@UtilityClass
public class UpdateUtils {

  public Update createCommandUpdate(String command) {
    Update update = new Update();

    User user = new User();
    user.setId(2L);

    Message message = new Message();
    message.setText(command);
    message.setEntities(List.of(new MessageEntity(EntityType.BOTCOMMAND, 0, command.length())));
    message.setFrom(user);

    update.setMessage(message);
    update.setUpdateId(1);

    return update;
  }
}
