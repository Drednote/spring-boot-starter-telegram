package io.github.drednote.telegram.response;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.utils.Assert;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * This class provides a way to execute multiple {@code TelegramResponse} instances in a sequential manner. It is
 * typically used to compose multiple response actions and execute them together.
 * <p>
 * Note: It is recommended to avoid using this class directly. Instead, if you need to execute many
 * {@code TelegramResponse} instances, you can return a {@code Collection} of {@code TelegramResponse} directly from
 * your handler method.
 *
 * @author Ivan Galushko
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CompositeTelegramResponse extends AbstractTelegramResponse {

    private final List<?> invoked;

    /**
     * Constructs a {@code CompositeTelegramResponse} with a collection of invoked TelegramResponse instances.
     *
     * @param invoked The collection of {@code TelegramResponse} instances to be invoked
     */
    public CompositeTelegramResponse(Collection<?> invoked) {
        Assert.required(invoked, "Collection of TelegramResponse");
        this.invoked = invoked.stream()
            .filter(handlerResponse -> handlerResponse != this)
            .sorted(AnnotationAwareOrderComparator.INSTANCE)
            .toList();
    }

    /**
     * Constructs a {@code CompositeTelegramResponse} with a collection of invoked TelegramResponse instances.
     *
     * @param invoked The collection of {@code TelegramResponse} instances to be invoked
     */
    public CompositeTelegramResponse(Object... invoked) {
        this(Arrays.asList(invoked));
    }

    /**
     * Processes each of the invoked TelegramResponse instances sequentially
     *
     * @param request The UpdateRequest containing the update information
     * @throws TelegramApiException if processing any of the invoked responses fails
     */
    @Override
    public void process(UpdateRequest request) throws TelegramApiException {
        for (Object response : invoked) {
            TelegramResponseHelper.create(wrapWithTelegramResponse(response))
                .propagateProperties(this)
                .process(request);
        }
    }
}
