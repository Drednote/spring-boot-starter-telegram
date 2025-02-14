package io.github.drednote.telegram.filter.pre;

import io.github.drednote.telegram.core.request.UpdateRequest;
import io.github.drednote.telegram.filter.FilterOrder;
import io.github.drednote.telegram.handler.advancedscenario.AdvancedScenarioConfigurationBeanPostProcessor;
import io.github.drednote.telegram.handler.advancedscenario.core.AdvancedScenarioManager;
import io.github.drednote.telegram.handler.advancedscenario.core.interfaces.IAdvancedScenarioConfig;

import java.util.List;

public class AdvancedScenarioUpdateHandlerPopular implements PriorityPreUpdateFilter {
    private final List<IAdvancedScenarioConfig> advancedScenarioConfigs;

    public AdvancedScenarioUpdateHandlerPopular(List<IAdvancedScenarioConfig> advancedScenarioConfigs) {
        this.advancedScenarioConfigs = advancedScenarioConfigs;
    }


    @Override
    public void preFilter(UpdateRequest request) {
        if (!this.advancedScenarioConfigs.isEmpty()) {
            AdvancedScenarioManager advancedScenarioManager = new AdvancedScenarioManager();
          /*  for (AdvancedScenarioConfigurationBeanPostProcessor.AdvancedScenarioInfo scenarioInfo : this.advancedScenarioConfigs) {
                advancedScenarioManager.addScenario(scenarioInfo.name(), scenarioInfo.scenario());
            }*/
            request.getAccessor().setAdvancedScenarioManager(advancedScenarioManager);
        }
    }

    @Override
    public int getPreOrder() {
        return FilterOrder.PRIORITY_PRE_FILTERS.get(this.getClass());
    }
}
