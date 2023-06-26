package com.github.drednote.telegram.session;

import java.util.List;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.telegram.telegrambots.meta.api.objects.Update;

@FeignClient(url = "https://api.telegram.org", name = "updates")
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
