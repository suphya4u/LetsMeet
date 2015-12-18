package com.letsmeet.android.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.letsmeet.android.R;
import com.letsmeet.android.activity.adapter.ChatListAdapter;
import com.letsmeet.android.apiclient.ChatServiceClient;
import com.letsmeet.android.config.Constants;
import com.letsmeet.android.storage.LocalStore;
import com.letsmeet.android.storage.chat.ChatStore;
import com.letsmeet.android.storage.chat.ChatMessage;
import com.letsmeet.server.chatService.model.SendChatMessageRequest;
import com.letsmeet.server.chatService.model.SendChatMessageResponse;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity {

  private BroadcastReceiver newChatMessageReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      onNewChatMessage(intent);
    }
  };

  private long eventId = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    String eventName = getIntent().getStringExtra(Constants.INTENT_EVENT_NAME_KEY);
    if (!Strings.isNullOrEmpty(eventName)) {
      setTitle(eventName);
    }

    eventId = extractEventIdFromIntent(getIntent());
    if (eventId == 0) {
      Toast.makeText(this, "Could not find event id. Returning.", Toast.LENGTH_LONG).show();
      finish();
    }

    final TextView chatMessageView = (TextView) findViewById(R.id.new_chat_message);

    Button sendChatButton = (Button) findViewById(R.id.send_chat);
    sendChatButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        sendChat(chatMessageView.getText().toString());
      }
    });

    ListView chatListView = (ListView) findViewById(R.id.chat_messages);
    chatListView.setAdapter(new ChatListAdapter(this, eventId));

    ChatStore.markAllAsRead(this, eventId);
  }

  private long extractEventIdFromIntent(Intent intent) {
    try {
      String eventIdString = getIntent().getStringExtra(Constants.INTENT_EVENT_ID_KEY);
      if (!Strings.isNullOrEmpty(eventIdString)) {
        return Long.parseLong(eventIdString);
      }
    } catch (NumberFormatException e) {
      // Log to analytics.
    }
    return 0;
  }

  @Override protected void onStart() {
    super.onStart();
    IntentFilter filter = new IntentFilter();
    filter.addAction(Constants.NEW_CHAT_MESSAGE_BROADCAST);
    registerReceiver(newChatMessageReceiver, filter);
  }

  @Override protected void onStop() {
    try {
      unregisterReceiver(newChatMessageReceiver);
    } catch (IllegalArgumentException e) {
      // Ignore. Thrown when receiver is already unregistered.
    }
    super.onStop();
  }

  private void sendChat(final String message) {
    final ChatMessage chatMessage = new ChatMessage()
        .setEventId(eventId)
        .setMessage(message)
        .setIsMyMessage(true)
        .markAsRead()
        .markPending();

    final long chatId = ChatStore.insert(ChatActivity.this, chatMessage);
    updateAdapter();

    new AsyncTask<Void, Void, SendChatMessageResponse>() {

      @Override protected SendChatMessageResponse doInBackground(Void... params) {
        SendChatMessageRequest request = new SendChatMessageRequest();
        request.setEventId(eventId);
        request.setUserId(LocalStore.getInstance(ChatActivity.this).getUserId());
        request.setMessage(message);
        try {
          return ChatServiceClient.getInstance(ChatActivity.this).sendChat(request);
        } catch (IOException e) {
          // TODO: Log to analytics.yg
        }
        return null;
      }

      @Override protected void onPostExecute(SendChatMessageResponse response) {
        super.onPostExecute(response);
        if (response == null) {
          Toast.makeText(ChatActivity.this,
              "Failed to connect server. Please check your network connection",
              Toast.LENGTH_SHORT).show();
          chatMessage.markFailed();
        } else {
          chatMessage.markComplete();
          chatMessage.setTimeSent(response.getTimestamp());
        }
        ChatStore.update(ChatActivity.this, chatId, chatMessage);
        final TextView chatMessageView = (TextView) findViewById(R.id.new_chat_message);
        chatMessageView.setText("");
      }
    }.execute();
  }

  private void updateAdapter() {
    ListView chatListView = (ListView) findViewById(R.id.chat_messages);
    if (chatListView != null) {
      ((ChatListAdapter) chatListView.getAdapter()).updateData();
    }
  }

  private void onNewChatMessage(Intent intent) {
    long newChatEventId = extractEventIdFromIntent(intent);
    if (newChatEventId == eventId) {
      updateAdapter();
      NotificationManager notificationManager =
          (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      // TODO: Do not cancel all. Instead use ordered broadcast described in GsmMessageHandler.
      notificationManager.cancelAll();
    }
  }
}
