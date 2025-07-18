## Exception handling

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
- Methods marked with `@TelegramExceptionHandler` annotation can return any object, as a result. The
  response processing mechanism is detailed in the [Response Processing](#response-processing)
  section