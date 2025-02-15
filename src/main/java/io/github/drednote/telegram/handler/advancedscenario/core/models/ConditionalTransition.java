package io.github.drednote.telegram.handler.advancedscenario.core.models;


import com.esotericsoftware.kryo.NotNull;
import lombok.Getter;
import org.json.JSONObject;

import java.util.function.Predicate;

@Getter
public class ConditionalTransition<E extends Enum<E>> {

    @NotNull
    private final E transitionState; // Parameter transitionState
    @NotNull
    private final Predicate<JSONObject> condition; // Lambda that returns boolean

    // Constructor to initialize the fields
    public ConditionalTransition(E transitionState, Predicate<JSONObject> condition) {
        this.transitionState = transitionState;
        this.condition = condition;
    }

}
