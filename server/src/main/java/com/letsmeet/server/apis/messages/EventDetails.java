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
  // TODO(suhas): Maybe use some location object.
  private long latitude;
  private long longitude;
  private List<Invitee> invitees;
  // TODO(suhas): ownerId should not be part of request. It should be somehow referred from caller.
  private long ownerId;
  private boolean isOwner;
  private String ownerPhoneNumber;

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

  public long getLatitude() {
    return latitude;
  }

  public EventDetails setLatitude(long latitude) {
    this.latitude = latitude;
    return this;
  }

  public long getLongitude() {
    return longitude;
  }

  public EventDetails setLongitude(long longitude) {
    this.longitude = longitude;
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
}
