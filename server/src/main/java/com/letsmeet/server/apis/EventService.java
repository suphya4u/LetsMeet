package com.letsmeet.server.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.users.User;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.objectify.cmd.Query;
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

import java.util.Calendar;
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
    clientIds = {Constants.ANDROID_CLIENT_ID, Constants.WEB_CLIENT_ID},
    audiences = {Constants.WEB_CLIENT_ID},
    namespace = @ApiNamespace(ownerDomain = "server.letsmeet.com",
        ownerName = "server.letsmeet.com",
        packagePath = ""))
public class EventService {

  private static final Logger log = Logger.getLogger(EventService.class.getName());
  private static final long UPCOMING_EVENT_BUFFER_MS = 7200000 /* 2 hours */;

  @ApiMethod(name = "createOrEditEvent")
  public CreateOrEditEventResponse createOrEditEvent(CreateOrEditEventRequest request,
        User verifiedUser) {
    EventDetails eventDetails = request.getEventDetails();
    EventRecord event = new EventRecord();
    if (eventDetails.getEventId() != 0) {
      event.setId(eventDetails.getEventId());
      event = ofy().load().type(EventRecord.class).id(eventDetails.getEventId()).now();
    }
    event.setName(eventDetails.getName())
        .setNotes(eventDetails.getNotes())
        .setOwnerId(eventDetails.getOwnerId())
        .setEventTimeMillis(eventDetails.getEventTimeMillis());

    if (eventDetails.getLocation() != null) {
      event.setEventLocationAddress(eventDetails.getLocation().getPlaceAddress())
          .setEventLocationGoogleMapPlaceId(eventDetails.getLocation().getPlaceId());
    }

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
    updateInvites(eventId, eventDetails.getOwnerId(), eventDetails.getEventTimeMillis(),
        invitedUsers);

    // TODO(suhas): Do this in background and not in user request.
    eventDetails.setEventId(eventId);
    GcmNotifier.getInstance().notifyNewEvent(usersToNotify, eventDetails);
    return response.setEventId(eventId);
  }

  @ApiMethod(name = "eventsForUser")
  public ListEventsForUserResponse eventsForUser(ListEventsForUserRequest request,
        User verifiedUser) {
    ListEventsForUserResponse response = new ListEventsForUserResponse();

    Query<Invites> query = ofy().load().type(Invites.class)
        .filter("userId", request.getUserId())
        .order("eventTime");
    if (request.getIgnorePastEvents()) {
      long currentTime = Calendar.getInstance().getTimeInMillis();
      long cutoffTime = currentTime - UPCOMING_EVENT_BUFFER_MS;
      query = query.filter("eventTime >", cutoffTime);
    }
    List<Invites> userInvites = query.list();

    // TODO(suhas): Add more checks like user does not exist / event or user or invite missing few
    // fields. Handle all error cases.
    for (Invites userInvite : userInvites) {
      EventRecord eventRecord = ofy().load().type(EventRecord.class)
          .id(userInvite.getEventId()).now();
      EventDetails eventDetails = toEventDetails(eventRecord,
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
  public FetchEventDetailsResponse fetchEventDetails(FetchEventDetailsRequest request,
        User verifiedUser) {
    EventRecord eventRecord = ofy().load().type(EventRecord.class).id(request.getEventId()).now();
    EventDetails eventDetails = toEventDetails(eventRecord,
        request.getUserId(),
        true /* populatePhoneNumberData */);
    return new FetchEventDetailsResponse()
        .setEventDetails(eventDetails);
  }

  @ApiMethod(name = "rsvpEvent")
  public RsvpResponse rsvpEvent(RsvpRequest request, User verifiedUser) {
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


  private void updateInvites(long eventId, long ownerId, long eventTime, Set<Long> invitedUsers) {
    // Make sure invite is added only once for each event.
    List<Invites> existingInvites = ofy().load().type(Invites.class)
        .filter("eventId", eventId).list();
    List<Long> existingInvitedUsers = Lists.transform(
        existingInvites, new Function<Invites, Long>() {
          @Nullable @Override public Long apply(Invites input) {
            return input.getUserId();
          }
        });
    List<Invites> invitesList = Lists.newArrayList();
    for (long userId : invitedUsers) {
      if (!existingInvitedUsers.contains(userId)) {
        Invites newInvite = new Invites(eventId, userId, eventTime);
        if (userId == ownerId) {
          // Set owner response as "YES" by default.
          newInvite.setResponse(Invites.Response.YES);
        }
        invitesList.add(newInvite);
      }
    }
    ofy().save().entities(invitesList).now();

    // Delete the invites that do not exist in invitedUsers (for edit event case).
    List<Invites> tobeDeleted = Lists.newArrayList();
    for (Invites invite: existingInvites) {
      if (!invitedUsers.contains(invite.getUserId())) {
        tobeDeleted.add(invite);
      }
    }
    ofy().delete().entities(tobeDeleted).now();
  }

  private EventDetails toEventDetails(EventRecord eventRecord, long userId,
        boolean populatePhoneNumberData) {

    // TODO(suhas): Move constructing EventDetails from EventRecord and other way round to
    // a common place. We might need this quite a lot.
    EventDetails eventDetails = new EventDetails()
        .setEventId(eventRecord.getId())
        .setName(eventRecord.getName())
        .setNotes(eventRecord.getNotes())
        .setEventTimeMillis(eventRecord.getEventTimeMillis());
    List<Invites> otherInvitees = ofy().load().type(Invites.class)
        .filter("eventId", eventRecord.getId()).list();
    if (userId == eventRecord.getOwnerId()) {
      eventDetails.setIsOwner(true);
    }

    for (Invites otherInvitee : otherInvitees) {
      if (otherInvitee.getUserId() == 0) {
        continue;
      }
      if (otherInvitee.getUserId() == userId) {
        eventDetails.setMyResponse(otherInvitee.getResponse());
      }
      EventDetails.Invitee invitee = new EventDetails.Invitee()
          .setResponse(otherInvitee.getResponse());
      if (populatePhoneNumberData) {
        // TODO(suhas): Cache userId to phoneNumber mapping.
        UserRecord inviteeUserRecord = ofy().load().type(UserRecord.class)
            .id(otherInvitee.getUserId()).now();
        if (inviteeUserRecord != null) {
          invitee.setPhoneNumber(inviteeUserRecord.getPhoneNumber());
          if (otherInvitee.getUserId() == eventRecord.getOwnerId()) {
            eventDetails.setOwnerPhoneNumber(inviteeUserRecord.getPhoneNumber());
          }
        }
      }
      eventDetails.addInvitee(invitee);
    }
    return eventDetails;
  }
}
