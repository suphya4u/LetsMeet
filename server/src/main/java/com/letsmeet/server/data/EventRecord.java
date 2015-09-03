package com.letsmeet.server.data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Event data object.
 */
@Entity
public class EventRecord {

  @Id
  Long id;
  private String name;
  private String notes;
  private long eventTimeMillis;
  private long ownerId;
  private String eventLocationName;
  private String eventLocationAddress;
  private String eventLocationGoogleMapPlaceId;
  // TODO(suhas): May be set lat-lang as well?

  public long getId() {
    return id;
  }

  public EventRecord setId(long id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public EventRecord setName(String name) {
    this.name = name;
    return this;
  }

  public String getNotes() {
    return notes;
  }

  public EventRecord setNotes(String notes) {
    this.notes = notes;
    return this;
  }

  public long getEventTimeMillis() {
    return eventTimeMillis;
  }

  public EventRecord setEventTimeMillis(long time) {
    this.eventTimeMillis = time;
    return this;
  }

  public long getOwnerId() {
    return ownerId;
  }

  public EventRecord setOwnerId(long ownerId) {
    this.ownerId = ownerId;
    return this;
  }

  public String getEventLocationName() {
    return eventLocationName;
  }

  public EventRecord setEventLocationName(String eventLocationName) {
    this.eventLocationName = eventLocationName;
    return this;
  }

  public String getEventLocationAddress() {
    return eventLocationAddress;
  }

  public EventRecord setEventLocationAddress(String eventLocationAddress) {
    this.eventLocationAddress = eventLocationAddress;
    return this;
  }

  public String getEventLocationGoogleMapPlaceId() {
    return eventLocationGoogleMapPlaceId;
  }

  public EventRecord setEventLocationGoogleMapPlaceId(String eventLocationGoogleMapPlaceId) {
    this.eventLocationGoogleMapPlaceId = eventLocationGoogleMapPlaceId;
    return this;
  }
}
