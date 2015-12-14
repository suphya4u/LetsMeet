package com.letsmeet.android.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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

  private final static String[] FROM_COLUMNS = {
      ChatStore.COLUMN_MESSAGE
  };

  private final static int[] TO_IDS = {
      R.id.test_chat
  };

  private long eventId = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);

    try {
      String eventIdString = getIntent().getStringExtra(Constants.EVENT_ID_KEY);
      String eventName = getIntent().getStringExtra(Constants.EVENT_NAME_KEY);
      if (!Strings.isNullOrEmpty(eventIdString)) {
        eventId = Long.parseLong(eventIdString);
      }
      if (!Strings.isNullOrEmpty(eventName)) {
        setTitle(eventName);
      }
    } catch (NumberFormatException e) {
      // Log to analytics.
    }
    if (eventId == 0) {
      Toast.makeText(this, "Could not find event id. Returning.", Toast.LENGTH_LONG).show();
      finish();
    }

    populateChatData(eventId);

    final TextView chatMessageView = (TextView) findViewById(R.id.new_chat_message);

    Button sendChatButton = (Button) findViewById(R.id.send_chat);
    sendChatButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        sendChat(chatMessageView.getText().toString());
      }
    });

    ListView chatListView = (ListView) findViewById(R.id.chat_messages);
    chatListView.setAdapter(new SimpleCursorAdapter(this, R.layout.chat_item, ChatStore.getCursor(this),
        FROM_COLUMNS, TO_IDS, 0 /* flags */));
  }

  private void sendChat(final String message) {
    final ChatMessage chatMessage = new ChatMessage()
        .setEventId(eventId)
        .setMessage(message)
        .setIsMyMessage(true)
        .markPending();

    final long chatId = ChatStore.insert(ChatActivity.this, chatMessage);

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

  private void populateChatData(long eventId) {

  }
}
