package io.github.drednote.examples.scenario;

import io.github.drednote.telegram.core.request.MessageType;
import io.github.drednote.telegram.core.request.RequestType;
import io.github.drednote.telegram.core.request.TelegramRequestImpl;
import io.github.drednote.telegram.datasource.DataSourceAdapter;
import io.github.drednote.telegram.datasource.scenario.jpa.JpaScenarioRepositoryAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.config.ScenarioConfigConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.ScenarioConfigurerAdapter;
import io.github.drednote.telegram.handler.scenario.configurer.state.ScenarioStateConfigurer;
import io.github.drednote.telegram.handler.scenario.configurer.transition.ScenarioTransitionConfigurer;
import io.github.drednote.telegram.handler.scenario.persist.DefaultScenarioPersister;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories(basePackages = "io.github.drednote.examples.scenario")
@EntityScan(basePackageClasses = {DataSourceAdapter.class}, basePackages = "io.github.drednote.examples.scenario")
public class ScenarioConfig extends ScenarioConfigurerAdapter<State> {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioFactory scenarioFactory;

    @Override
    public void onConfigure(@NonNull ScenarioTransitionConfigurer<State> configurer) throws Exception {
        configurer.withExternal().inlineKeyboardCreation()
            .source(State.INITIAL).target(State.TEST)
            .action(scenarioFactory::initialTest)
            .telegramRequest(getTelegramRequest("/test", RequestType.MESSAGE, MessageType.COMMAND))

            .and().withRollback()
            .source(State.TEST).target(State.GET)
            .action(scenarioFactory::choose)
            .telegramRequest(getTelegramRequest("choose-{value:.*}", RequestType.CALLBACK_QUERY, null))
            .rollbackTelegramRequest(getTelegramRequest("back", RequestType.CALLBACK_QUERY, null))
            .rollbackAction(scenarioFactory::scenarioBack)

            .and().withExternal()
            .source(State.GET).target(State.GET)
            .action(scenarioFactory::print)
            .telegramRequest(getTelegramRequest("print", RequestType.CALLBACK_QUERY, null))

            .and();
    }

    @NotNull
    private static TelegramRequestImpl getTelegramRequest(
        String pattern, RequestType requestType, @Nullable MessageType messageType
    ) {
        return new TelegramRequestImpl(Set.of(pattern), Set.of(requestType),
            messageType != null ? Set.of(messageType) : Set.of(), false);
    }

    @Override
    public void onConfigure(ScenarioConfigConfigurer<State> configurer) {
        configurer
            .withPersister(new DefaultScenarioPersister<>(new JpaScenarioRepositoryAdapter<>(scenarioRepository)));
    }

    @Override
    public void onConfigure(ScenarioStateConfigurer<State> configurer) throws Exception {
        configurer.withStates().initial(State.INITIAL);
    }
}
