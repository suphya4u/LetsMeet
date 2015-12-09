package com.letsmeet.android.apiclient;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.letsmeet.android.config.Config;
import com.letsmeet.server.chatService.ChatService;
import com.letsmeet.server.chatService.model.SendChatMessageRequest;
import com.letsmeet.server.chatService.model.SendChatMessageResponse;

import java.io.IOException;

/**
 * Client for chat APIs.
 */
public class ChatServiceClient {

  private static ChatServiceClient selfInstance;
  private static ChatService chatService;

  private final Context context;

  private ChatServiceClient(Context context) {
    this.context = context;
  }

  public static ChatServiceClient getInstance(Context context) {
    if (selfInstance == null) {
      selfInstance = new ChatServiceClient(context);
    }
    return selfInstance;
  }

  public SendChatMessageResponse sendChat(SendChatMessageRequest request) throws IOException {
    ChatService service = getService();
    return service.sendChatMessage(request).execute();
  }

  private ChatService getService() {
    if (chatService != null) {
      return chatService;
    }
    // TODO(suhas): This is common code for all services. Move it to a common place.
    ChatService.Builder serviceBuilder = new ChatService.Builder(
        AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
        .setRootUrl(Config.getServerUrl());

    if (Config.isEmulator()) {
      serviceBuilder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
        @Override
        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
            throws IOException {
          abstractGoogleClientRequest.setDisableGZipContent(true);
        }
      });
    }
    chatService = serviceBuilder.build();
    return chatService;
  }
}
