# Spring Boot Starter Telegram

[![Build](https://github.com/Drednote/spring-boot-starter-telegram/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Drednote/spring-boot-starter-telegram/actions/workflows/build.yml)
[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.drednote/spring-boot-starter-telegram.svg)](https://search.maven.org/artifact/io.github.drednote/spring-boot-starter-telegram)
[![Codecov](https://codecov.io/gh/Drednote/spring-boot-starter-telegram/graph/badge.svg?token=4GGKDCSXH2)](https://codecov.io/gh/Drednote/spring-boot-starter-telegram)

**Spring Boot Starter Telegram** is a library designed to simplify the setup of Telegram bots using
`Spring Boot` and `org.telegram:telegrambots` as the core dependency. Using this library allows you to easily create your first bot with zero configuration. At the same time, it provides a highly modular architecture, enabling you to customize almost any functionality by creating your own Spring beans. Nearly every class used by this library can be replaced. You are free to implement any custom logic within the library's workflow. To help you navigate the wide range of settings and classes, a detailed description of all key features and classes is provided below, along with usage examples.

## Main Features

1. **Controller Update Handling**: Allows receiving updates from the bot via `TelegramController` similar to
   `RestController` in Spring. This enables seamless integration of Telegram bot functionality with
   the existing Spring ecosystem.

2. **Customizable Scenarios**: Users can define custom scenarios for their bots, which can be
   configured via Java configuration. These scenarios allow the bot to process user interactions in
   a more organized and structured manner.

3. **Flexible Update Filters**: The library provides the ability to set up custom filters for
   individual bot updates. These filters are executed before and after the user-defined code,
   allowing for pre-processing and post-processing of updates.

4. **Centralized Error Handling**: Leveraging annotations in conjunction with `TelegramAdvice` and
   `TelegramExceptionHandler`, the library offers a centralized approach for handling errors,
   similar to `ControllerAdvice` and `ExceptionHandler` in Spring.

## Navigation

- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Usage](#usage)
    - [Summary](#summary)
    - [Details](#details)
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

## Quick Start

Add to `application.yml` your bot token and specify the name of bot

```yaml
drednote:
  telegram:
    token: <Your bot token>
```

Or if you preferred properties instead of yml

```properties
drednote.telegram.token=<Your bot token>
```

> If you want, you can disable the autoconfiguring telegram bot by setting `drednote.telegram.enabled` to false. This
> will prevent the automatic creation of any beans.

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

    @TelegramMessage("My name is {name:.*}")
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

## Usage

This section describes the capabilities of the library for creating Telegram bots. We will start with a general overview of how messages from Telegram are processed and sent back. Below you will find a textual description of the process, as well as a diagram.

### Summary

- Upon application startup, a session is established to connect with the Telegram API, initiating
  the reception of updates. These updates are obtained either through a **long polling strategy** or
  by setting up a **webhook** that prompts Telegram to directly transmit updates to the application.

- Upon receiving an `Update`, a `UpdateRequest` object is
  generated. This object serves as the central entity throughout the further update processing
  pipeline. Initially, it contains only the information extracted from the `Update` object.
  As the processing continued, the `UpdateRequest` accumulates additional data, enriching its content.

- At the very beginning of the update processing chain, the `UpdateRequest` is stored in the context of the current
  thread. This is done to create a `Telegram Scope`.

- After that, the main update processing starts with calls to `Filters`. These filters
  help in determining which updates should be processed further, and at this stage it is determined what kind of request has arrived and how it needs to be processed. You can put any logic in the filter that will be executed for each update, for example, log something.

- Once the updates are filtered, the available `UpdateHandler` are called in a specific order based
  on their priority. There are different mechanisms available for handling updates such as controllers or scenario.

- After the successful processing of updates, the `Filters` are called again as part of
  the post-processing stage. This gives an opportunity for any additional filtering or actions to be
  applied.

- Once all the processing and filtering are done, the response is processed using a specialized
  mechanism called `Response Processing`. This mechanism takes the defined
  response and performs necessary actions, such as sending it back to the user or performing any
  other desired logic.

- After sending a response to Telegram, `Filters` are called again, but now these are a special type of filters - conclusive filters. They are specially designed to process either responses from Telegram, or as the final stage of `Update` processing.

- Throughout the entire processing chain, there is a dedicated mechanism for handling errors called
  `Exception Handling`. This ensures that any errors or exceptions that occur
  during the processing are properly handled and don't disrupt the flow of the program.

- Additionally, some filters and handlers defined by this library require access to a database. For
  this purpose, they can make use of the `Data Source` functionality. This allows them
  to interact with the database and retrieve or store data as needed.

### Details

> All documentation is located in other files, so feel free to follow the links.

Before we move on to the main features, I would like to introduce you to three basic entities that you need to know and understand. I strongly recommend reading about them, as they will be referenced later without further explanation. It will be much easier to understand the documentation if you are familiar with these core classes.

- [Update](docs/update-object.md#update) — is the main object that comes from the Telegram API
- [UpdateRequest](docs/update-object.md#updaterequest) - is a primary object that stores all information about update.
- [TelegramClient](docs/update-object.md#telegramclient) — a client that allows you to send anything to Telegram from your code.

Next, we will take a closer look at each aspect of the library.

- [Controllers](docs/controllers.md) — To get started, I recommend familiarizing yourself with basic interaction with Telegram via controllers.
- [Exception handling](docs/exception-handling.md) — No application is complete without error handling.
- [Filters](docs/filters.md) — This mechanism allows you to flexibly and easily customize the processing of updates from Telegram.
- [Responses](docs/response-processing.md) — In most cases, you won't need to manually configure the response mechanism, but this section provides a detailed explanation of how it works.
- [Database connection](docs/datasource.md) — Mechanisms such as scenarios, in my opinion, require working with a database. Also, the default session for receiving messages from Telegram has one limitation, which can be resolved by connecting a database.
- [Scenarios](docs/scenario.md) — This is a powerful mechanism built on top of the Spring State Machine. It allows you to configure entire chains of message processing rules.
- [Session](docs/session.md) — This section describes how the session for receiving messages from Telegram works, how you can configure it, and how to implement your own if needed.
- [Menu](docs/menu.md) — Creating and displaying a menu in your bot.
- [Telegram scope](docs/telegram-scope.md) — A bean scope specific to the Telegram session.
- [Permissions](docs/permissions.md) — Access settings for your bot.

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
