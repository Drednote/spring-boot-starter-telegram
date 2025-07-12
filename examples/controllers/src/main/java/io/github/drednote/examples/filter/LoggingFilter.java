package io.github.drednote.examples.filter;

import io.github.drednote.telegram.core.annotation.TelegramScope;
import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.post.ConclusivePostUpdateFilter;
import io.github.drednote.telegram.filter.pre.PriorityPreUpdateFilter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@Component
@RequiredArgsConstructor
@TelegramScope
public class LoggingFilter implements PriorityPreUpdateFilter, ConclusivePostUpdateFilter {

    private LocalDateTime startTime;

    @SneakyThrows
    @Override
    public void preFilter(@NonNull UpdateRequest request) {
        startTime = LocalDateTime.now();
        User user = request.getUser();
        String userName = user != null ? user.getId() + " - " + user.getUserName() : null;
        log.info(
            "New request with id {} for userId {}", request.getId(), userName
        );
    }

    @SneakyThrows
    @Override
    public void conclusivePostFilter(@NonNull UpdateRequest request) {
        if (startTime != null) {
            User user = request.getUser();
            log.info(
                "Request with id {} for userId {} processed for {} ms", request.getId(),
                user != null ? user.getId() : null,
                ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())
            );
        }
    }

    @Override
    public int getPreOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

