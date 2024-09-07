package io.github.drednote.telegram.menu;

import io.github.drednote.telegram.menu.DefaultBotMenu.CommandKey;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllGroupChats;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllPrivateChats;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

@Configuration
@ConfigurationProperties("drednote.telegram.menu")
@Getter
@Setter
public class MenuProperties {

    private static final Pattern PATTERN = Pattern.compile("[^a-z\\s/]");

    /**
     * Create bean {@link BotMenu} with this commands
     */
    @Nullable
    private Map<String, CommandCls> values;
    /**
     * Send policy
     */
    private SendPolicy sendPolicy = SendPolicy.ON_STARTUP;

    @Getter
    @Setter
    @ToString
    public static class CommandCls {

        /**
         * Text for the button. Example: Registration
         */
        private String text;

        /**
         * Command for the button. Example: /register
         */
        private String command;
        /**
         * Field describing the scope of users for which the commands are relevant. Defaults to
         * {@link ScopeCommand#DEFAULT}.
         *
         * @see ScopeCommand
         */
        private Set<ScopeCommand> scopes = Set.of(ScopeCommand.DEFAULT);
        /**
         * A two-letter ISO 639-1 language code. If empty, commands will be applied to all users from the given scope,
         * for whose language there are no dedicated commands
         * <p>
         * Example: ru, en
         */
        @Nullable
        private String languageCode;
        /**
         * Unique identifier of the target user to who apply commands. Only applicable if {@link #scopes} equals to
         * {@link ScopeCommand#CHAT_MEMBER}
         */
        private Set<Long> userIds = Set.of();
        /**
         * Unique identifier for the target chat or username of the target supergroup (in the format
         * {@code @supergroupusername)}
         * <p>
         * Only applicable if {@link #scopes} equals to {@link ScopeCommand#CHAT_MEMBER},
         * {@link ScopeCommand#CHAT_ADMINISTRATORS} or {@link ScopeCommand#CHAT}
         */
        private Set<Long> chatIds = Set.of();

        public void setCommand(@Nullable String command) {
            if (command != null && !command.isBlank()) {
                this.command = (command.startsWith("/") ? "" : "/") + command;
            }
        }

        void validate() {
            if (StringUtils.isBlank(command) || StringUtils.isBlank(text)) {
                throw new IllegalArgumentException(
                    "Validation failed for field 'command' or 'text'. These fields must not be empty. %s"
                        .formatted(this));
            }

            // Valid only lower case and '/' symbol
            if (PATTERN.matcher(command).find() || command.lastIndexOf('/') > 0) {
                throw new IllegalArgumentException(
                    "Bot command must contain only lower case letters and '/' symbol as first symbol");
            }
            if (scopes.contains(ScopeCommand.CHAT_MEMBER) && isInvalidSet(userIds)) {
                throw new IllegalArgumentException(
                    "For bot command with scope CHAT_MEMBER must be specified userId parameter");
            }
            if ((scopes.contains(ScopeCommand.CHAT_MEMBER) || scopes.contains(ScopeCommand.CHAT_ADMINISTRATORS)
                 || scopes.contains(ScopeCommand.CHAT)) && isInvalidSet(chatIds)) {
                throw new IllegalArgumentException(
                    "For bot command with scope CHAT_MEMBER, CHAT_ADMINISTRATORS "
                    + "or CHAT must be specified chatId parameter");
            }
        }

        boolean isInvalidSet(@Nullable Set<Long> longs) {
            if (longs == null || longs.isEmpty()) {
                return true;
            }
            for (Long chatId : longs) {
                if (chatId == null || chatId == 0L) {
                    return true;
                }
            }
            return false;
        }
    }

    public enum SendPolicy {
        /**
         * No auto sending menu to Telegram API
         */
        NONE,
        /**
         * Auto send menu to Telegram API on application start up
         */
        ON_STARTUP
    }

    /**
     * Represents a scope of command
     *
     * @see BotCommandScope
     */
    public enum ScopeCommand {
        /**
         * @see BotCommandScopeDefault
         */
        DEFAULT(it -> new BotCommandScopeDefault(), false, false),
        /**
         * @see BotCommandScopeChatMember
         */
        CHAT_MEMBER(it -> {
            BotCommandScopeChatMember scope = new BotCommandScopeChatMember();
            scope.setChatId(it.chatId());
            scope.setUserId(it.userId());
            return scope;
        }, true, true),
        /**
         * @see BotCommandScopeChatAdministrators
         */
        CHAT_ADMINISTRATORS(it -> {
            BotCommandScopeChatAdministrators scope = new BotCommandScopeChatAdministrators();
            scope.setChatId(it.chatId());
            return scope;
        }, true, false),
        /**
         * @see BotCommandScopeChat
         */
        CHAT(it -> {
            BotCommandScopeChat scope = new BotCommandScopeChat();
            scope.setChatId(it.chatId());
            return scope;
        }, true, false),
        /**
         * @see BotCommandScopeAllPrivateChats
         */
        ALL_PRIVATE_CHATS(it -> new BotCommandScopeAllPrivateChats(), false, false),
        /**
         * @see BotCommandScopeAllGroupChats
         */
        ALL_GROUP_CHATS(it -> new BotCommandScopeAllGroupChats(), false, false),
        /**
         * @see BotCommandScopeAllChatAdministrators
         */
        ALL_CHAT_ADMINISTRATORS(it -> new BotCommandScopeAllChatAdministrators(), false, false);

        private final Function<CommandKey, BotCommandScope> scopeFun;
        private final boolean chatId;
        private final boolean userId;

        ScopeCommand(Function<CommandKey, BotCommandScope> scope, boolean chatId, boolean userId) {
            this.scopeFun = scope;
            this.chatId = chatId;
            this.userId = userId;
        }

        public BotCommandScope getBotScope(CommandKey commandCls) {
            return scopeFun.apply(commandCls);
        }

        public boolean isChatId() {
            return chatId;
        }

        public boolean isUserId() {
            return userId;
        }
    }
}
