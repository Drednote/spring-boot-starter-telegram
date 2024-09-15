package io.github.drednote.telegram.filter.post;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.scenario.Scenario;
import io.github.drednote.telegram.handler.scenario.ScenarioAccessor;
import java.io.Serializable;
import java.util.List;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;

public class ScenarioIdUpdateFilter implements ConclusivePostUpdateFilter {

    @Override
    public void postFilter(@NonNull UpdateRequest request) throws Exception {
        List<Serializable> responses = request.getResponseFromTelegram();
        Scenario<?> scenario = request.getScenario();
        if (!responses.isEmpty() && scenario != null && scenario.getState().isCallbackQueryState()) {
            for (Serializable response : responses) {
                if (response instanceof Message message) {
                    doFilter(scenario, message);
                }
            }
        }
    }

    private void doFilter(Scenario<?> scenario, Message message) {
        ScenarioAccessor accessor = scenario.getAccessor();
        String messageId = message.getMessageId().toString();
        accessor.getPersister().changeId(scenario, messageId);
    }

    @Override
    public int getPostOrder() {
        return FilterOrder.CONCLUSIVE_POST_FILTERS.get(this.getClass());
    }
}
