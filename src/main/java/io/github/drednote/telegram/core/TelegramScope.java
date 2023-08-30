package io.github.drednote.telegram.core;

import io.github.drednote.telegram.core.request.TelegramUpdateRequest;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.AliasFor;

/**
 * Custom annotation to mark a class as being eligible for Telegram bot request scope.
 *
 * <p>This annotation is used to indicate that a class should be managed within the Telegram bot
 * request scope. Beans marked with this annotation will have their instances created and managed
 * for the duration of a single Telegram bot request.
 *
 * <p>The annotation uses a proxy mode of {@link ScopedProxyMode#TARGET_CLASS} to ensure that
 * a proxy is used to manage the bean's lifecycle correctly.
 *
 * <p>The Telegram bot request scope is designed to be used within a Spring application context
 * to manage beans associated with a {@link TelegramUpdateRequest}. Beans within this scope are
 * created and managed for the duration of a single Telegram bot request.
 *
 * <p>Usage example:
 * <pre>
 * {@code
 * TelegramScope
 * public class MyRequestScopedBean {
 *     // Class definition...
 * }}
 * </pre>
 *
 * @author Ivan Galushko
 * @see TelegramRequestScope
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(scopeName = TelegramRequestScope.BOT_SCOPE_NAME)
public @interface TelegramScope {

  /**
   * @see Scope#proxyMode()
   */
  @AliasFor(annotation = Scope.class)
  ScopedProxyMode proxyMode() default ScopedProxyMode.TARGET_CLASS;
}
