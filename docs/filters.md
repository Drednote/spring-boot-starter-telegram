## Filters


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
    - `ConclusivePostUpdateFilter` - **spring beans** that implement this interface will be called **after**
      the response is sent to telegram. [see](#response-processing)

- Also, for convenience, one interface are created. First one - `PriorityPreUpdateFilter` is
  implemented from `PreUpdateFilter` and take precedence over `PreUpdateFilter` and is executed
  earlier whatever returns **getPreOrder()**.

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
