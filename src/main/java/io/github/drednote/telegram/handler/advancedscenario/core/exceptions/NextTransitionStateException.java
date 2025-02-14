package io.github.drednote.telegram.handler.advancedscenario.core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NextTransitionStateException extends RuntimeException {
    Enum<?> errorState;
}
