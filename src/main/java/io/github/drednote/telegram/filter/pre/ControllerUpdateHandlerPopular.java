package io.github.drednote.telegram.filter.pre;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.controller.HandlerMethodPopular;
import io.github.drednote.telegram.utils.Assert;

/**
 * {@code ControllerUpdateHandlerPopular} is a pre-update filter that populates the necessary
 * information from the incoming update request using a specific handler method.
 *
 * @author Ivan Galushko
 */
public class ControllerUpdateHandlerPopular implements PriorityPreUpdateFilter {

    private final HandlerMethodPopular handlerMethodPopular;

    /**
     * Constructs a {@code ControllerUpdateHandlerPopular} with the given handler.
     *
     * @param handlerMethodPopular the component responsible for populating the necessary
     *                             information from the incoming update request; must not be null.
     */
    public ControllerUpdateHandlerPopular(HandlerMethodPopular handlerMethodPopular) {
        Assert.required(handlerMethodPopular, "HandlerMethodPopular");

        this.handlerMethodPopular = handlerMethodPopular;
    }

    /**
     * Applies the pre-filter step to the given update request by populating it with the necessary
     * information.
     *
     * @param request the {@code UpdateRequest} to populate.
     */
    @Override
    public void preFilter(UpdateRequest request) {
        handlerMethodPopular.populate(request);
    }

    /**
     * Returns the pre-order value for this filter.
     *
     * @return the integer value representing the pre-order of this filter in the filter chain.
     */
    @Override
    public int getPreOrder() {
        return FilterOrder.PRIORITY_PRE_FILTERS.get(this.getClass());
    }
}

