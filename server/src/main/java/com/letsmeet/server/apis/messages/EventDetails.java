package com.letsmeet.server.apis.messages;

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by suhas on 7/30/15.
 */
public class EventDetails {

  private String name;

  private String notes;

  private Date time;

  // TODO(suhas): Maybe use some location object.
  private long latitude;

  private long longitude;

  private List<String> inviteePhoneNumbers;

  public EventDetails() {
    inviteePhoneNumbers = Lists.newArrayList();
  }

  // TODO(suhas): ownerId should not be part of request. It should be somehow referred from caller.
  private long ownerId;

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

  public Date getTime() {
    return time;
  }

  public EventDetails setTime(Date time) {
    this.time = time;
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

  public List<String> getInviteePhoneNumbers() {
    return inviteePhoneNumbers;
  }

  public EventDetails addInviteePhoneNumbers(String phoneNumber) {
    this.inviteePhoneNumbers.add(phoneNumber);
    return this;
  }

  public long getOwnerId() {
    return ownerId;
  }

  public EventDetails setOwnerId(long ownerId) {
    this.ownerId = ownerId;
    return this;
  }

}
