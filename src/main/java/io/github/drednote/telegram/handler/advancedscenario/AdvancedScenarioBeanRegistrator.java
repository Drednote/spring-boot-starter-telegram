package io.github.drednote.telegram.handler.advancedscenario;

import io.github.drednote.telegram.handler.advancedscenario.core.annotations.AdvancedScenarioController;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;

public class AdvancedScenarioBeanRegistrator {
    private final ConfigurableListableBeanFactory beanFactory;

    public AdvancedScenarioBeanRegistrator(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        registerBeans();
    }

    private void registerBeans() {
        try {
            // Create a resolver to scan the classpath
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

            // Scan all classes in the classpath
            String pattern = "classpath*:/**/*.class"; // Pattern to find all classes
            org.springframework.core.io.Resource[] resources = resolver.getResources(pattern);

            for (org.springframework.core.io.Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();

                // Check if the class is annotated with @AdvancedScenarioController
                if (isAnnotatedWithAdvancedScenarioController(className)) {
                    try {
                        Class<?> beanClass = Class.forName(className);

                        // Get the 'name' attribute value from the annotation
                        AdvancedScenarioController annotation = beanClass.getAnnotation(AdvancedScenarioController.class);
                        String beanName = annotation.name();

                        // Register the bean
                        beanFactory.registerSingleton(beanName, beanClass.getDeclaredConstructor().newInstance());
                        beanFactory.autowireBean(beanFactory.getBean(beanName)); // Automatic dependency injection
                    } catch (Exception e) {
                        throw new RuntimeException("Error registering bean: " + className, e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error scanning classpath", e);
        }
    }

    private boolean isAnnotatedWithAdvancedScenarioController(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.isAnnotationPresent(AdvancedScenarioController.class);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
