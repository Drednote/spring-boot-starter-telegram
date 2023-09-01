package io.github.drednote.telegram.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.drednote.telegram.utils.Assert;
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

/**
 * Implementation of the {@link TelegramClient} interface for interacting with the Telegram Bot
 * API.
 *
 * <p>This class implements the {@link TelegramClient} interface to provide methods for sending
 * requests to the Telegram Bot API to retrieve updates using the bot token. It uses an underlying
 * {@link CloseableHttpClient} to perform HTTP requests to the Telegram API.
 *
 * <p>Requests are sent using the provided {@link DefaultBotOptions} and are configured with the
 * properties defined in the {@link SessionProperties}. The class also utilizes an
 * {@link ObjectMapper} for serializing and deserializing JSON data.
 *
 * @author Ivan Galushko
 * @see CloseableHttpClient
 * @see DefaultBotOptions
 */
public class TelegramClientImpl implements TelegramClient {

  private final CloseableHttpClient httpClient;
  private final DefaultBotOptions options;
  private final ObjectMapper objectMapper;

  /**
   * Constructs a TelegramClientImpl with the provided session properties and object mapper.
   *
   * <p>The session properties are converted to {@link DefaultBotOptions}, and the HTTP client is
   * initialized using the {@link TelegramHttpClientBuilder}.
   *
   * @param properties   The session properties containing bot configuration, not null
   * @param objectMapper The object mapper for JSON serialization and deserialization, not null
   */
  public TelegramClientImpl(
      SessionProperties properties, ObjectMapper objectMapper
  ) {
    Assert.required(properties, "SessionProperties");
    Assert.required(objectMapper, "ObjectMapper");

    this.options = properties.toBotOptions();
    this.httpClient = TelegramHttpClientBuilder.build(options);
    this.objectMapper = objectMapper;
  }

  @Override
  public List<Update> getUpdates(
      String token, @Nullable Integer offset, @Nullable Integer limit,
      @Nullable Integer timeout, @Nullable List<String> allowedUpdates
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
