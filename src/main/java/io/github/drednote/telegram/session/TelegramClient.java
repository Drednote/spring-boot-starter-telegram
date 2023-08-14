package io.github.drednote.telegram.session;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public interface TelegramClient {

  @GetMapping("/bot{token}/getUpdates")
  List<Update> getUpdates(
      @PathVariable("token") String token,
      @RequestParam(value = "offset", required = false) Integer offset,
      @RequestParam(value = "limit", required = false) Integer limit,
      @RequestParam(value = "timeout", required = false) Integer timeout,
      @RequestParam(value = "allowed_updates", required = false) List<String> allowedUpdates
  ) throws IOException, TelegramApiRequestException;
}
