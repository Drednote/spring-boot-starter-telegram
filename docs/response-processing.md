## Response processing

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

> The exception is three types of response - `Collection<?>`, `Stream<?>`, `Flux<?>`. For handling
> these types of response are created three additional implementations
> of `TelegramResponse` - `CompositeTelegramResponse`, `FluxTelegramResponse`
> and `StreamTelegramResponse`

- You can create any implementation of `TelegramResponse` for sending response
- Any custom code can be written in `TelegramResponse`, but I strongly recommend using this
  interface only for sending a response to **Telegram**
