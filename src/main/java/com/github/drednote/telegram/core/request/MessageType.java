package com.github.drednote.telegram.core.request;

import org.telegram.telegrambots.meta.api.objects.Message;

public enum MessageType {
  /**
   * @see Message#getText()
   */
  TEXT,
  /**
   * @apiNote note that the command is not a text
   * @see Message#isCommand()
   */
  COMMAND,
  /**
   * @see Message#getAudio()
   */
  AUDIO,
  /**
   * @see Message#getDocument()
   */
  DOCUMENT,
  /**
   * @see Message#getPhoto()
   */
  PHOTO,
  /**
   * @see Message#getSticker()
   */
  STICKER,
  /**
   * @see Message#getVideo()
   */
  VIDEO,
  /**
   * @see Message#getContact()
   */
  CONTACT,
  /**
   * @see Message#getLocation()
   */
  LOCATION,
  /**
   * @see Message#getVenue()
   */
  VENUE,
  /**
   * @see Message#getAnimation()
   */
  ANIMATION,
  /**
   * @see Message#getPinnedMessage()
   * @see Message#getNewChatMembers()
   * @see Message#getLeftChatMember()
   * @see Message#getNewChatTitle()
   * @see Message#getNewChatPhoto()
   * @see Message#getDeleteChatPhoto()
   * @see Message#getGroupchatCreated()
   */
  CHAT_CHANGES,
  /**
   * @see Message#getReplyToMessage()
   */
  REPLY_TO_MESSAGE,
  /**
   * @see Message#getVoice()
   */
  VOICE,
  /**
   * @see Message#getGame()
   */
  GAME,
  /**
   * @see Message#getSuccessfulPayment()
   */
  SUCCESSFUL_PAYMENT,
  /**
   * @see Message#getVideoNote()
   */
  VIDEO_NOTE,
  /**
   * @see Message#getPoll()
   */
  POLL,
  /**
   * @see Message#getDice()
   */
  DICE,
  /**
   * @see Message#getProximityAlertTriggered()
   * @see Message#getMessageAutoDeleteTimerChanged()
   * @see Message#getWebAppData()
   * @see Message#getVideoChatStarted()
   * @see Message#getVideoChatEnded()
   * @see Message#getVideoChatParticipantsInvited()
   * @see Message#getVideoChatScheduled()
   * @see Message#getForumTopicCreated()
   * @see Message#getForumTopicClosed()
   * @see Message#getForumTopicReopened()
   * @see Message#getForumTopicEdited()
   * @see Message#getGeneralForumTopicHidden()
   * @see Message#getGeneralForumTopicUnhidden()
   * @see Message#getWriteAccessAllowed()
   * @see Message#getUserShared()
   * @see Message#getChatShared()
   */
  SERVICE_MESSAGE
}
