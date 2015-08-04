package com.letsmeet.server.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.GeoPt;
import com.google.common.collect.Lists;
import com.letsmeet.server.apis.messages.CreateEventRequest;
import com.letsmeet.server.apis.messages.CreateEventResponse;
import com.letsmeet.server.apis.messages.EventDetails;
import com.letsmeet.server.apis.messages.ListEventsForUserRequest;
import com.letsmeet.server.apis.messages.ListEventsForUserResponse;
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

  @ApiMethod(name = "createEvent")
  public CreateEventResponse createEvent(CreateEventRequest request) {
    EventDetails eventDetails = request.getEventDetails();
    EventRecord event = new EventRecord()
        .setName(eventDetails.getName())
        .setLocation(new GeoPt(eventDetails.getLatitude(), eventDetails.getLongitude()))
        .setNotes(eventDetails.getNotes())
        .setOwnerId(eventDetails.getOwnerId())
        .setTime(eventDetails.getTime());

    long eventId = ofy().save().entity(event).now().getId();

    List<Invites> invitesList = Lists.newArrayList();
    // Add owner by default.
    invitesList.add(new Invites(eventId, eventDetails.getOwnerId()));

    for (String phone : eventDetails.getInviteePhoneNumbers()) {
      // TODO(suhas): Get registration records for these phone numbers and add eventId, userId in
      // invites.

      // TODO(suhas): Sanitize phone numbers or match excluding country code.
      // Possible options to handle country code -
      // Assume phone number does not contain country code - assume owners country.

      // TODO(suhas): Create new user for each of the phone number that do not exist yet.
    }
    ofy().save().entities(invitesList).now();

    return new CreateEventResponse().setEventId(eventId);
  }

  @ApiMethod(name = "eventsForUser")
  public ListEventsForUserResponse eventsForUser(ListEventsForUserRequest request) {
    List<Invites> eventInvites = ofy().load().type(Invites.class)
        .filter("userId", request.getUserId()).list();
    ListEventsForUserResponse response = new ListEventsForUserResponse();
    for (Invites invite : eventInvites) {
      EventRecord event = ofy().load().type(EventRecord.class).id(invite.getEventId()).now();
      // TODO(suhas): Move constructing EventDetails from EventRecord loging and other way round to
      // a common place. We might need this quite a lot.
      EventDetails eventDetails = new EventDetails();
      eventDetails.setName(event.getName());
      eventDetails.setNotes(event.getNotes());
      // Populate other event details.

      // TODO(suhas): Make sure each event is added only once. Currently there is no check on it.
      response.addEvent(eventDetails);
    }

    return response;
  }
}
