# Controllers

This library offers users two primary approaches for handling updates: `controllers` and `scenarios`. Both controllers
and scenarios offer their own benefits, so you can choose the one that suits your bot's requirements and your preferred
coding style. В этом разделе речь пойдет о контроллерах, как их использовать и какие возможности они дают.

> If you need to make your own handler, all you have to do is create a **spring bean** that will
> implement the `UpdateHandler` interface and set its execution priority using a spring
> annotations `@Order` if needed. Also, after successful processing of the message, it is necessary
> put in the object `UpdateRequest` response with type `TelegramResponse`, so that update
> processing can be considered successful. If this is not done, further update handlers will be
> called

## Overall information

To begin receiving updates from **Telegram**, you will first need to create a controller and specify the criteria for
which updates you want to receive.

To assist with the analysis, let's use the controller provided in the `Quick Start` section:

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

- In order for an application to register a class as a controller, it must be marked
  annotation `@TelegramController`
- To receive updates, there is an annotation `@TelegramRequest`. Also, to make it easier to work
  with updates, was created several derived annotations. The main ones are `@TelegramCommand`
  and `@TelegramMessage`. As the names imply, with the help of the first one you can only receive
  commands **(Message#isCommand)**, and from the second any messages **(Update#getMessage)**
- The `@TelegramRequest` annotation's **pattern()** parameter takes a string that serves as a
  matching condition for the text in the incoming message, whether it's just a message text or a
  photo caption. For matcher uses `AntPathMatcher`, so you can specify
  any [condition](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html)
  in the string valid for `AntPathMatcher`. For example: `Hello*`, will match any string that starts
  with `Hello`. If you do not specify **pattern()** it will be replaced with the `**` pattern and
  matched with any text.
- If the `update` matches with several patterns that you specified in different methods
  marked with `@TelegramRequest`, than sorted will be applied:
    - `TelegramRequest` without **requestType** has the lowest priority
    - `TelegramRequest` without **messageType** has the lowest priority among the **requestType**
      equal to `RequestType.MESSAGE`
    - `TelegramRequest` with **exclusiveMessageType** set to true and with more elements specified
      in **messageType** takes precedence over others
    - If `TelegramRequest` has the same priority by the above conditions, than the `AntPathMatcher`
      order applied
      > Простыми словами - чем уникальнее контроллер тем больший приоритет он имеет.
- Methods marked with `@TelegramRequest` annotation can accept a specific set of inputs
  parameters as defined in the [Argument resolving](#argument-resolving) section
- Methods marked with `@TelegramRequest` annotation can return any object, as a result. Более детально данный процесс будет разобран в [Responses](#responses)
- Also, if you need to get some data from the user's message by a specific pattern, then you can use
  the [TelegramPatternVariable](#telegrampatternvariable)
  annotation
  **Essentially `TelegramPatternVariable` works the same as `PathVariable`.**

## Argument resolving

Чтобы дать вам максимальное количество возможностей при работе с контроллерами, были определены несколько типов которые
вы можете указать в методе своих контроллеров и библиотека автоматически вытащит их из своего контекста и подставит вам
в метод. Далее приведен список поддерживаемых типов на данный момент:

| Type                | Source                                             |
|---------------------|----------------------------------------------------|
| `UpdateRequest`     | `UpdateRequest`                                    |
| `TelegramClient`    | `UpdateRequest.getTelegramClient()`                |
| `Throwable`         | `UpdateRequest.getError()`                         |
| `String`            | `UpdateRequest.getText()`                          |
| `Long`              | `UpdateRequest.getChatId()`                        |
| `Update`            | `UpdateRequest.getOrigin()`                        |
| `Message`           | `UpdateRequest.getMessage()`                       |
| `User`              | `UpdateRequest.getUser()`                          |
| `Chat`              | `UpdateRequest.getChat()`                          |
| `InlineQuery`       | `UpdateRequest.getOrigin().getInlineQuery()`       |
| `ChosenInlineQuery` | `UpdateRequest.getOrigin().getChosenInlineQuery()` |
| `CallbackQuery`     | `UpdateRequest.getOrigin().getCallbackQuery()`     |
| `ShippingQuery`     | `UpdateRequest.getOrigin().getShippingQuery()`     |
| `PreCheckoutQuery`  | `UpdateRequest.getOrigin().getPreCheckoutQuery()`  |
| `Poll`              | `UpdateRequest.getOrigin().getPoll()`              |
| `PollAnswer`        | `UpdateRequest.getOrigin().getPollAnswer()`        |
| `ChatMemberUpdated` | `UpdateRequest.getOrigin().getChatMember()`        |
| `ChatJoinRequest`   | `UpdateRequest.getOrigin().getChatJoinRequest()`   |

> If you need to add support for your custom argument, you need to create **spring bean** and
> implement `io.github.drednote.telegram.core.resolver.HandlerMethodArgumentResolver` interface

## TelegramPatternVariable

You can annotate certain arguments using `@TelegramPatternVariable`. Here are the rules to follow:

- This annotation is only valid if there's text in the update (e.g., message text, photo caption).
  Otherwise, it will be `null`
- This annotation supports only two Java types: `String` and `Map`
- When using `String`, the value of the template variable from the pattern is exposed
- In the case of `Map`, all template variables are exposed

## Responses

Вы можете возвращать любой тип из методов помеченных аннотацией `TelegramRequest`. Тип будет обернут в `TelegramResponse` и далее обработан. Подробнее о механизме отправки ответов в телеграм будет в разделе Response Processing. Здесь же я хочу показать несколько возможных вариантов ответов.

### Simple

Данный контроллер возвращает строку, которая потом оборачивается в `GenericTelegramResponse` и в таком виде существует до момента вызова отправки ответа в телеграм.

```java

@TelegramController
public class MainController {

    @TelegramCommand("/start")
    public String onStart(User user) {
        return "Hello " + user.getFirstName();
    }
}
```

### List

Этот контроллер обернет ответ в `CompositeTelegramResponse` и поочередно выполнит две отправки ответа. 
> Количество объектов в листе не ограничено.

```java

@TelegramController
public class MainController {

    @TelegramCommand("/start")
    public List<String> onStart() {
        return List.of("1", "2");
    }
}
```

### Void

Вы можете ничего не возвращать и тогда ничего и отправится в телеграм. Но ответ будет обернут в `EmptyTelegramResponse`.

```java

@TelegramController
public class MainController {

    @TelegramCommand("/something")
    public void empty() {
        // do something
    }
}
```

### Flux
Вы можете использовать тип ответа `Flux` для того чтобы создавать коллбеки. Flux очень напоминает Stream API, но это все таки совершенно другое. Об этом вы можете почитать тут https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html. 

```java
@TelegramController
public class CallbackController {

  @TelegramCommand("/callback")
  public Flux<Object> onCallback(UpdateRequest updateRequest) {
    Mono<SendChatAction> typing = Mono.just(SendChatAction.builder()
            .chatId(updateRequest.getChatId())
            .action(ChatActionType.TYPING.getValue())
            .build());

    // create callback that will be executed when a long process is finished
    Mono<String> callback = Mono.defer(() -> {
      try {
        // imitate a long process
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return Mono.just("Hello World");
    });

    return Flux.merge(typing, callback);
  }
}
```
Пример выше при вызове контроллера создает 2 параллельные задачи: поменять статус диалога у пользователя на "печатает" и имитация долгой задачи, после чего пользователю отправляется строка `Hello World`. Этот пример можно переписать на более простой и понятный пример:

```java
@TelegramController
public class CallbackController {

  @TelegramCommand("/callback")
  public List<Object> onCallback(UpdateRequest updateRequest) {
    SendChatAction typing = SendChatAction.builder()
            .chatId(updateRequest.getChatId())
            .action(ChatActionType.TYPING.getValue())
            .build();

    // create callback that will be executed when a long process is finished
    TelegramResponse response = request -> {
      try {
        // imitate a long process
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      new GenericTelegramResponse("Hello World").process(request);
    };

    return List.of(typing, response);
  }
}
```

Тут же ответы будут отправляться строго последовательно, без гибкости которую предоставляет Flux.

### Дополнительная информация

Полная документация по Responses вы сможете найти в разделе посвященном обработке ответов [Response handling](response-processing.md).

## Permissions

Дополнительно для удобства проверки прав пользователей бота, есть аннотация `HasRole`. Ниже приведен пример ее использования:

```java

@TelegramController
public class MainController {

    @TelegramCommand("/admin")
    @HasRole("admin")
    public void empty() {
        // do something
    }
}
```

Для своей работы она использует механизм `Permissions`, который детально рассмотрен в разделе [Permissions](permissions.md). Если у пользователя который отправил запрос в бот, нет необходимой роли, то запрос не дойдет до контроллера, прервется и отправит ошибку пользователю.