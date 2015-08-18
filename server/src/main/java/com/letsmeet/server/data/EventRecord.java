package com.letsmeet.server.data;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

/**
 * Event data object.
 */
@Entity
public class EventRecord {

  @Id
  Long id;

  private String name;

  private String notes;

  private GeoPt location;

  private Date time;

  private long ownerId;

  public long getId() {
    return id;
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

  public GeoPt getLocation() {
    return location;
  }

  public EventRecord setLocation(GeoPt location) {
    this.location = location;
    return this;
  }

  public Date getTime() {
    return time;
  }

  public EventRecord setTime(Date time) {
    this.time = time;
    return this;
  }

  public long getOwnerId() {
    return ownerId;
  }

  public EventRecord setOwnerId(long ownerId) {
    this.ownerId = ownerId;
    return this;
  }
}
