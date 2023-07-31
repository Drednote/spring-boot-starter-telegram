package com.github.drednote.telegram.core.request;

public enum RequestType {
  COMMAND,
  MESSAGE,
  INLINE_QUERY,
  CHOSEN_INLINE_QUERY,
  CALLBACK_QUERY,
  SHIPPING_QUERY,
  PRE_CHECKOUT_QUERY,
  POLL,
  POLL_ANSWER,
  CHAT_MEMBER_UPDATED,
  CHAT_JOIN_REQUEST
}
