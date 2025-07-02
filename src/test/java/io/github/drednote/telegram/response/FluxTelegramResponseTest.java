package io.github.drednote.telegram.response;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.drednote.telegram.core.request.UpdateRequest;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import reactor.core.publisher.Flux;

class FluxTelegramResponseTest {

    @Test
    void shouldCorrectProcess3Entities() throws TelegramApiException {
        UpdateRequest request = Mockito.mock(UpdateRequest.class);
        TelegramClient telegramClient = Mockito.mock(TelegramClient.class);
        when(request.getAbsSender()).thenReturn(telegramClient);
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        FluxTelegramResponse response2 = new FluxTelegramResponse(generate(null));
        assertThatNoException().isThrownBy(() -> response2.processReactive(request).block());
        verify(telegramClient, times(3)).execute(messageCaptor.capture());
    }

    @Test
    void shouldCorrectProcess1EntityAndThrowException() throws TelegramApiException {
        UpdateRequest request = Mockito.mock(UpdateRequest.class);
        TelegramClient telegramClient = Mockito.mock(TelegramClient.class);
        when(request.getAbsSender()).thenReturn(telegramClient);
        ArgumentCaptor<SendMessage> messageCaptor = ArgumentCaptor.forClass(SendMessage.class);

        FluxTelegramResponse response2 = new FluxTelegramResponse(generate(new TelegramApiException()));
        assertThatException().isThrownBy(() -> response2.processReactive(request).block())
            .is(new Condition<>(e -> e.getCause() instanceof TelegramApiException, "TelegramApiException"));
        verify(telegramClient, times(1)).execute(messageCaptor.capture());
    }

    private Flux<?> generate(@Nullable Exception exception) {
        return Flux.just("1", "2", "3").flatMapSequential(f -> {
            if (f.equals("2") && exception != null) {
                return Flux.error(exception);
            }
            return Flux.just(f);
        });
    }
}