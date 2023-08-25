package io.github.drednote.telegram.core.request;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * The {@code RequestType} enum represents the type of {@link Update}.
 *
 * @author Galushko Ivan
 * @apiNote Only one type can be present for any given update.
 * @see Update
 */
public enum RequestType {
  /**
   * Represents a message of update
   *
   * @see Update#getMessage()
   */
  MESSAGE,
  /**
   * Represents an inline query of update
   *
   * @see Update#getInlineQuery()
   */
  INLINE_QUERY,
  /**
   * Represents a chosen inline query update
   *
   * @see Update#getChosenInlineQuery()
   */
  CHOSEN_INLINE_QUERY,
  /**
   * Represents a callback query update
   *
   * @see Update#getCallbackQuery()
   */
  CALLBACK_QUERY,
  /**
   * Represents a shipping query update
   *
   * @see Update#getShippingQuery()
   */
  SHIPPING_QUERY,
  /**
   * Represents a pre-checkout query update
   *
   * @see Update#getPreCheckoutQuery()
   */
  PRE_CHECKOUT_QUERY,
  /**
   * Represents a poll update
   *
   * @see Update#getPoll()
   */
  POLL,
  /**
   * Represents a poll answer update
   *
   * @see Update#getPollAnswer()
   */
  POLL_ANSWER,
  /**
   * Represents a chat member updated update
   *
   * @see Update#getChatMember()
   */
  CHAT_MEMBER_UPDATED,
  /**
   * Represents a chat join request update
   *
   * @see Update#getChatJoinRequest()
   */
  CHAT_JOIN_REQUEST
}
