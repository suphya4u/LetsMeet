package com.letsmeet.server.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.GeoPt;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.letsmeet.server.apis.messages.CreateOrEditEventRequest;
import com.letsmeet.server.apis.messages.CreateOrEditEventResponse;
import com.letsmeet.server.apis.messages.EventDetails;
import com.letsmeet.server.apis.messages.FetchEventDetailsRequest;
import com.letsmeet.server.apis.messages.FetchEventDetailsResponse;
import com.letsmeet.server.apis.messages.ListEventsForUserRequest;
import com.letsmeet.server.apis.messages.ListEventsForUserResponse;
import com.letsmeet.server.apis.messages.RsvpRequest;
import com.letsmeet.server.apis.messages.RsvpResponse;
import com.letsmeet.server.data.EventRecord;
import com.letsmeet.server.data.Invites;
import com.letsmeet.server.data.UserRecord;
import com.letsmeet.server.notifications.GcmNotifier;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nullable;

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

  @ApiMethod(name = "createOrEditEvent")
  public CreateOrEditEventResponse createOrEditEvent(CreateOrEditEventRequest request) {
    EventDetails eventDetails = request.getEventDetails();
    EventRecord event = new EventRecord();
    if (eventDetails.getEventId() != 0) {
      event.setId(eventDetails.getEventId());
      event = ofy().load().type(EventRecord.class).id(eventDetails.getEventId()).now();
    }
    event.setName(eventDetails.getName())
        .setLocation(new GeoPt(eventDetails.getLatitude(), eventDetails.getLongitude()))
        .setNotes(eventDetails.getNotes())
        .setOwnerId(eventDetails.getOwnerId())
        .setEventTimeMillis(eventDetails.getEventTimeMillis());

    long eventId = ofy().save().entity(event).now().getId();

    Set<Long> invitedUsers = Sets.newHashSet();
    // Add owner by default.
    invitedUsers.add(eventDetails.getOwnerId());
    List<UserRecord> usersToNotify = Lists.newArrayList();
    CreateOrEditEventResponse response = new CreateOrEditEventResponse();

    for (EventDetails.Invitee invitee : eventDetails.getInviteePhoneNumbers()) {
      // TODO(suhas): Get registration records for these phone numbers and add eventId, userId in
      // invites.
      String phoneNumber = invitee.getPhoneNumber();
      List<UserRecord> users = ofy().load().type(UserRecord.class)
          .filter("phoneNumber", phoneNumber).list();
      if (users.size() > 1) {
        log.severe("More than one user with same phone number");
      };

      long invitedUserId;
      if (users.isEmpty() || Strings.isNullOrEmpty(users.get(0).getRegId())) {
        response.addPhoneNumberNotYetRegistered(phoneNumber);
      }
      if (users.isEmpty()) {
        UserRecord newUserRecord = new UserRecord()
            .setPhoneNumber(phoneNumber);
        invitedUserId = ofy().save().entity(newUserRecord).now().getId();
      } else {
        UserRecord user = users.get(0);
        invitedUserId = user.getId();
        usersToNotify.add(user);
      }
      invitedUsers.add(invitedUserId);

      // TODO(suhas): Sanitize phone numbers or match excluding country code.
      // Possible options to handle country code -
      // Assume phone number does not contain country code - assume owners country.
    }
    List<Invites> invitesList = createInvitesList(eventId, invitedUsers);
    ofy().save().entities(invitesList).now();

    // TODO(suhas): Do this in background and not in user request.
    eventDetails.setEventId(eventId);
    GcmNotifier.getInstance().notifyNewEvent(usersToNotify, eventDetails);
    return response.setEventId(eventId);
  }

  private List<Invites> createInvitesList(long eventId, Set<Long> invitedUsers) {
    // Make sure invite is added only once for each event.
    List<Invites> existingInvites = ofy().load().type(Invites.class)
        .filter("eventId", eventId).list();
    List<Long> existedInvitedUsers = Lists.transform(
        existingInvites, new Function<Invites, Long>() {
          @Nullable @Override public Long apply(Invites input) {
            return input.getUserId();
          }
        });
    List<Invites> invitesList = Lists.newArrayList();
    for (long userId : invitedUsers) {
      if (!existedInvitedUsers.contains(userId)) {
        invitesList.add(new Invites(eventId, userId));
      }
    }
    return invitesList;
  }

  @ApiMethod(name = "eventsForUser")
  public ListEventsForUserResponse eventsForUser(ListEventsForUserRequest request) {
    List<Invites> userInvites = ofy().load().type(Invites.class)
        .filter("userId", request.getUserId()).list();
    ListEventsForUserResponse response = new ListEventsForUserResponse();

    // TODO(suhas): Add more checks like user does not exist / event or user or invite missing few
    // fields. Handle all error cases.
    for (Invites userInvite : userInvites) {
      EventDetails eventDetails = getEventDetails(userInvite.getEventId(),
          request.getUserId(),
          false /* populatePhoneNumberData */);  // No phone numbers populated for list view.

      // TODO(suhas): Make sure each event is added only once. Currently there is no check on it.
      response.addEvent(eventDetails);
    }

    return response;
  }

  // TODO(suhas): Not using API names starting with Get, List etc. If failed for some random reason.
  // Not sure if Name really matter.
  @ApiMethod(name = "fetchEventDetails")
  public FetchEventDetailsResponse FetchEventDetails(FetchEventDetailsRequest request) {
    EventDetails eventDetails = getEventDetails(request.getEventId(),
        request.getUserId(),
        true /* populatePhoneNumberData */);
    return new FetchEventDetailsResponse()
        .setEventDetails(eventDetails);
  }

  @ApiMethod(name = "rsvpEvent")
  public RsvpResponse rsvpEvent(RsvpRequest request) {
    List<Invites> invitesList = ofy().load().type(Invites.class)
        .filter("userId", request.getUserId())
        .filter("eventId", request.getEventId())
        .list();
    if (invitesList.isEmpty()) {
      log.severe("Empty invites list while RSVP request for user [" + request.getUserId()
          + "] Event [" + request.getEventId() + "]");
      return new RsvpResponse().setSuccess(false);
    }

    if (invitesList.size() > 1) {
      log.severe("More than one invite for user [" + request.getUserId() + "] event ["
          + request.getEventId() + "]");
    }
    Invites invite = invitesList.get(0);
    invite.setFromRsvpRequestEnum(request.getResponse());
    ofy().save().entity(invite).now();
    return new RsvpResponse().setSuccess(true);
  }

  private EventDetails getEventDetails(long eventId, long userId, boolean populatePhoneNumberData) {
    EventRecord event = ofy().load().type(EventRecord.class).id(eventId).now();

    // TODO(suhas): Move constructing EventDetails from EventRecord and other way round to
    // a common place. We might need this quite a lot.
    EventDetails eventDetails = new EventDetails()
        .setEventId(event.getId())
        .setName(event.getName())
        .setNotes(event.getNotes())
        .setEventTimeMillis(event.getEventTimeMillis());
    List<Invites> otherInvitees = ofy().load().type(Invites.class)
        .filter("eventId", event.getId()).list();
    if (userId == event.getOwnerId()) {
      eventDetails.setIsOwner(true);
    }

    for (Invites otherInvitee : otherInvitees) {
      if (otherInvitee.getUserId() == 0) {
        continue;
      }
      EventDetails.Invitee invitee = new EventDetails.Invitee()
          .setResponse(otherInvitee.getResponse());
      if (populatePhoneNumberData) {
        // TODO(suhas): Cache userId to phoneNumber mapping.
        UserRecord inviteeUserRecord = ofy().load().type(UserRecord.class)
            .id(otherInvitee.getUserId()).now();
        if (invitee != null) {
          invitee.setPhoneNumber(inviteeUserRecord.getPhoneNumber());
          if (otherInvitee.getUserId() == event.getOwnerId()) {
            eventDetails.setOwnerPhoneNumber(invitee.getPhoneNumber());
          }
        }
      }
      eventDetails.addInvitee(invitee);
    }
    return eventDetails;
  }
}
