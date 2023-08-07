package io.github.drednote.telegram.session;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class TelegramClientImpl implements TelegramClient {

  private static final String BASE_URL = "https://api.telegram.org";
  private static final String UPDATE_URL = "/bot%s/getUpdates";
  private final RestTemplate restTemplate;

  @Override
  public Response getUpdates(
      String token, Integer offset, Integer limit, Integer timeout, List<String> allowedUpdates
  ) {
    UriComponentsBuilder builder = UriComponentsBuilder
        .fromHttpUrl(BASE_URL + UPDATE_URL.formatted(token))
        .queryParam("offset", offset)
        .queryParam("limit", limit)
        .queryParam("timeout", timeout)
        .queryParam("allowed_updates", allowedUpdates);
    return restTemplate.getForEntity(builder.toUriString(), Response.class).getBody();
  }
}
