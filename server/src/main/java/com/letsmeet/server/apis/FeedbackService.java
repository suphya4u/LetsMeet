package com.letsmeet.server.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.letsmeet.server.apis.messages.SendFeedbackRequest;
import com.letsmeet.server.apis.messages.SendFeedbackResponse;
import com.letsmeet.server.data.FeedbackRecord;

import static com.letsmeet.server.OfyService.ofy;

/**
 * Exposes APIs for feedback.
 */
@Api(name = "feedbackService",
    version = "v1",
    namespace = @ApiNamespace(ownerDomain = "server.letsmeet.com",
        ownerName = "server.letsmeet.com",
        packagePath = ""))
public class FeedbackService {

  @ApiMethod(name = "sendFeedback")
  public SendFeedbackResponse sendFeedback(SendFeedbackRequest request) {
    FeedbackRecord feedback = new FeedbackRecord()
        .setUserId(request.getUserId())
        .setFeedback(request.getFeedback())
        .setAppVersion(request.getAppVersion())
        .setTimestamp(System.currentTimeMillis());

    ofy().save().entity(feedback).now();

    // TODO: Maybe also send an email to some address.
    return new SendFeedbackResponse().setSuccess(true);
  }
}
