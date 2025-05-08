package io.github.drednote.telegram.filter.pre;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.advancedscenario.AdvancedScenarioConfigurationBeanPostProcessor;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioManager;

import java.util.List;

public class AdvancedScenarioUpdateHandlerPopular implements PriorityPreUpdateFilter {
    private final List<AdvancedScenarioConfigurationBeanPostProcessor.AdvancedScenarioInfo> scenarios;

    public AdvancedScenarioUpdateHandlerPopular(List<AdvancedScenarioConfigurationBeanPostProcessor.AdvancedScenarioInfo> scenarios) {
        this.scenarios = scenarios;
    }


    @Override
    public void preFilter(UpdateRequest request) {
        if (!this.scenarios.isEmpty()) {
            AdvancedScenarioManager advancedScenarioManager = new AdvancedScenarioManager();
            for (AdvancedScenarioConfigurationBeanPostProcessor.AdvancedScenarioInfo scenarioInfo : this.scenarios) {
                advancedScenarioManager.addScenario(scenarioInfo.name(), scenarioInfo.isSubScenario(), scenarioInfo.scenario());
            }
            request.getAccessor().setAdvancedScenarioManager(advancedScenarioManager);
        }
    }

    @Override
    public int getPreOrder() {
        return FilterOrder.PRIORITY_PRE_FILTERS.get(this.getClass());
    }
}
