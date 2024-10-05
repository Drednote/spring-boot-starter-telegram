# Spring Boot Starter Telegram

[![Build](https://github.com/Drednote/spring-boot-starter-telegram/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Drednote/spring-boot-starter-telegram/actions/workflows/build.yml)
[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.drednote/spring-boot-starter-telegram.svg)](https://search.maven.org/artifact/io.github.drednote/spring-boot-starter-telegram)
[![Codecov](https://codecov.io/gh/Drednote/spring-boot-starter-telegram/graph/badge.svg?token=4GGKDCSXH2)](https://codecov.io/gh/Drednote/spring-boot-starter-telegram)

**Spring Boot Starter Telegram** is a library designed to simplify the setup of Telegram bots using
`Spring Boot` and `org.telegram:telegrambots` as the core dependency. It provides several key
features to facilitate the bot development process

## Main Features

1. **Controller Update Handling**: Allows receiving updates from the bot via `TelegramController`
   similar
   to
   `RestController` in Spring. This enables seamless integration of Telegram bot functionality with
   the existing Spring ecosystem.

2. **Customizable Scenarios**: Users can define custom scenarios for their bots, which can be
   configured via Java configuration. These scenarios allow the bot to process user interactions in
   a more organized and structured manner.

3. **Flexible Update Filters**: The library provides the capability to set up custom filters for
   individual bot updates. These filters are executed before and after the user-defined code,
   allowing for pre-processing and post-processing of updates.

4. **Centralized Error Handling**: Leveraging annotations in conjunction with `TelegramAdvice` and
   `TelegramExceptionHandler`, the library offers a centralized approach for handling errors,
   similar to `ControllerAdvice` and `ExceptionHandler` in Spring.

## Getting started

- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
    - [Quick Start](#quick-start)
    - [Overall Information](#overall-information)
    - [Update Handling](#update-handling)
        - [Controllers](#controllers)
        - [Scenario](#scenario)
    - [Filters](#filters)
    - [Response Processing](#response-processing)
    - [Exception Handling](#exception-handling)
    - [Argument Resolving](#argument-resolving)
        - [Java types](#java-types)
        - [TelegramPatternVariable](#telegrampatternvariable)
    - [Telegram Scope](#telegram-scope)
    - [Data Source](#data-source)
    - [Primary Entities](#primary-entities)
        - [Update](#update)
        - [UpdateRequest](#updaterequest)
        - [UpdateHandler](#updatehandler)
        - [UpdateFilter](#updatefilter)
        - [TelegramResponse](#telegramresponse)
        - [TelegramScope](#telegramscope)
- [Additional Info](#additional-info)
- [Configuration](#configuration)
- [Dependencies](#dependencies)
- [Contributing](#contributing)
- [License](#license)
- [Authors](#authors)

## Requirements

`Java 17` or a later version and `Spring Boot 3` as the **Spring Boot Starter Telegram** library is
based on Spring Boot 3. Spring Boot 3 requires Java 17 or higher.

## Installation

To use the **Spring Boot Starter Telegram** library in your project, follow the instructions below
based on your preferred build tool.

The Latest version you can check in **Releases** tab or in **Maven Central** badge

### Maven

Add the repository for the library in your to your `pom.xml` file to fetch the artifact from:

```xml

<repositories>
  <repository>
    <id>central</id>
    <name>Maven Central</name>
    <url>https://repo.maven.apache.org/maven2</url>
  </repository>
</repositories>
```

Add the following dependency to your `pom.xml` file:

```xml

<dependency>
  <groupId>io.github.drednote</groupId>
  <artifactId>spring-boot-starter-telegram</artifactId>
  <version>yourVersion</version> <!-- Replace with the actual version -->
</dependency>
```

### Gradle

Add the following repository to your `build.gradle` file to fetch the artifact from:

```groovy
repositories {
    mavenCentral()
}
```

Add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    // Replace with the actual version
    implementation 'io.github.drednote:spring-boot-starter-telegram:yourVersion'
}
```

## Usage

### Quick Start

Add to `application.yml` your bot token and specify the name of bot

```yaml
drednote:
  telegram:
    name: <Your bot name>
    token: <Your bot token>
```

Or if you preferred properties instead of yml

```properties
drednote.telegram.name=<Your bot name>
drednote.telegram.token=<Your bot token>
```

Create your main controller

```java

@TelegramController
public class MainController {

    @TelegramCommand("/start")
    public String onStart(User user) {
        return "Hello " + user.getFirstName();
    }

    @TelegramMessage
    public String onMessage(UpdateRequest request) {
        return "You sent message with types %s".formatted(request.getMessageTypes());
    }

    @TelegramMessage("My name is {name}")
    public String onPattern(@TelegramPatternVariable("name") String name) {
        return "Hello " + name;
    }

    @TelegramRequest
    public TelegramResponse onAll(UpdateRequest request) {
        return new GenericTelegramResponse("Unsupported command");
    }

}
```

Create your main class

```java

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
```

That's all! Enjoy your bot

### Overall information

This library's implementation closely resembles the familiar structure of `Java HTTP servlets`.

- Upon application startup, a session is established to connect with the Telegram API, initiating
  the reception of updates. These updates are obtained either through a **long polling strategy** or
  by setting up a **webhook** that prompts Telegram to directly transmit updates to the application.

- Upon receiving an [Update](#update), a [UpdateRequest](#updaterequest) object is
  generated. This object serves as the central entity throughout the subsequent update processing
  pipeline. Initially, it contains only the information extracted from the [Update](#update) object.
  As the processing continued, the [UpdateRequest](#updaterequest)
  accumulates additional data, enriching its content.

- At the very beginning of the update processing chain,
  the [UpdateRequest](#updaterequest) is stored in the context of the current
  thread. This is done to create a [Telegram Scope](#telegram-scope).

- After that, the main update processing starts with calls to [Filters](#filters). These filters
  help in determining which updates should be processed further. Or you can put logic in the filter
  that will be executed for each update, for example, log something

- Once the updates are filtered, the available `UpdateHandler` are called in a specific order based
  on their priority. There are different mechanisms available for handling updates, and you can find
  more information about them in the [Update Handling](#update-handling) section

- After the successful processing of updates, the [Filters](#filters) are called again as part of
  the post-processing stage. This gives an opportunity for any additional filtering or actions to be
  applied.

- Once all the processing and filtering are done, the response is processed using a specialized
  mechanism called [Response Processing](#response-processing). This mechanism takes the defined
  response and performs necessary actions, such as sending it back to the user or performing any
  other desired logic.

- Throughout the entire processing chain, there is a dedicated mechanism for handling errors called
  [Exception Handling](#exception-handling). This ensures that any errors or exceptions that occur
  during the processing are properly handled and don't disrupt the flow of the program.

- Additionally, some filters and handlers defined by this library require access to a database. For
  this purpose, they can make use of the [Data Source](#data-source) functionality. This allows them
  to interact with the database and retrieve or store data as needed.

### Update Handling

This library offers users two primary approaches for handling updates: [controllers](#controllers)
and [scenarios](#scenario). Both controllers and scenarios offer their own benefits, so you can
choose the one that suits your bot's requirements and your preferred coding style.
**But I recommend using both**

> If you need to make your own handler, all you have to do is create a **spring bean** that will
> implement the `UpdateHandler` interface and set its execution priority using a spring
> annotations `@Order` if needed. Also, after successful processing of the message, it is necessary
> put in the object `UpdateRequest` response with type `TelegramResponse`, so that update
> processing can be considered successful. If this is not done, further update handlers will be
> called

---

#### Controllers

---

In order to begin receiving updates from **Telegram**, you will first need to create a controller
and specify the criteria for which updates you want to receive.

To assist with the analysis, let's utilize the controller provided in
the [Quick Start](#quick-start) section:

```java

@TelegramController
public class MainController {

    @TelegramCommand("/start")
    public String onStart(User user) {
        return "Hello " + user.getFirstName();
    }

    @TelegramMessage
    public String onMessage(UpdateRequest request) {
        return "You sent message with types %s".formatted(request.getMessageTypes());
    }

    @TelegramMessage("My name is {name}")
    public String onPattern(@TelegramPatternVariable("name") String name) {
        return "Hello " + name;
    }

    @TelegramRequest
    public TelegramResponse onAll(UpdateRequest request) {
        return new GenericTelegramResponse("Unsupported command");
    }

}
```

- In order for an application to register a class as a controller, it must be marked
  annotation `@TelegramController`
- To receive updates, there is an annotation `@TelegramRequest`. Also, to make it easier to work
  with [updates](#update), created several derived annotations. The main ones are `@TelegramCommand`
  and `@TelegramMessage`. As the names imply, with the help of the first one you can only receive
  commands **(Message#isCommand)**, and from the second any messages **(Update#getMessage)**
- The `@TelegramRequest` annotation's **pattern()** parameter takes a string that serves as a
  matching condition for the text in the incoming message, whether it's just a message text or a
  photo caption. For matcher uses `AntPathMatcher`, so you can specify
  any [condition](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html)
  in the string valid for `AntPathMatcher`. For example: `Hello*`, will match any string that starts
  with `Hello`. If you do not specify **pattern()** it will be replaced with the `**` pattern and
  matched with any text.
- If the [update](#update) matches with several patterns that you specified in different methods
  marked with `@TelegramRequest`, than sorted will be applied:
    - `TelegramRequest` without **requestType** has the lowest priority
    - `TelegramRequest` without **messageType** has the lowest priority among the **requestType**
      equal to `RequestType.MESSAGE`
    - `TelegramRequest` with **exclusiveMessageType** set to true and with more elements specified
      in **messageType** takes precedence over others
    - If `TelegramRequest` has the same priority by the above conditions, than the `AntPathMatcher`
      order applied
      > This ensures that the most specific controllers are given precedence in the update
      handling process
- Methods marked with `@TelegramRequest` annotation can accept a specific set of inputs
  parameters as defined in the [Argument resolving](#argument-resolving) section
- Methods marked with `@TelegramRequest` annotation can return any object, as a result. The
  response processing mechanism is detailed in the [Response Processing](#response-processing)
  section
- Also, if you need to get some data from the user's message by a specific pattern, then you can use
  the [TelegramPatternVariable](#telegrampatternvariable)
  annotation
  **Essentially `TelegramPatternVariable` works the same as `PathVariable`.**
- Any uncaught errors that occur during the execution of user code, are caught
  by `ExceptionHandler`. More [here](#exception-handling).

---

#### Scenario

---

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
        configurer.withCreateInlineMessage()
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

---

### Filters

Filters serve as both pre-processing and post-processing mechanisms for the primary stage of update
processing. They allow you to define specific criteria for filtering and manipulating updates before
and after they are processed.

- Filters are needed for several main purposes:
    - To filter updates
    - To execute the code for each update

- To control update filtering, filters can set some properties
  in [UpdateRequest](#updaterequest), such as `response`. If any filter set
  property `response` then the update is considered successful and an attempt will be made to send a
  response
- Filters are called twice: before (pre-filters) the main [Update Handling](#update-handling) and
  after (post-filters). It is important to note that, even if an error occurred during the main
  processing of the update, post-filters will still be executed

- There are two main interfaces for creating a filter:
    - `PreUpdateFilter` - **spring beans** that implement this interface will be called **before**
      the main [Update Handling](#update-handling)
    - `PostUpdateFilter` - **spring beans** that implement this interface will be called **after**
      the main [Update Handling](#update-handling)

- Also, for convenience, two interfaces are created. First one - `PriorityPreUpdateFilter` is
  implemented
  from `PreUpdateFilter` and take precedence over `PreUpdateFilter` and is executed earlier whatever
  returns
  **getPreOrder()**/**getPostOrder()**.
  Second one - `ConclusivePostUpdateFilter` is super to `PreUpdateFilter`, and is executed later
  whatever returns
  **getPreOrder()**/**getPostOrder()**.

- To add a filter, you need to create a **spring bean** that will implement the `PreUpdateFilter`
  or `PostUpdateFilter` interface.

- Additionally, it is possible to create a filter with [Telegram Scope](#telegram-scope). With this
  approach, a unique filter instance is created for each update, and it remains active until the
  processing of the update is completed. This allows for better separation and management of filters
  for individual updates. Example:

```java

@Component
@TelegramScope
public class LoggingFilter implements PriorityPreUpdateFilter, PostUpdateFilter {

    private LocalDateTime startTime;

    @Override
    public void preFilter(@NonNull UpdateRequest request) {
        this.startTime = LocalDateTime.now();
        log.info("Receive request with id {}", request.getId());
    }

    @Override
    public void postFilter(@NonNull UpdateRequest request) {
        log.info("Request with id {} processed for {} ms", request.getId(),
            ChronoUnit.MILLIS.between(startTime, LocalDateTime.now()));
    }

    @Override
    public int getPreOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

```

### Response Processing

After the update processing is complete, it is expected that a response will be sent to the user. To
handle this, there is a component called **Response Processing**, which follows certain rules.

- The response represents by interface `TelegramResponse`
- **Response can only be sent if [Update](#update) has a `chatId`**. So if in update there is
  no `chatId` than you should return `void`
- Any response will automatically be wrapped in the `TelegramResponse` interface and execute sending
  method. Rules of wrapping:
    - `void` or `null` will not trigger sending the response
    - `String` will be wrapped in `GenericTelegramResponse` and execution method will send simple
      text response (`SendMessage`)
    - For `byte[]` same rule like for `String` except that `String` instance will be created
      from `byte[]` (`new String(byte[])`)
    - `BotApiMethod` and `SendMediaBotMethod` will be executed as is.
      > `BotApiMethod` is an abstract class that represents sending object.

      > For `BotApiMethod` or `SendMediaBotMethod` the 'chatId' property will be automatically set
      (only if it is null). If you manually set 'chatId', nothing happens
    - A `TelegramResponse` object will be handled without wrapping.
    - List of `TelegramResponse` will be wrapped in `CompositeTelegramResponse` and execute with
      specified priority.
      > Priority specified by `@Order` annotation
    - For `java object` the `GenericTelegramResponse` will try to serialize it with `Jackson`. In
      simple words will do `objectMapper.writeValueAsString(response)`
    - For more information on wrapping rules, see the `ResponseSetter` and `GenericTelegramResponse`
      classes
- You can create any implementation of `TelegramResponse` for sending response
- Any custom code can be written in `TelegramResponse`, but I strongly recommend using this
  interface only for sending a response to **Telegram**
- If you pass {@link BotApiMethod} or {@link SendMediaBotMethod} in the constructor of this class,
  the 'chatId' property will be automatically set (only if it is null). If you manually
  set 'chatId', nothing happens

### Exception Handling

The purpose of the ExceptionHandler mechanism is to provide centralized error handling. Any errors
that occur during the processing will be caught and sent to the `ExceptionHandler` for further
handling. Here are some important rules to keep in mind:

- To initiate error handling, the class must be annotated with `@TelegramAdvice`. Additionally, you
  need to specify at least one method and annotate it with `@TelegramExceptionHandler`.
- If the user is not marked any method with the `@TelegramExceptionHandler` annotation, the default
  error handling approach will be applied.
- In situations where multiple handlers are present for a particular error, a sorting mechanism is
  implemented to determine the priority of method calls. The higher in the hierarchy the error
  (throwable at the very top) that the handler expects, the lower the priority of the method call
  will be compared to others.
  > This ensures that the most specific error handlers are given precedence in the error handling
  process
- Methods marked with `@TelegramExceptionHandler` annotation can accept a specific set of inputs
  parameters as defined in the [Argument resolving](#argument-resolving) section

### Argument resolving

To provide maximum flexibility when calling custom code through the reflection mechanism, the input
parameters for any method are calculated dynamically according to the following rules:

> If you need to add support for your custom argument, you need to create **spring bean** and
> implement `HandlerMethodArgumentResolver` interface

#### Java types

You can specify arguments based on a java type:

- [Update](#update) and any top-level nested objects within it. `Message`, `Poll`, `InlineQuery`,
  etc.
- [UpdateRequest](#updaterequest)
- `TelegramBot` or any subclasses if you need to consume current telegram bot instance
- `String` arguments will fill with the **text** property
  of [UpdateRequest](#updaterequest) if it exists, or it will be `null`
  > In almost all cases this will be the text of the `Message`
- `Long` arguments will fill with the **chatId** property
  of [UpdateRequest](#updaterequest)
- `Throwable` arguments will fill with the **error** property
  of [UpdateRequest](#updaterequest) if it exists. If no error than it will
  be `null`
  > This type should be only used in methods marked with `@TelegramExceptionHandler`. In other
  > methods, it more likely will be `null`

#### TelegramPatternVariable

You can annotate certain arguments using `@TelegramPatternVariable`. Here are the rules to follow:

- This annotation is only valid if there's text in the update (e.g., message text, photo caption).
  Otherwise, it will be `null`
- This annotation supports only two Java types: `String` and `Map`
- When using `String`, the value of the template variable from the pattern is exposed
- In the case of `Map`, all template variables are exposed

### Telegram Scope

The functionality of `@TelegramScope` is similar to the Spring annotation `@Scope("request")`, with
the difference being that the context is created at the start of update processing instead of at the
request level. By marking a **spring bean** with this annotation, a new instance of the bean will be
created for each update processing.

> It's important to note that each update handling is associated with a specific thread. In cases
> where you create sub-threads within the main thread, you will need to manually bind
> the `UpdateRequest` to the new thread. This can be achieved using
> the `UpdateRequestContext`
> class.

### Data Source

- For some filters and scenarios to work correctly, you need to save information to the database.
  You can save data to the application memory, but then during the restart, all information will be
  lost. Therefore, it is better if you configure the datasource using spring.

- This library is fully based on Spring JPA in working with the database. Therefore, to support
  different databases (postgres, mongo, etc.), using the implementations of `DataSourceAdapter`
  interface
- If you want to add support for a database that currently is not supported, you should to
  create entity and create repository extending `PermissionRepository` or `ScenarioRepository`

> **Currently supported `JpaRepository` and `MongoRepository`**

> Note: To enable auto scan for jpa entities, you should manually pick main interfaces for entities
> and use `@EntityScan` annotation. To create spring data repository, you need to just implement one
> of the repository interfaces

```java

@EntityScan(basePackageClasses = {Permission.class, PersistScenario.class})
@Configuration
public class JpaConfig {

}
```

```java

public interface PermissionRepository extends JpaPermissionRepository {}
```

### Primary Entities

#### Update

- `Update` is the main object that comes from the Telegram API. It contains all information about
  the event that happened in the bot, whether it's a new message from the user, or changes in some
  settings chat in which the bot is located.

> Additional docs - <a href="https://core.telegram.org/bots/api">Telegram API docs</a>

#### UpdateRequest

`UpdateRequest` is a primary object that stores all information about update. Any change
that occurs during the processing of an update is written to it. Thus, if you get it in the user
code, you can find out all the information about the current update. For example, in this way:

```java

@TelegramController
public class Example {

    @TelegramRequest
    public void onAll(UpdateRequest request) {
        System.out.printf("request is %s", request);
    }
}
```

Read more about [Telegram Controllers](#controllers).

#### UpdateHandler

- The `UpdateHandler` interface is the entry point for handling updates. More
  in [Update Handling](#update-handling) section.

#### UpdateFilter

- `UpdateFilters` allow you to execute some code before or after the main `UpdateHandlers` call. The
  main interfaces are `PreUpdateFilter` and `PostUpdateFilter`. More about filters [here](#filters)

#### TelegramResponse

- `TelegramResponse` represents the action that need to be executed to sent response to the user who
  initiated the update processing. For this here special
  mechanism [Response Processing](#response-processing)

#### TelegramScope

- `TelegramScope` is a specialization of `@Scope` for a component whose lifecycle is bound to the
  current telegram update handling. More [here](#telegram-scope)

## Additional Info

- All packages are children of `io.github.drednote.telegram` marked with two annotations
    - `@NonNullApi` and `@NonNullFields`. This means that by default all fields and APIs accept and
      return non-null objects. If it is needed to return a nullable object, then the field or method
      is annotated with `@Nullable`
- You can use `HasRole` annotation to check controller access

## Configuration

All settings tables contain 4 columns:

- `Name` - the name of the variable as it is called in the code
- `Description` - a brief description of what this setting does
- `Default Value` - the default value of the variable
- `Required` - whether the variable is required

> If the `Required` field is `true` and the value of the `Default Value` column is not equal to `-`,
> it means that you don't need to manually set the value for the variable. However, if you manually
> set it to `null` or any value that can be considered empty, the application will not start

### Base properties

| Name          | Description                               | Default Value                                            | Required |
|---------------|-------------------------------------------|----------------------------------------------------------|----------|
| name*         | The name of a bot. Example: TheBestBot.   | <b>must be set by user</b>                               | true     |
| token*        | The token of a bot.                       | <b>must be set by user</b>                               | true     |
| defaultLocale | The default locale for sending responses. | -                                                        | false    |
| session       | Session properties.                       | [Session properties](#session-properties)                |          |
| updateHandler | Properties of update handlers.            | [Update handlers properties](#update-handler-properties) |          |
| filters       | Filters properties.                       | [Filters properties](#filters-properties)                |          |
| menu          | Menu properties.                          | [Menu properties](#menu-properties)                      |          |

### Session properties

| Name                    | Description                                                                                                           | Default Value                                     | Required |
|-------------------------|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------|----------|
| maxUserParallelRequests | Max number of threads used for consumption messages from a telegram for concrete user. 0 - no restrictions            | 1                                                 | true     |
| consumeMaxThreads       | Max number of threads used for consumption messages from a telegram                                                   | 1                                                 | true     |
| updateStrategy          | The strategy to receive updates from Telegram API. Long polling or webhooks.                                          | LONG_POLLING                                      | true     |
| backOffStrategy         | Backoff strategy for failed requests to Telegram API. Impl of BackOff interface must be with public empty constructor | ExponentialBackOff                                | true     |
| proxyType               | The proxy type for executing requests to Telegram API.                                                                | NO_PROXY                                          | true     |
| proxyUrl                | The proxy url in format `host:port` or if auth needed `host:port:username:password`.                                  | -                                                 | false    |
| cacheLiveDuration       | Cache lifetime used in `DefaultTelegramBotSession`                                                                    | 1                                                 | true     |
| cacheLiveDurationUnit   | The `TimeUnit` which will be applied to `cacheLiveDuration`                                                           | hours                                             | true     |
| longPolling             | LongPolling properties.                                                                                               | [LongPolling properties](#Longpolling-properties) | false    |

#### LongPolling properties

| Name           | Description                                                                                          | Default Value | Required |
|----------------|------------------------------------------------------------------------------------------------------|---------------|----------|
| updateLimit    | Limits the number of updates to be retrieved. Values between 1-100 are accepted                      | 100           | true     |
| updateTimeout  | Timeout in seconds for long polling. Should be positive, short polling (0) for testing purposes only | 50            | true     |
| allowedUpdates | A JSON-serialized list of update types to receive. See RequestType for available update types.       | -             | false    |

Additional docs <a href="https://core.telegram.org/bots/api">Telegram API docs</a>

### Update handler properties

| Name                           | Description                                                                                                                          | Default Value | Required |
|--------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|---------------|----------|
| controllerEnabled              | Enabled controller update handling.                                                                                                  | true          | true     |
| scenarioEnabled                | Enabled scenario update handling.                                                                                                    | true          | true     |
| setDefaultErrorAnswer          | If an exception occurs and no handler processes it, set InternalErrorTelegramResponse as response.                                   | true          | true     |
| scenarioLockMs                 | The time that scenario executor will wait if a concurrent interaction was performed. 0 - no limit.                                   | 0             | false    |
| serializeJavaObjectWithJackson | Whether to serialize Java POJO objects with Jackson to JSON in GenericTelegramResponse.                                              | true          | true     |
| enabledWarningForScenario      | Throws an error with a warning about using scenario safe only when getMaxThreadsPerUser is set to 1, if value set to different value | true          | true     |

### Filters properties

| Name                         | Description                                                                                                                    | Default Value                                   | Required |
|------------------------------|--------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|----------|
| permission                   | Permission filter properties.                                                                                                  | [Permission properties](#permission-properties) |          |
| userRateLimit                | How often each user can perform requests to bot. 0 = no rules.                                                                 | 0                                               | true     |
| userRateLimitUnit            | The ChronoUnit which will be applied to userRateLimit.                                                                         | SECONDS                                         | true     |
| userRateLimitCacheExpire     | How long cache with rate limit bucket will not expire. This parameter needed just for delete staled buckets to free up memory. | 1                                               | true     |
| userRateLimitCacheExpireUnit | The ChronoUnit which will be applied to userRateLimitCacheExpire.                                                              | HOURS                                           | true     |
| setDefaultAnswer             | If response is null at the end of update handling and post filtering, set NotHandledTelegramResponse as response.              | true                                            | true     |

### Permission properties

| Name        | Description                                                               | Default Value | Required |
|-------------|---------------------------------------------------------------------------|---------------|----------|
| access      | Define who has access to the bot.                                         | ALL           | true     |
| defaultRole | If a user has no role, this role will be set by default.                  | NONE          | true     |
| roles       | The list of roles with privileges.                                        | -             | false    |
| assignRole  | The map of [userId:[Role](#role)]. (Deprecated: Not safe for production.) | -             | false    |

#### Role

```java
public class Role {

    /**
     * Boolean indicating if the role has basic interaction permission and can send requests to bot
     */
    private boolean canRead;
}
```

### Menu properties

| Name       | Description                                      | Default Value | Required |
|------------|--------------------------------------------------|---------------|----------|
| values     | Map of [name:[CommandCls](#Command-properties)]. | -             | false    |
| sendPolicy | Send policy.                                     | ON_STARTUP    | false    |

#### Command Properties

| Name         | Description                                                                   | Default Value | Required |
|--------------|-------------------------------------------------------------------------------|---------------|----------|
| text         | Text for the button.                                                          | -             | true     |
| command      | Command for the button.                                                       | -             | true     |
| scopes       | Scopes of users for which the commands are relevant.                          | [DEFAULT]     | true     |
| languageCode | A two-letter ISO 639-1 language code.                                         | -             | false    |
| userIds      | Unique identifier of the target users to who apply commands.                  | -             | false    |
| chatIds      | Unique identifier for the target chats or usernames of the target supergroup. | -             | false    |

## Dependencies

### Require

These dependencies will automatically be included in your project

`org.telegram:telegrambots-longpolling`

`org.telegram:telegrambots-client`

`org.springframework.boot:spring-boot-starter-web`

`com.esotericsoftware:kryo`

`com.github.vladimir-bukhtoyarov:bucket4j-core`

`com.github.ben-manes.caffeine:caffeine`

`org.apache.httpcomponents.client5:httpclient5`

### Optional

You can manually add them if you want to configure datasource. For what you should configure
datasource read in [Data Source](#data-source) block

`org.springframework.boot:spring-boot-starter-data-jpa`

or

`org.springframework.boot:spring-boot-starter-data-mongo`

## Contributing

I welcome contributions from the community to improve and extend the Spring Boot Starter Telegram
library. If you would like to contribute, follow these steps:

1. Fork the repository on GitHub by clicking the "Fork" button in the top right corner of the
   repository page.
2. Clone your forked repository to your local development environment:

```shell
git clone https://github.com/your-username/spring-boot-starter-telegram.git
cd spring-boot-starter-telegram
```

3. Create a new branch for your changes:

```shell
# Replace feature/new-feature with a descriptive name for your branch.
git checkout -b feature/new-feature
```

4. Make the necessary changes and additions to the codebase.

5. Test your changes to ensure they work as expected and do not introduce any regressions.

6. Commit your changes:

```shell
git add .
git commit -m "Add your commit message here"
```

**Be sure to write a clear and concise commit message explaining the changes you made.**

7. Push your branch to your forked repository:

```shell
git push origin feature/new-feature
```

8. Open a pull request (PR) on the original repository by navigating to your forked repository on
   GitHub and clicking the "Compare & pull request" button next to your newly pushed branch.

9. Fill out the pull request template with relevant information about your changes, and click the
   "Create pull request" button.

10. I will review your pull request and provide feedback or request further changes if necessary.

11. Once your pull request is approved, it will be merged into the main repository, and your
    contributions will be part of the Spring Boot Starter Telegram library.

Thank you for considering contributing to the project! I appreciate your efforts to make this
library better and more useful for the community. If you have any questions or need assistance with
the contribution process, feel free to ask in the pull request discussion.

**Happy coding!**

## License

This project is licensed under the [MIT License](LICENSE). You are free to use, modify, and
distribute this library according to the terms of the license.

### The MIT License (MIT)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Thanks

Thanks to [OlegNyr](https://github.com/OlegNyr/java-telegram-bot-mvc) for an idea of controllers

## Authors

The Spring Boot Starter Telegram library is maintained and developed by Ivan Galushko (Drednote).

Happy bot development!
