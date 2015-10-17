package com.letsmeet.android.common;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Service for location related stuff.
 */
public class GoogleApiHelper {

  // Pune.
  private static final LatLngBounds DEFAULT_BOUNDS = new LatLngBounds(
      new LatLng(18.441577, 73.749196), new LatLng(18.622241, 73.978536));

  private final Context context;
  private final GoogleApiClient googleApiClient;

  public GoogleApiHelper(Context context, FragmentActivity fragmentActivity, int clientId) {
    this.context = context;
    googleApiClient = new GoogleApiClient.Builder(context)
        .enableAutoManage(fragmentActivity, clientId, new ConnectionFailedHandler())
        .addApi(Places.GEO_DATA_API)
        .addApi(LocationServices.API)
        .build();
  }

  public Location getLastKnownLocation() {
    return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
  }

  public LatLngBounds getBoundsAroundLastKnownLocation() {
    Location lastLocation = getLastKnownLocation();
    if (lastLocation != null) {
      // Include current location +0.5 and -0.5 latitudes - approx +35 miles, -35 miles.
      return new LatLngBounds.Builder()
          .include(new LatLng(lastLocation.getLatitude() - 0.5, lastLocation.getLongitude()))
          .include(new LatLng(lastLocation.getLatitude() + 0.5, lastLocation.getLongitude()))
          .build();
    }
    return DEFAULT_BOUNDS;
  }

  public GoogleApiClient getGoogleApiClient() {
    return googleApiClient;
  }

  private class ConnectionFailedHandler implements GoogleApiClient.OnConnectionFailedListener {
    @Override public void onConnectionFailed(ConnectionResult connectionResult) {
      Toast.makeText(context,
          "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
          Toast.LENGTH_LONG).show();
    }
  }
}
