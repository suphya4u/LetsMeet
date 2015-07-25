package com.letsmeet.server;

public class RegistrationRequest {

  private String regId;
  private String phoneNumber;
  private String name;

  public String getRegId() {
    return regId;
  }

  public RegistrationRequest setRegId(String regId) {
    this.regId = regId;
    return this;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public RegistrationRequest setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  public String getName() {
    return name;
  }

  public RegistrationRequest setName(String name) {
    this.name = name;
    return this;
  }
}
