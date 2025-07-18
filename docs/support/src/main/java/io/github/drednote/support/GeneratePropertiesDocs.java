package io.github.drednote.support;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import io.github.drednote.telegram.TelegramProperties;
import jakarta.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.directory.SearchResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class GeneratePropertiesDocs {

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private static final String INDEX_MAIN = "java/main";
    private static final String INDEX_TEST = "java/test";

    public static void main(String[] args) throws Exception {
        List<SearchResult> propertyClasses = search("io.github.drednote.telegram");
        propertyClasses.sort((f, s) -> {
            if (TelegramProperties.class.isAssignableFrom(f.clazz)) {
                return -1;
            } else if (TelegramProperties.class.isAssignableFrom(s.clazz)) {
                return 1;
            }
            return 0;
        });

        List<String> markdownByClass = new LinkedList<>();
        Set<Class<?>> processed = new HashSet<>();

        for (SearchResult searchResult : propertyClasses) {
            String markdown = generateMarkdown(searchResult, null, processed);
            if (markdown != null) {
                markdownByClass.add(markdown);
            }
        }

        String property = System.getProperty("user.dir");
        Path output = Paths.get(property).getParent().resolve("properties/Properties.md");
        Files.createDirectories(output.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write("""
                # Properties
                All settings tables contain 5 columns:
                
                - `Name` - the name of the variable as it is called in the code
                - `Type` - the type of the variable
                - `Description` - a brief description of what this setting does
                - `Default Value` - the default value of the variable
                - `Required` - whether the variable is required
                
                > If the `Required` field is `true` and the value of the `Default Value` column is not equal to `-`,
                > it means that you don't need to manually set the value for the variable. However, if you manually
                > set it to `null` or any value that can be considered empty, the application will not start
                """);
            for (String markdown : markdownByClass) {
                writer.write(markdown);
            }
        }

        System.out.println("📄 Documentation generated to " + output.toAbsolutePath());
    }

    @Nullable
    private static String generateMarkdown(
        SearchResult searchResult, @Nullable List<String> markdownContainer, Set<Class<?>> processed
    ) throws IOException {
        Class<?> clazz = searchResult.clazz;
        if (processed.contains(clazz)) {
            return null;
        }
        processed.add(clazz);
        List<String> container = markdownContainer == null ? new LinkedList<>() : markdownContainer;

        StringBuilder sb = new StringBuilder();
        if (!clazz.getSimpleName().endsWith("Properties")) {
            sb.append("### ");
        } else {
            sb.append("## ");
        }
        sb.append(clazz.getSimpleName()).append("\n\n");
        sb.append("| Name | Type | Description | Default Value | Required |\n");
        sb.append("|------|------|-------------|---------------|----------|\n");

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            String name = field.getName().replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
            String description = getFirstSentenceFromJavadoc(field, searchResult);
            String defaultValue = getDefaultValue(field, clazz);
            String required = isRequired(field) ? "true" : "false";

            Class<?> type = field.getType();
            String fieldType = type.getSimpleName();
            if (!type.isPrimitive() && !type.isEnum()) {
                if (Collection.class.isAssignableFrom(type)) {
                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                    Type argument = genericType.getActualTypeArguments()[0];
                    if (argument instanceof Class<?> collectionClass) {
                        if (collectionClass.getEnclosingClass() != null) {
                            String markdown = generateMarkdown(
                                new SearchResult(searchResult.classPath, searchResult.fullClassPath, collectionClass),
                                container, processed);
                            if (markdown != null) {
                                container.add(markdown);
                            }
                        }
                        fieldType =
                            "[[" + collectionClass.getSimpleName() + "](#" + collectionClass.getSimpleName()
                                .toLowerCase() + ")]";
                    }
                } else if (Map.class.isAssignableFrom(type)) {
                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                    Type argument = genericType.getActualTypeArguments()[1];
                    if (argument instanceof Class<?> mapClazz) {
                        if (mapClazz.getEnclosingClass() != null) {
                            String markdown = generateMarkdown(
                                new SearchResult(searchResult.classPath, searchResult.fullClassPath, mapClazz),
                                container, processed);
                            if (markdown != null) {
                                container.add(markdown);
                            }
                        }
                        fieldType = "{%s : [%s](#%s)}".formatted(
                            ((Class<?>) genericType.getActualTypeArguments()[0]).getSimpleName(),
                            mapClazz.getSimpleName(), mapClazz.getSimpleName().toLowerCase());
                    }
                } else if (!type.getName().startsWith("java.")) {
                    if (type.getEnclosingClass() != null) {
                        String markdown = generateMarkdown(
                            new SearchResult(searchResult.classPath, searchResult.fullClassPath, type),
                            container, processed);
                        if (markdown != null) {
                            container.add(markdown);
                        }
                    }
                    fieldType = " [" + type.getSimpleName() + "](#" + type.getSimpleName().toLowerCase() + ")";
                }
            }
            if (defaultValue == null) {
                defaultValue = "-";
            }

            sb.append("| ").append(name)
                .append(" | ").append(fieldType)
                .append(" | ").append(description)
                .append(" | ").append(defaultValue)
                .append(" | ").append(required).append(" |\n");
        }

        if (markdownContainer == null) {
            Collections.reverse(container);
            for (String markdown : container) {
                sb.append(markdown).append("\n\n");
            }
            sb.append("---\n\n");
        }
        return sb.toString();
    }

    private static String getFirstSentenceFromJavadoc(Field field, SearchResult searchResult) throws IOException {
        String replaced = searchResult.fullClassPath
            .replace(".jar", "-sources.jar")
            .replace(".class", ".java");
        int index = replaced.indexOf("$");
        if (index != -1) {
            replaced = replaced.substring(0, index) + ".java";
        }
        Resource resource = new PathMatchingResourcePatternResolver().getResource(replaced);
        InputStream inputStream = resource.getInputStream();
        ParseResult<CompilationUnit> result = new JavaParser().parse(inputStream);
        CompilationUnit unit = result.getResult()
            .orElseThrow(() -> new IllegalStateException("Cannot parse java class"));

        return unit.getTypes().stream()
            .filter(type -> {
                String id = type.getName().getId();
                String simpleName = searchResult.clazz.getSimpleName();
                return id.equals(simpleName);
            })
            .findFirst().flatMap(typeDeclaration ->
                typeDeclaration.getMembers().stream().filter(member -> {
                    List<Node> childNodes = member.getChildNodes();
                    for (Node childNode : childNodes) {
                        if (childNode instanceof VariableDeclarator variableDeclarator) {
                            if (variableDeclarator.getName().getId().equals(field.getName())) {
                                return true;
                            }
                        }
                    }
                    return false;
                }).findFirst())
            .flatMap(Node::getComment)
            .map(comment -> {
                String header = comment.getHeader();
                String footer = comment.getFooter();
                String text = comment.getContent()
                    .substring(header.length(), comment.getContent().length() - footer.length());
                int endIndex = text.indexOf("*\r\n");
                if (endIndex != -1) {
                    text = text.substring(0, endIndex);
                }
                text = text.replace("\r", "").replace("\n", "").trim();
                if (text.startsWith("*")) {
                    text = text.substring(1);
                }
                text = text.replace(" * ", " ");
                return text;
            })
            .orElse("");
    }

    private static String getDefaultValue(Field field, Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Object value = field.get(instance);
            if (value != null) {
                if (value instanceof Map<?, ?> map) {
                    return map.isEmpty() ? "{}" : map.toString();
                } else if (value instanceof Collection<?> set) {
                    return set.isEmpty() ? "[]" : set.toString();
                } else if (value.getClass().getSimpleName().endsWith("Properties")) {
                    return value.getClass().getSimpleName();
                } else {
                    return value.toString();
                }
            } else {
                return "-";
            }
        } catch (Exception e) {
            return "-";
        }
    }

    private static boolean isRequired(Field field) {
        return field.isAnnotationPresent(NotNull.class) || field.isAnnotationPresent(Nonnull.class)
               || field.isAnnotationPresent(NonNull.class);
    }

    private static List<SearchResult> search(String packageSearchPath) throws ClassNotFoundException {
        List<SearchResult> classes = new ArrayList<>();
        String toSearch = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(
            packageSearchPath) + '/' + DEFAULT_RESOURCE_PATTERN;
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(toSearch);
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename == null || filename.contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
                    // Ignore CGLIB-generated classes in the classpath
                    continue;
                }
                String urlPath = resource.getURI().toString();
                String className = findPathToClass(urlPath);
                if (className.endsWith("Properties")) {
                    classes.add(new SearchResult(className, urlPath,
                        loadClass(ClassUtils.convertResourcePathToClassName(className))));
                }
            }
        } catch (IOException e) {
            throw new BeanCreationException("Failed to load package " + toSearch, e);
        }
        return classes;
    }

    static String findPathToClass(String urlPath) {
        if (urlPath == null) {
            throw new IllegalArgumentException("urlPath must not be null");
        }
        String result = urlPath.replace("\\", "/");
        int indexExclamation = urlPath.lastIndexOf('!');
        if (indexExclamation != -1) {
            result = urlPath.substring(indexExclamation + 1);
        }
        int indexOfMain = result.indexOf(INDEX_MAIN);
        if (indexOfMain != -1) {
            result = result.substring(indexOfMain + INDEX_MAIN.length());
        }
        int indexOfTest = result.indexOf(INDEX_TEST);
        if (indexOfTest != -1) {
            result = result.substring(indexOfTest + INDEX_TEST.length());
        }
        if (result.startsWith("/")) {
            result = result.substring(1);
        }
        return result.substring(0, result.indexOf(".class"));
    }

    private static Class<?> loadClass(String className) throws ClassNotFoundException {
        return ClassUtils.forName(className, GeneratePropertiesDocs.class.getClassLoader());
    }

    record SearchResult(String classPath, String fullClassPath, Class<?> clazz) {}
}
