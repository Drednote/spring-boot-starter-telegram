package io.github.drednote.telegram.core.annotation;

import io.github.drednote.telegram.response.ForbiddenTelegramResponse;
import io.github.drednote.telegram.response.GenericTelegramResponse;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying required roles for a Telegram handler method.
 * <p>
 * The method annotated with {@code @HasRole} will be checked during request processing, and access will be granted only
 * if the current user has the required roles according to the specified matching strategy.
 * </p>
 *
 * <p>Supported matching strategies:
 * <ul>
 *   <li>{@link StrategyMatching#ANY} – at least one required role must be present.</li>
 *   <li>{@link StrategyMatching#ALL} – all specified roles must be present.</li>
 * </ul>
 * </p>
 * <p>
 * If validation fails, the request is blocked and a response is added, either a {@link ForbiddenTelegramResponse} or a
 * {@link GenericTelegramResponse} based on the annotation's {@code description} field.
 *
 * @author Ivan Galushko
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasRole {

    /**
     * One or more required roles that the current user must have.
     *
     * @return the array of role names
     */
    String[] value();

    /**
     * Optional description that will be sent to the user in case of access denial.
     *
     * @return a textual description or message
     */
    String description() default "";

    /**
     * Strategy used to match the user's roles against the required ones. Defaults to
     * {@link StrategyMatching#ANY}.
     *
     * @return the matching strategy
     */
    StrategyMatching strategyMatching() default StrategyMatching.ANY;

    /**
     * Role matching strategy for evaluating the user's permissions.
     */
    enum StrategyMatching {
        /**
         * Requires at least one of the specified roles to be present.
         */
        ANY,

        /**
         * Requires all specified roles to be present.
         */
        ALL
    }
}
