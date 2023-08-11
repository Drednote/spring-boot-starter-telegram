# Spring Boot Starter Telegram

[![Build](https://github.com/Drednote/spring-boot-starter-telegram/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Drednote/spring-boot-starter-telegram/actions/workflows/build.yml)
[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

**Spring Boot Starter Telegram** is a library designed to simplify the setup of Telegram bots using
`Spring Boot` and `org.telegram:telegrambots` as the core dependency. It provides several key
features to facilitate the bot development process

## Main Features

1. **Mvc Update Handling**: Allows receiving updates from the bot via `TelegramController` similar
   to
   `RestController` in Spring. This enables seamless integration of Telegram bot functionality with
   the existing Spring ecosystem.

2. **Centralized Error Handling**: Leveraging annotations in conjunction with `TelegramAdvice` and
   `TelegramExceptionHandler`, the library offers a centralized approach for handling errors,
   similar to `ControllerAdvice` and `ExceptionHandler` in Spring.

3. **Customizable Scenarios**: Users can define custom scenarios for their bots, which can be
   configured via Java configuration. These scenarios allow the bot to process user interactions in
   a more organized and structured manner.

4. **Flexible Update Filters**: The library provides the capability to set up custom filters for
   individual bot updates. These filters are executed before and after the user-defined code,
   allowing for pre-processing and post-processing of updates.

## Getting started

- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
    - [QuikStart](#quik-start)
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

The Latest version you can check in **Releases** tab

### Maven

Add the repository for the library in your to your `pom.xml` file to fetch the artifact from:

```xml

<repositories>
  <repository>
    <id>central</id>
    <name>Maven Central</name>
    <url>https://repo.maven.apache.org/maven2</url>
  </repository>
  <!--if you want to download snapshots-->
  <repository>
    <id>snapshots</id>
    <name>Maven Snapshots</name>
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
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
    // if you want to download snapshots
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
}
```

Add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    // Replace with the actual version
    implementation 'io.github.drednote:spring-boot-starter-telegram:yourVersion'
}
```

Make sure to use Java 17 or a later version in your project as the Spring Boot Starter Telegram
library is based on Spring Boot 3 and requires Java 17 or higher.

Please note that the version specified (yourVersion) is just a placeholder. Replace it with the
actual version you want to use for your project.

## Usage

### Quik Start

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
  public String onMessage(TelegramUpdateRequest request) {
    return "You sent message with types %s".formatted(request.getMessageTypes());
  }

  @TelegramRequest
  public TelegramResponse onAll(TelegramUpdateRequest request) {
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

That's all! Enjoy your bot. For further information and bot configuration read below

## Configuration

### Base properties

| Name          | Description                               | Default Value                                            |
|---------------|-------------------------------------------|----------------------------------------------------------|
| name*         | The name of a bot. Example: TheBestBot.   | <b>must be set by user</b>                               |
| token*        | The token of a bot.                       | <b>must be set by user</b>                               |
| defaultLocale | The default locale for sending responses. | -                                                        |
| session       | Session properties.                       | [Session properties](#session-properties)                |
| updateHandler | Properties of update handlers.            | [Update handlers properties](#update-handler-properties) |
| dataSource    | Datasource properties.                    | [Datasource properties](#datasource-properties)          |
| filters       | Filters properties.                       | [Filters properties](#filters-properties)                |
| menu          | Menu properties.                          | [Menu properties](#menu-properties)                      |

### Session properties

| Name            | Description                                                                                                           | Default Value      |
|-----------------|-----------------------------------------------------------------------------------------------------------------------|--------------------|
| updateLimit     | Limits the number of updates to be retrieved. Values between 1-100 are accepted                                       | 100                |
| updateTimeout   | Timeout in seconds for long polling. Should be positive, short polling (0) for testing purposes only                  | 50                 |
| maxThreads      | Max number of threads used for async methods executions                                                               | 1                  |
| allowedUpdates  | A JSON-serialized list of update types to receive. See RequestType for available update types.                        | -                  |
| updateStrategy  | The strategy to receive updates from Telegram API. Long polling or webhooks.                                          | LONG_POLLING       |
| backOffStrategy | Backoff strategy for failed requests to Telegram API. Impl of BackOff interface must be with public empty constructor | ExponentialBackOff |
| proxyType       | The proxy type for executing requests to Telegram API.                                                                | NO_PROXY           |
| proxyHost       | The proxy host.                                                                                                       | -                  |
| proxyPort       | The proxy port.                                                                                                       | 0                  |

Additional docs <a href="https://core.telegram.org/bots/api">Telegram API docs</a>

### Update handler properties

| Name                           | Description                                                                                        | Default Value |
|--------------------------------|----------------------------------------------------------------------------------------------------|---------------|
| mvcEnabled                     | Enabled mvc update handling.                                                                       | true          |
| scenarioEnabled                | Enabled scenario update handling.                                                                  | true          |
| setDefaultErrorAnswer          | If an exception occurs and no handler processes it, set InternalErrorTelegramResponse as response. | true          |
| scenarioLockMs                 | The time that scenario executor will wait if a concurrent interaction was performed. 0 - no limit. | 0             |
| autoConfigureScenarioPersister | Whether to autoconfigure scenarioPersister if none is provided.                                    | true          |
| serializeJavaObjectWithJackson | Whether to serialize Java POJO objects with Jackson to JSON in GenericTelegramResponse.            | true          |

### Datasource properties

| Name                               | Description                                                                                                                                                         | Default Value |
|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------|
| disableAutoGenerateTables          | By default, all tables for JPA datasource are generated with Hibernate (if it exists on classpath). If you don't want to generate them, set this parameter to true. | false         |
| disableDataSourceAutoConfiguration | Disable all datasource configuration. DataSourceAdapter bean will not be created. The application will work as if there is no datasource.                           | false         |

### Filters properties

| Name                | Description                                                                                                       | Default Value                                   |
|---------------------|-------------------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| permission          | Permission filter properties.                                                                                     | [Permission properties](#permission-properties) |
| userConcurrency     | How often each user can perform requests to bot. 0 = no rules.                                                    | 0                                               |
| userConcurrencyUnit | The ChronoUnit which will be applied to userConcurrency.                                                          | SECONDS                                         |
| setDefaultAnswer    | If response is null at the end of update handling and post filtering, set NotHandledTelegramResponse as response. | true                                            |

### Permission properties

| Name        | Description                                                               | Default Value |
|-------------|---------------------------------------------------------------------------|---------------|
| access      | Define who has access to the bot.                                         | ALL           |
| defaultRole | If a user has no role, this role will be set by default.                  | NONE          |
| roles       | The list of roles with privileges.                                        | -             |
| assignRole  | The map of [userId:[Role](#role)]. (Deprecated: Not safe for production.) | -             |

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

| Name       | Description                              | Default Value |
|------------|------------------------------------------|---------------|
| values     | Map of [name:[CommandCls](#CommandCls)]. | -             |
| sendPolicy | Send policy.                             | ON_STARTUP    |

#### CommandCls

```java
public class CommandCls {

  /**
   * Text for the button. Example: Registration
   */
  private String text;

  /**
   * Command for the button. Example: /register
   */
  private String command;
}
```

## Dependencies

### Require

These dependencies will automatically be included in your project

`org.telegram:telegrambots`

`org.springframework.boot:spring-boot-starter-web`

`com.esotericsoftware:kryo`

### Optional

You can manually add them if you want to configure datasource. For what you should configure
datasource read in [Configuration](#configuration) block

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

9. Fill out the pull request template with relevant information about your changes, and click the "
   Create pull request" button.

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

Thanks to [OlegNyr](https://github.com/OlegNyr/java-telegram-bot-mvc) for an idea of mvc controllers

## Authors

The Spring Boot Starter Telegram library is maintained and developed by Galushko Ivan (Drednote).

Happy bot development!
