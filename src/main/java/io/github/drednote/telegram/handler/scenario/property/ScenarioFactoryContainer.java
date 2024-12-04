package io.github.drednote.telegram.handler.scenario.property;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

public class ScenarioFactoryContainer implements ScenarioFactoryResolver {

    private final Map<String, HandlerMethod> mappingLookup = new HashMap<>();

    @Override
    @Nullable
    public HandlerMethod resolveAction(String name) {
        return mappingLookup.get(name);
    }

    public void registerAction(Object bean, Method method, TelegramScenarioAction action) {
        HandlerMethod handlerMethod = new HandlerMethod(bean, method);
        String name = resolveHandlerName(bean, method, action, handlerMethod);
        HandlerMethod existingHandler = mappingLookup.get(name);
        if (existingHandler != null) {
            throw new IllegalStateException(
                "\nAmbiguous mapping. Cannot map '" + handlerMethod.getBean() + "' method \n" +
                handlerMethod + "\nto " + name + ": There is already '" +
                existingHandler.getBean() + "' bean method\n" + existingHandler + " mapped.");
        } else {
            mappingLookup.put(name, handlerMethod);
        }
    }

    private static String resolveHandlerName(
        Object bean, Method method, TelegramScenarioAction action, HandlerMethod handlerMethod
    ) {
        String name;
        if (action.value().isEmpty()) {
            if (action.fullName()) {
                name = handlerMethod.toString();
            } else {
                name = bean.getClass().getSimpleName() + "#" + method.getName();
            }
        } else {
            name = action.value();
        }
        return name;
    }
}
