package com.letsmeet.android.widgets.contactselect;

/**
 * Contact info POJO used for contact completion.
 */
public class ContactInfo {

  private String displayName;
  private String thumbnailUrl;
  private String phoneNumber;

  public String getDisplayName() {
    return displayName;
  }

  public ContactInfo setDisplayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public ContactInfo setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
    return this;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public ContactInfo setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }
}
