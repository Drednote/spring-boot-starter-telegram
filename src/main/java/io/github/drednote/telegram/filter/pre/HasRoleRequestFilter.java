package io.github.drednote.telegram.filter.pre;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.datasource.permission.Permission;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.controller.RequestHandler;
import io.github.drednote.telegram.handler.controller.annotation.HasRole;
import io.github.drednote.telegram.handler.controller.annotation.HasRole.StrategyMatching;
import io.github.drednote.telegram.response.ForbiddenTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.telegram.telegrambots.meta.api.objects.User;

public class HasRoleRequestFilter implements PreUpdateFilter {

    private static final Logger log = LoggerFactory.getLogger(HasRoleRequestFilter.class);

    @Override
    public void preFilter(UpdateRequest request) {
        RequestHandler requestHandler = request.getRequestHandler();
        HandlerMethod handlerMethod = requestHandler.handlerMethod();
        HasRole hasRole = handlerMethod.getMethodAnnotation(HasRole.class);
        if (hasRole != null) {
            boolean isValid = isValid(request, hasRole);
            if (!isValid) {
                String description = hasRole.description();
                boolean hasDescription = StringUtils.isNotBlank(description);
                User user = request.getUser();
                String message = "The method '%s' annotated with @HasRole annotation and user '%s - %s' has no matching role '%s'"
                    .formatted(handlerMethod,
                        user != null ? user.getId() : null,
                        user != null ? user.getUserName() : null,
                        Arrays.toString(hasRole.value()));
                log.warn(message);
                request.getAccessor().addResponse(
                    hasDescription ? new GenericTelegramResponse(description) : new ForbiddenTelegramResponse());
            }
        }
    }

    private boolean isValid(UpdateRequest request, HasRole hasRole) {
        Permission permission = Objects.requireNonNull(request.getPermission());
        Set<String> roles = defaultIfNull(permission.getRoles(), Set.of());
        StrategyMatching strategy = hasRole.strategyMatching();
        if (strategy == StrategyMatching.INTERSECTION) {
            return Arrays.stream(hasRole.value()).anyMatch(roles::contains);
        } else {
            return Arrays.stream(hasRole.value()).allMatch(roles::contains);
        }
    }

    @Override
    public boolean matches(UpdateRequest request) {
        return request.getRequestHandler() != null;
    }

    @Override
    public int getPreOrder() {
        return FilterOrder.HIGHEST_PRECEDENCE;
    }
}
