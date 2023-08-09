package io.github.drednote.telegram.session;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Scope(scopeName = TelegramSessionScope.BOT_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
public @interface TelegramScope {
}
