package com.letsmeet.server.data;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * The Objectify object model for device registrations we are persisting
 */
// TODO(suhas): Rename this to UserRecord. As this is used as user data later in the code.
@Entity
public class RegistrationRecord {

  @Id
  Long id;

  @Index
  private String regId;

  @Index
  private String phoneNumber;

  private String name;

  private GeoPt userLocation;

  public RegistrationRecord() {
  }

  public String getRegId() {
    return regId;
  }

  public RegistrationRecord setRegId(String regId) {
    this.regId = regId;
    return this;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public RegistrationRecord setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public String getName() {
    return name;
  }

  public RegistrationRecord setName(String name) {
    this.name = name;
    return this;
  }

  public GeoPt getUserLocation() {
    return userLocation;
  }

  public RegistrationRecord setUserLocation(GeoPt userLocation) {
    this.userLocation = userLocation;
    return this;
  }
}