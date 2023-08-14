package io.github.drednote.telegram.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.facilities.TelegramHttpClientBuilder;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class TelegramClientImpl implements TelegramClient {

  private final CloseableHttpClient httpClient;
  private final DefaultBotOptions options;
  private final ObjectMapper objectMapper;

  public TelegramClientImpl(
      SessionProperties properties, ObjectMapper objectMapper
  ) {
    this.options = properties.toBotOptions();
    this.httpClient = TelegramHttpClientBuilder.build(options);
    this.objectMapper = objectMapper;
  }

  @Override
  public List<Update> getUpdates(
      String token, Integer offset, Integer limit, Integer timeout,
      @Nullable List<String> allowedUpdates
  ) throws IOException, TelegramApiRequestException {
    GetUpdates request = GetUpdates
        .builder()
        .limit(limit)
        .timeout(timeout)
        .offset(offset)
        .build();

    if (allowedUpdates != null) {
      request.setAllowedUpdates(allowedUpdates);
    }

    String url = options.getBaseUrl() + token + "/" + GetUpdates.PATH;
    HttpPost httpPost = new HttpPost(url);
    httpPost.addHeader("charset", StandardCharsets.UTF_8.name());
    httpPost.setConfig(options.getRequestConfig());
    httpPost.setEntity(
        new StringEntity(objectMapper.writeValueAsString(request), ContentType.APPLICATION_JSON));

    try (CloseableHttpResponse response = httpClient.execute(httpPost, options.getHttpContext())) {
      String responseContent = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

      if (response.getStatusLine().getStatusCode() >= 500) {
        throw new TelegramApiRequestException("Receive 500 status from HttpClient");
      } else {
        return request.deserializeResponse(responseContent);
      }
    }
  }
}
