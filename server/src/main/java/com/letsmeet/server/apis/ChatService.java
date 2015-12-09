package com.letsmeet.server.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.common.collect.Lists;
import com.letsmeet.server.apis.messages.SendChatMessageRequest;
import com.letsmeet.server.apis.messages.SendChatMessageResponse;
import com.letsmeet.server.data.Invites;
import com.letsmeet.server.data.UserRecord;
import com.letsmeet.server.notifications.GcmNotifier;

import java.util.List;
import java.util.logging.Logger;

import static com.letsmeet.server.OfyService.ofy;

/**
 * Exposes APIs related to chat.
 */
@Api(name = "chatService",
    version = "v1",
    clientIds = {Constants.ANDROID_CLIENT_ID, Constants.WEB_CLIENT_ID},
    audiences = {Constants.WEB_CLIENT_ID},
    namespace = @ApiNamespace(ownerDomain = "server.letsmeet.com",
        ownerName = "server.letsmeet.com",
        packagePath = ""))
public class ChatService {

  private static final Logger log = Logger.getLogger(EventService.class.getName());

  @ApiMethod(name = "sendChatMessage")
  public SendChatMessageResponse sendChatMessage(SendChatMessageRequest request) {
    List<Invites> invitesList = ofy().load().type(Invites.class)
        .filter("eventId", request.getEventId()).list();
    List<UserRecord> userRecords = Lists.newArrayList();

    String phoneNumber = "";
    for (Invites invite : invitesList) {
      if (invite.getUserId() == 0) {
        continue;
      }
      UserRecord userRecord = ofy().load().type(UserRecord.class).id(invite.getUserId()).now();
      if (invite.getUserId() == request.getUserId()) {
        // Sender of message.
        phoneNumber = userRecord.getPhoneNumber();
      } else {
        userRecords.add(userRecord);
      }
    }

    GcmNotifier.getInstance().notifyNewChat(userRecords, request.getMessage(), phoneNumber,
        request.getEventId());

    return new SendChatMessageResponse().setSuccess(true);
  }
}
