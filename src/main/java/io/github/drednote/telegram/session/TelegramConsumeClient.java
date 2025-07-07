package io.github.drednote.telegram.session;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Interface representing a Telegram client for interacting with the Telegram API.
 *
 * @author Ivan Galushko
 * @see Update
 */
@HttpExchange(
    contentType = "application/json",
    accept = "application/json"
)
public interface TelegramConsumeClient {

    /**
     * Retrieves updates from the Telegram Bot API.
     *
     * <p>This method sends a GET request to the "getUpdates" endpoint of the Telegram Bot API
     * using the provided bot token. It allows specifying optional parameters such as offset, limit,
     * timeout, and allowed updates.
     *
     * <p>This method may throw exceptions related to network errors or Telegram API request
     * failures.
     *
     * @param token          The bot token used for authentication
     * @param offset         The offset of the first update to be retrieved
     * @param limit          The maximum number of updates to be retrieved
     * @param timeout        The maximum time in seconds for long polling
     * @param allowedUpdates A list of allowed update types
     * @return A list of {@link Update} instances representing the retrieved updates
     */
    @GetExchange("/bot{token}/getUpdates")
    UpdateResponse getUpdates(
        @PathVariable("token") String token,
        @RequestParam(value = "offset", required = false) @Nullable Integer offset,
        @RequestParam(value = "limit", required = false) @Nullable Integer limit,
        @RequestParam(value = "timeout", required = false) @Nullable Integer timeout,
        @RequestParam(value = "allowed_updates", required = false) @Nullable List<String> allowedUpdates
    );
}
