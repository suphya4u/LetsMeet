package com.letsmeet.android.apiclient;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.letsmeet.android.config.Config;
import com.letsmeet.server.feedbackService.FeedbackService;
import com.letsmeet.server.feedbackService.model.SendFeedbackRequest;
import com.letsmeet.server.feedbackService.model.SendFeedbackResponse;

import java.io.IOException;

/**
 * Api client for feedback service.
 */
public class FeedbackServiceClient {

  private static FeedbackServiceClient selfInstance;
  private static FeedbackService feedbackService;

  private FeedbackServiceClient() {}

  public static FeedbackServiceClient getInstance() {
    if (selfInstance == null) {
      selfInstance = new FeedbackServiceClient();
    }
    return selfInstance;
  }

  public SendFeedbackResponse sendFeedback(long userId, String feedback, String appVersionId)
      throws IOException {
    SendFeedbackRequest request = new SendFeedbackRequest()
        .setUserId(userId)
        .setFeedback(feedback)
        .setAppVersion(appVersionId);
    return getService().sendFeedback(request).execute();
  }

  private FeedbackService getService() {
    if (feedbackService != null) {
      return feedbackService;
    }
    // TODO(suhas): This is common code for all services. Move it to a common place.
    FeedbackService.Builder serviceBuilder = new FeedbackService.Builder(
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
    feedbackService = serviceBuilder.build();
    return feedbackService;
  }
}
