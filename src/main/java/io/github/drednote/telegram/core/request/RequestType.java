package io.github.drednote.telegram.core.request;

import org.telegram.telegrambots.meta.api.objects.Update;

public enum RequestType {
  /**
   * @see Update#getMessage()
   */
  MESSAGE,
  /**
   * @see Update#getInlineQuery()
   */
  INLINE_QUERY,
  /**
   * @see Update#getChosenInlineQuery()
   */
  CHOSEN_INLINE_QUERY,
  /**
   * @see Update#getCallbackQuery()
   */
  CALLBACK_QUERY,
  /**
   * @see Update#getShippingQuery()
   */
  SHIPPING_QUERY,
  /**
   * @see Update#getPreCheckoutQuery()
   */
  PRE_CHECKOUT_QUERY,
  /**
   * @see Update#getPoll()
   */
  POLL,
  /**
   * @see Update#getPollAnswer()
   */
  POLL_ANSWER,
  /**
   * @see Update#getChatMember()
   */
  CHAT_MEMBER_UPDATED,
  /**
   * @see Update#getChatJoinRequest()
   */
  CHAT_JOIN_REQUEST
}
