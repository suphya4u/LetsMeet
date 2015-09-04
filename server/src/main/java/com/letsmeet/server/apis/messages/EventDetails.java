package com.letsmeet.server.apis.messages;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;
import com.letsmeet.server.data.Invites;

import java.util.List;

/**
 * Details of event.
 */
public class EventDetails {

  private long eventId;
  private String name;
  private String notes;
  private long eventTimeMillis;
  private EventLocation location;
  private List<Invitee> invitees;
  // TODO(suhas): ownerId should not be part of request. It should be somehow referred from caller.
  private long ownerId;
  private boolean isOwner;
  private String ownerPhoneNumber;
  private Invites.Response myResponse;

  public EventDetails() {
    invitees = Lists.newArrayList();
  }

  public String getName() {
    return name;
  }

  public EventDetails setName(String name) {
    this.name = name;
    return this;
  }

  public String getNotes() {
    return notes;
  }

  public EventDetails setNotes(String notes) {
    this.notes = notes;
    return this;
  }

  public long getEventTimeMillis() {
    return eventTimeMillis;
  }

  public EventDetails setEventTimeMillis(long eventTimeMillis) {
    this.eventTimeMillis = eventTimeMillis;
    return this;
  }

  public List<Invitee> getInviteePhoneNumbers() {
    return invitees;
  }

  public EventDetails addInvitee(Invitee invitee) {
    this.invitees.add(invitee);
    return this;
  }

  public long getOwnerId() {
    return ownerId;
  }

  public EventDetails setOwnerId(long ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  public String getOwnerPhoneNumber() {
    return ownerPhoneNumber;
  }

  public EventDetails setOwnerPhoneNumber(String ownerPhoneNumber) {
    this.ownerPhoneNumber = ownerPhoneNumber;
    return this;
  }

  public long getEventId() {
    return eventId;
  }

  public EventDetails setEventId(long eventId) {
    this.eventId = eventId;
    return this;
  }

  public boolean getIsOwner() {
    return isOwner;
  }

  public void setIsOwner(boolean isOwner) {
    this.isOwner = isOwner;
  }

  public EventLocation getLocation() {
    return location;
  }

  public EventDetails setLocation(EventLocation location) {
    this.location = location;
    return this;
  }

  public Invites.Response getMyResponse() {
    return myResponse;
  }

  public EventDetails setMyResponse(Invites.Response myResponse) {
    this.myResponse = myResponse;
    return this;
  }

  public static class Invitee {

    private String phoneNumber;
    private Invites.Response response;

    public String getPhoneNumber() {
      return phoneNumber;
    }

    public Invitee setPhoneNumber(String phoneNumber) {
      this.phoneNumber = phoneNumber;
      return this;
    }

    public Invites.Response getResponse() {
      return response;
    }

    public Invitee setResponse(Invites.Response response) {
      this.response = response;
      return this;
    }
  }

  public static class EventLocation {
    // Place id to identify the place in Google Maps.
    private String placeId;
    private String placeName;
    private String placeAddress;

    public String getPlaceId() {
      return placeId;
    }

    public EventLocation setPlaceId(String placeId) {
      this.placeId = placeId;
      return this;
    }

    public String getPlaceAddress() {
      return placeAddress;
    }

    public EventLocation setPlaceAddress(String placeAddress) {
      this.placeAddress = placeAddress;
      return this;
    }

    public String getPlaceName() {
      return placeName;
    }

    public EventLocation setPlaceName(String placeName) {
      this.placeName = placeName;
      return this;
    }
  }
}
