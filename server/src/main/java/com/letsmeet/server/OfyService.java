package com.letsmeet.server;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.letsmeet.server.data.EventRecord;
import com.letsmeet.server.data.FeedbackRecord;
import com.letsmeet.server.data.Invites;
import com.letsmeet.server.data.UserRecord;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 */
public class OfyService {

  static {
    ObjectifyService.register(UserRecord.class);
    ObjectifyService.register(EventRecord.class);
    ObjectifyService.register(Invites.class);
    ObjectifyService.register(FeedbackRecord.class);
  }

  public static Objectify ofy() {
    return ObjectifyService.ofy();
  }

  public static ObjectifyFactory factory() {
    return ObjectifyService.factory();
  }
}
