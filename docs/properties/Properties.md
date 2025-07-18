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
## TelegramProperties

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| enabled | boolean |  Enable the bot. Default: true | true | true |
| name | String |  The name of a bot. Example: TheBestBot.   <p>   @deprecated not used since 0.3.0 | - | false |
| token | String |  The token of a bot.   <p>   <b>Required</b> | - | true |
| default-locale | String |  The default locale with which bot will send responses to user chats. A two-letter ISO 639-1   language code   <p>   Example: en, fr, ru. | - | false |
| session |  [SessionProperties](#sessionproperties) |  Session properties | SessionProperties | true |
| update-handler |  [UpdateHandlerProperties](#updatehandlerproperties) |  Properties of update handlers | UpdateHandlerProperties | true |
| filters |  [FilterProperties](#filterproperties) |  Filters properties | FilterProperties | true |
| menu |  [MenuProperties](#menuproperties) |  Menu properties | MenuProperties | true |
| scenario |  [ScenarioProperties](#scenarioproperties) |  Scenario properties | ScenarioProperties | true |
---

## FilterProperties

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| permission |  [PermissionProperties](#permissionproperties) |  Permission filter properties | PermissionProperties | true |
| user-rate-limit | long |  How often each user can perform requests to bot. 0 = no rules | 0 | true |
| user-rate-limit-unit | ChronoUnit |  The {@link ChronoUnit} which will be applied to {@link #userRateLimit} | Seconds | true |
| user-rate-limit-cache-expire | long |  How long cache with rate limit bucket will not expire. This parameter needed just for delete   staled buckets to free up memory | 1 | true |
| user-rate-limit-cache-expire-unit | ChronoUnit |  The {@link ChronoUnit} which will be applied to {@link #userRateLimitCacheExpire} | Hours | true |
| set-default-answer | boolean |  If at the end of update handling and post filtering, the response is null, set   {@link NotHandledTelegramResponse} as response | true | true |
---

## PermissionProperties

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| access | Access |  Define who has access to bot | ALL | true |
| default-role | String |  If a user has no role, this role will be set by default | NONE | true |
| roles | {String : [Role](#role)} |  The list of roles with privileges | {} | true |
| assign-role | Map |  The map of [userId:role] | {} | true |
### Role

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| can-read | boolean |  | false | false |
| permissions | {String : [Object](#object)} |  | {} | false |


---

## ScenarioProperties

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| values | {String : [Scenario](#scenario)} |  A map of scenario names to their corresponding {@link Scenario} objects. | {} | true |
| default-rollback |  [Rollback](#rollback) |  The default rollback configuration, which applies if no another set in scenario object. | - | false |
### Scenario

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| request |  [Request](#request) |  | - | false |
| action-references | [[String](#string)] |  | - | false |
| type | TransitionType |  | EXTERNAL | false |
| source | String |  | - | false |
| target | String |  | - | false |
| graph | [[Node](#node)] |  | [] | false |
| rollback |  [Rollback](#rollback) |  | - | false |
| props | {String : [Object](#object)} |  | {} | false |
| steps | {String : [Scenario](#scenario)} |  | {} | false |


### Rollback

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| request |  [Request](#request) |  | - | false |
| action-references | [[String](#string)] |  | - | false |


### Node

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| id | String |  | - | false |
| children | [[Node](#node)] |  | [] | false |


### Request

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| patterns | [[String](#string)] |  | - | false |
| request-types | [[RequestType](#requesttype)] |  | - | false |
| message-types | [[MessageType](#messagetype)] |  | [] | false |
| exclusive-message-type | boolean |  | false | false |


---

## UpdateHandlerProperties

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| controller-enabled | boolean |  Enabled controller update handling | true | true |
| scenario-enabled | boolean |  Enabled scenario update handling | true | true |
| set-default-error-answer | boolean |  If exception is occurred and no handler has processed it, set {@link InternalErrorTelegramResponse} as response | true | true |
| serialize-java-object-with-jackson | boolean |  By default, java pojo objects will be serialized with Jackson to json in {@link GenericTelegramResponse}. Set     this parameter to false, if you want to disable this behavior | true | true |
| parse-mode | ParseMode |  Default parse mode of a text message sent to telegram. Applies only if you return raw string from update     processing ({@link UpdateHandler}) | NO | true |
| enabled-warning-for-scenario | boolean |  If scenario is enabled and {@link SessionProperties#getMaxThreadsPerUser} is set value other than 1, throws an     error with a warning about using scenario safe only when getMaxThreadsPerUser is set to 1. | true | true |
---

## MenuProperties

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| values | {String : [CommandCls](#commandcls)} |  Create bean {@link BotMenu} with this commands | - | false |
| send-policy | SendPolicy |  Send policy | ON_STARTUP | true |
### CommandCls

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| text | String |  | - | true |
| command | String |  | - | true |
| scopes | [[ScopeCommand](#scopecommand)] |  | [DEFAULT] | true |
| language-code | String |  | - | false |
| user-ids | [[Long](#long)] |  | [] | true |
| chat-ids | [[Long](#long)] |  | [] | true |


### ScopeCommand

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|


---

## SchedulerTelegramUpdateProcessorProperties

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| auto-session-start | boolean |  Automatically start {@link SchedulerTelegramUpdateProcessor} {@link TelegramBotSession#start()} is called. If you     set this parameter to false, you will be needed to manually call the     {@link SchedulerTelegramUpdateProcessor#start()} to start the session and start to process messages from the     Telegram. | true | true |
| max-interval | int |  Maximum interval after which to check for new messages for processing. In milliseconds. | 1000 | true |
| min-interval | int |  Minimum interval after which to check for new messages for processing. In milliseconds. | 0 | true |
| reducing-interval-amount | int |  How much to decrease the interval if messages are found for processing. In milliseconds. | 500 | true |
| increasing-interval-amount | int |  How much to increase the interval if no message is found for processing. In milliseconds. | 100 | true |
| wait-interval | int |  Interval after which to check for new messages for processing while all threads are busy. In milliseconds. | 30 | true |
| idle-interval | int |  Interval after tasks is marked {@link UpdateInboxStatus#TIMEOUT}. In milliseconds. | 30000 | true |
| check-idle-interval | int |  Interval to check that tasks is idle. In milliseconds. | 5000 | true |
| max-message-in-queue-per-user | int |  Limits the number of updates to be store in memory queue for update processing for concrete user. 0 - no     restrictions.     <p>     Applied only for {@link UpdateProcessorType#SCHEDULER} | 0 | true |
---

## LongPollingSessionProperties

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| update-limit | int |  | 100 | true |
| update-timeout | int |  | 50 | true |
| allowed-updates | [[String](#string)] |  | - | false |
---

## SessionProperties

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| long-polling |  [LongPollingSessionProperties](#longpollingsessionproperties) |  LongPolling properties | LongPollingSessionProperties | true |
| scheduler-processor |  [SchedulerTelegramUpdateProcessorProperties](#schedulertelegramupdateprocessorproperties) |  SchedulerTelegramUpdateProcessor properties. | SchedulerTelegramUpdateProcessorProperties | false |
| consume-max-threads | int |  Max number of threads used for consumption messages from a telegram | 10 | true |
| max-messages-in-queue | int |  Limits the number of updates to be store in memory queue for update processing. 0 - no limit. Defaults to     (consumeMaxThreads 1.5). | 15 | true |
| max-threads-per-user | int |  Max number of threads used for consumption messages from a telegram for concrete user. 0 - no restrictions. | 1 | true |
| cache-live-duration | int |  Cache lifetime used in {@link OnFlyTelegramUpdateProcessor}. This parameter needed just to delete staled buckets     to free up memory | 1 | true |
| cache-live-duration-unit | TimeUnit |  The {@link TimeUnit} which will be applied to {@link #cacheLiveDuration} | HOURS | true |
| update-strategy | UpdateStrategy |  The strategy to receive updates from Telegram API | LONG_POLLING | true |
| update-processor-type | UpdateProcessorType |  A type of {@link TelegramUpdateProcessor} using. | DEFAULT | false |
| back-off-strategy | Class |  Backoff strategy which will be applied if requests to telegram API are failed with errors | class org.telegram.telegrambots.longpolling.util.ExponentialBackOff | true |
| proxy-type | ProxyType |  The proxy type for executing requests to telegram API | NO_PROXY | true |
| auto-session-start | boolean |  Automatically start session when spring context loaded. If you set this parameter to false, you will be needed to     manually call the {@link TelegramBotSession#start()} to start the session and start to consume messages from the     Telegram. | true | false |
| proxy-url |  [ProxyUrl](#proxyurl) |  Proxy url in format host:port or if auth needed host:port:username:password. | - | false |
### ProxyUrl

| Name | Type | Description | Default Value | Required |
|------|------|-------------|---------------|----------|
| user-name | String |  | - | false |
| password |  [char[]](#char[]) |  | - | false |


---

