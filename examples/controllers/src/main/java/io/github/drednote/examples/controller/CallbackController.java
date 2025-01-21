package io.github.drednote.examples.controller;

import io.github.drednote.telegram.core.annotation.TelegramCommand;
import io.github.drednote.telegram.core.annotation.TelegramController;
import io.github.drednote.telegram.core.request.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@TelegramController
@RequiredArgsConstructor
public class CallbackController {

    @TelegramCommand("/callback")
    public Flux<Object> onCallback(UpdateRequest updateRequest) {
        Mono<SendChatAction> typing = Mono.just(SendChatAction.builder()
            .chatId(updateRequest.getChatId())
            .action(ChatActionType.TYPING.getValue())
            .build());

        // create callback that will be executed when a long process is finished
        Mono<String> callback = Mono.defer(() -> {
            try {
                // imitate a long process
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Mono.just("Hello World");
        });

        return Flux.merge(typing, callback);
    }
}
