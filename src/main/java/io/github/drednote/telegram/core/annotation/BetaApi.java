package io.github.drednote.telegram.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker of a public API that is still in "beta" non-final version.
 * <p>
 * This annotation signals that the annotated public API (package, class, method or field) has not
 * been fully stabilized yet. As such, the API is subject to backward-incompatible changes (or even
 * removal) in a future release
 * <p>
 * This annotation does not indicate inferior quality or performance of the API, just informs that
 * the API may still evolve in the future in a backward-incompatible ways
 * <p>
 * Once a {@code @BetaApi}-annotated API reaches the desired maturity, the {@code @BetaApi}
 * annotation will be removed from such API, and the API will become part of a stable public API.
 *
 * @author Ivan Galushko
 */
@Target({
    ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE,
    ElementType.PACKAGE
})
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface BetaApi {

}
