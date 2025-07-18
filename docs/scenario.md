## Scenario

To create scenarios, you will need to implement the `ScenarioConfigurerAdapter` interface by
creating a **Spring bean**. This interface is the main tool for creating scenarios and allows you to
define and customize the behavior of your scenarios.

Here example of a configuring scenario, for additional info you can see javadocs.

```java

@Configuration
@RequiredArgsConstructor
public class ScenarioConfig extends ScenarioConfigurerAdapter<Enum<?>> {

    private final ScenarioRepository scenarioRepository;

    @Override
    public void onConfigure(@NonNull ScenarioTransitionConfigurer<Enum<?>> configurer) {
        configurer.withExternal().inlineKeyboard()
            .source(State.INITIAL).target(ASSISTANT_CHOICE)
            .telegramRequest(command(ASSISTANT_SETTINGS))
            .action(settingsActionsFactory::returnSettingsMenu)

            .and().withExternal()
            .source(State.INITIAL).target(State.TEST)
            .telegramRequest(command("/test"))
            .action(context -> "Test")

            .and().withRollback()
            .source(ASSISTANT_CHOICE).target(GET_SETTINGS)
            .telegramRequest(callbackQuery(SettingsKeyboardButton.GET_CURRENT))
            .action(settingsActionsFactory.getSettings())
            .rollbackTelegramRequest(callbackQuery(ROLLBACK))
            .rollbackAction(settingsActionsFactory.rollbackToSettingsMenu())

            .and();
    }

    @Override
    public void onConfigure(ScenarioConfigConfigurer<Enum<?>> configurer) {
        configurer
            .withPersister(new JpaScenarioRepositoryAdapter<>(scenarioRepository));
    }

    @Override
    public void onConfigure(ScenarioStateConfigurer<Enum<?>> configurer) {
        configurer.withInitialState(State.INITIAL);
    }
}
```
