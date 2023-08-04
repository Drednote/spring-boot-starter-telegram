package com.github.drednote.telegram.session;

import java.util.List;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramClient {

  @GetMapping("/bot{token}/getUpdates")
  Response getUpdates(
      @PathVariable("token") String token,
      @RequestParam(value = "offset", required = false) Integer offset,
      @RequestParam(value = "limit", required = false) Integer limit,
      @RequestParam(value = "timeout", required = false) Integer timeout,
      @RequestParam(value = "allowed_updates", required = false) List<String> allowedUpdates
  );

  @Data
  class Response {

    private Boolean ok;
    private List<Update> result;

    public boolean isOk() {
      return Boolean.TRUE.equals(this.ok);
    }
  }
}
