## Telegram scope

`TelegramScope` is a specialization of `@Scope` for a component whose lifecycle is bound to the
current telegram update handling.

The functionality of `@TelegramScope` is similar to the Spring annotation `@Scope("request")`, with
the difference being that the context is created at the start of update processing instead of at the
request level. By marking a **spring bean** with this annotation, a new instance of the bean will be
created for each update processing.

It's important to note that each update handling is associated with a specific thread. In cases
where you create sub-threads within the main thread, you will need to manually bind
the `UpdateRequest` to the new thread. This can be achieved using the `UpdateRequestContext` class. Так же вы можете установить настройку `UpdateRequestContext.setInheritable(boolean value)` в значение true, чтобы при создании дочерних потоков контекст автоматически прокидывался. Но у данного использования есть некоторые ограничения о которых вы можете дополнительно прочитать в классе `InheritableThreadLocal`.