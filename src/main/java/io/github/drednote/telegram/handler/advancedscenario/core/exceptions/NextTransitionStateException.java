package io.github.drednote.telegram.handler.advancedscenario.core.exceptions;

import lombok.Getter;


@Getter
public class NextTransitionStateException extends RuntimeException {

    private final Enum<?> errorState;

    // Constructor without cause
    public NextTransitionStateException(Enum<?> errorState, String message) {
        super(message); // Pass the message to the parent constructor
        this.errorState = errorState;
    }

    // Constructor with cause
    public NextTransitionStateException(Enum<?> errorState, String message, Throwable cause) {
        super(message, cause); // Pass the message and cause to the parent constructor
        this.errorState = errorState;
    }

    // Constructor with only errorState (automatic message generation)
    public NextTransitionStateException(Enum<?> errorState) {
        super(generateErrorMessage(errorState)); // Automatically generate the message
        this.errorState = errorState;
    }

    private static String generateErrorMessage(Enum<?> errorState) {
        if (errorState == null) {
            return "Error state is null";
        }
        return "Invalid transition to state: " + errorState.name();
    }
}
