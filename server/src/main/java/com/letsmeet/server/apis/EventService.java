package com.letsmeet.server.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.GeoPt;
import com.google.common.collect.Lists;
import com.letsmeet.server.apis.messages.CreateEventRequest;
import com.letsmeet.server.apis.messages.CreateEventResponse;
import com.letsmeet.server.data.EventRecord;
import com.letsmeet.server.data.Invites;

import java.util.List;
import java.util.logging.Logger;

import static com.letsmeet.server.OfyService.ofy;

/**
 * Exposes APIs related to events.
 */
@Api(name = "eventService",
    version = "v1",
    namespace = @ApiNamespace(ownerDomain = "server.letsmeet.com",
        ownerName = "server.letsmeet.com",
        packagePath = ""))
public class EventService {

  private static final Logger log = Logger.getLogger(EventService.class.getName());

  @ApiMethod(name = "register")
  public CreateEventResponse createEvent(CreateEventRequest request) {
    EventRecord event = new EventRecord()
        .setName(request.getName())
        .setLocation(new GeoPt(request.getLatitude(), request.getLongitude()))
        .setNotes(request.getNotes())
        .setOwnerId(request.getOwnerId())
        .setTime(request.getTime());

    long eventId = ofy().save().entity(event).now().getId();

    List<Invites> invitesList = Lists.newArrayList();
    for (String phone : request.getInviteePhoneNumbers()) {
      // TODO(suhas): Get registration records for these phone numbers and add eventId, userId in invites.
    }
    ofy().save().entities(invitesList).now();

    return new CreateEventResponse().setEventId(eventId);
  }
}
