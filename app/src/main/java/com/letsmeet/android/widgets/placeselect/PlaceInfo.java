package com.letsmeet.android.widgets.placeselect;

/**
 * Place info used in autocomplete view.
 */
public class PlaceInfo {

  private String placeId;
  private String placeAddress;

  public String getPlaceId() {
    return placeId;
  }

  public PlaceInfo setPlaceId(String placeId) {
    this.placeId = placeId;
    return this;
  }

  public String getAddress() {
    return placeAddress;
  }

  public PlaceInfo setAddress(String address) {
    this.placeAddress = address;
    return this;
  }

  @Override
  public String toString() {
    return placeAddress;
  }
}
