package com.letsmeet.server.apis.messages;

import com.google.appengine.api.datastore.GeoPt;

import java.util.Date;
import java.util.List;

/**
 * Request for creating an event.
 */
public class CreateEventRequest {

  private String name;

  private String notes;

  private Date time;

  // TODO(suhas): Maybe use some location object.
  private long latitude;

  private long longitude;

  private List<String> inviteePhoneNumbers;

  // TODO(suhas): ownerId should not be part of request. It should be somehow referred from caller.
  private long ownerId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Date getTime() {
    return time;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public long getLatitude() {
    return latitude;
  }

  public void setLatitude(long latitude) {
    this.latitude = latitude;
  }

  public long getLongitude() {
    return longitude;
  }

  public void setLongitude(long longitude) {
    this.longitude = longitude;
  }

  public List<String> getInviteePhoneNumbers() {
    return inviteePhoneNumbers;
  }

  public void setInviteePhoneNumbers(List<String> inviteePhoneNumbers) {
    this.inviteePhoneNumbers = inviteePhoneNumbers;
  }

  public long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(long ownerId) {
    this.ownerId = ownerId;
  }
}
