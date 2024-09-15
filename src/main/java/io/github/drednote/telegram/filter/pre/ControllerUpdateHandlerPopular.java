package io.github.drednote.telegram.filter.pre;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.controller.HandlerMethodPopular;
import io.github.drednote.telegram.utils.Assert;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ControllerUpdateHandlerPopular implements PriorityPreUpdateFilter {

    private final HandlerMethodPopular handlerMethodPopular;

    /**
     * @param handlerMethodPopular The component responsible for populating the necessary information from the incoming
     *                             update request, not null
     */
    public ControllerUpdateHandlerPopular(HandlerMethodPopular handlerMethodPopular) {
        Assert.required(handlerMethodPopular, "HandlerMethodPopular");

        this.handlerMethodPopular = handlerMethodPopular;
    }

    @Override
    public void preFilter(UpdateRequest request) {
        handlerMethodPopular.populate(request);
    }

    @Override
    public int getPreOrder() {
        return FilterOrder.PRIORITY_PRE_FILTERS.get(this.getClass());
    }
}
