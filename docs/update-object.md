## Update

`Update` is the main object that comes from the Telegram API. It contains all information about
the event that happened in the bot, whether it's a new message from the user, or changes in some
settings chat in which the bot is located.

> Additional docs - <a href="https://core.telegram.org/bots/api">Telegram API docs</a>

## UpdateRequest

`UpdateRequest` is a primary object that stores all information about [update](#update). Any change
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

При создании `UpdateRequest` происходит парсинг полученного `Update`. Любое обновление из телеграмма можно писать 3 полями. Это - text (если он есть), requestType и список messageType (если requestType == RequestType.MESSAGE). Возможные значения для requestType вы можете посмотреть в енаме `RequestType`, для messageType в енаме `MessageType`. Если requestType != RequestType.MESSAGE, тогда список MessageType будет пустым. 

> Если по каким – то причинам не удалось понять что за тип сообщения (MessageType) отражает объект `Update`, тогда будет проставлен тип MessageType.UNKNOWN. Но только если requestType == RequestType.MESSAGE.

Информацию по полям класса вы сможете найти в javadoc `UpdateRequest`. Вы можете почти неограниченно взаимодействовать с данным классом. Предполагается что вы сами не будете в него ничего записывать, а только читать. Но если вы захотите что-то вручную изменить в данном классе -  учтите что любое ваше действие может привести к непредвиденным ошибкам или непредсказуемому поведению. 

## TelegramClient

Документация по этому классу вы можете найти в org.telegram:telegrambots. //todo добавить ссылку