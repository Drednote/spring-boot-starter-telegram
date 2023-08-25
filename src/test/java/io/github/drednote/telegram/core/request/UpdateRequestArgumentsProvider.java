package io.github.drednote.telegram.core.request;

import static io.github.drednote.telegram.core.request.AbstractTelegramUpdateRequestTest.Data.createData;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.drednote.telegram.support.UpdateUtils;
import io.github.drednote.telegram.support.builder.UpdateBuilder;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class UpdateRequestArgumentsProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return Stream.of(
        arguments(createData(
            UpdateUtils.createCommand("1"), RequestType.MESSAGE, MessageType.COMMAND, "1",
            false, false, false
        )),
        arguments(createData(
            UpdateBuilder._default("1").message(),
            RequestType.MESSAGE, MessageType.TEXT, "1",
            false, false, false
        )),
        arguments(createData(
            UpdateBuilder._default("1").inlineQuery(),
            RequestType.INLINE_QUERY, List.of(), "1",
            true, false, true
        )),
        arguments(createData(
            UpdateBuilder._default("1").chosenInlineQuery(),
            RequestType.CHOSEN_INLINE_QUERY, List.of(), "1",
            true, false, true
        )),
        arguments(createData(
            UpdateBuilder._default("1").callbackQuery(),
            RequestType.CALLBACK_QUERY, List.of(), "1",
            true, false, true
        )),
        arguments(createData(
            UpdateBuilder._default("1").shippingQuery(),
            RequestType.SHIPPING_QUERY, List.of(), "1",
            true, false, true
        )),
        arguments(createData(
            UpdateBuilder._default("1").preCheckoutQuery(),
            RequestType.PRE_CHECKOUT_QUERY, List.of(), "1",
            true, false, true
        )),
        arguments(createData(
            UpdateBuilder._default("1").poll(),
            RequestType.POLL, List.of(), "1",
            true, true, true
        )),
        arguments(createData(
            UpdateBuilder._default("1").pollAnswer(),
            RequestType.POLL_ANSWER, List.of(), null,
            true, false, true
        )),
        arguments(createData(
            UpdateBuilder._default("1").chatMemberUpdated(),
            RequestType.CHAT_MEMBER_UPDATED, List.of(), null,
            true, false, false
        )),
        arguments(createData(
            UpdateBuilder._default("1").chatJoinRequest(),
            RequestType.CHAT_JOIN_REQUEST, List.of(), null,
            true, false, false
        ))
    );
  }
}
