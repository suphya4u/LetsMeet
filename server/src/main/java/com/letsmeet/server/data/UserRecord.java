package com.letsmeet.server.data;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class UserRecord {

  @Id
  Long id;

  @Index
  private String regId;

  @Index
  private String phoneNumber;

  private String name;

  private GeoPt userLocation;

  public UserRecord() {
  }

  public long getId() {
    return id;
  }

  public String getRegId() {
    return regId;
  }

  public UserRecord setRegId(String regId) {
    this.regId = regId;
    return this;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public UserRecord setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public String getName() {
    return name;
  }

  public UserRecord setName(String name) {
    this.name = name;
    return this;
  }

  public GeoPt getUserLocation() {
    return userLocation;
  }

  public UserRecord setUserLocation(GeoPt userLocation) {
    this.userLocation = userLocation;
    return this;
  }
}