package com.letsmeet.android.widgets.placeselect;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * View to select place.
 * TODO(suhas): Add attribution 'Powered by Google'
 */
public class PlaceSelectView extends AutoCompleteTextView {

  private PlaceAutocompleteAdapter placeAdapter;
  private PlaceInfo selectedPlace;

  // Pune.
  private static final LatLngBounds DEFAULT_BOUNDS = new LatLngBounds(
      new LatLng(18.441577, 73.749196), new LatLng(18.622241, 73.978536));

  public PlaceSelectView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void init(FragmentActivity activity) {
    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getContext())
        .enableAutoManage(activity, 0 /* clientId */, new ConnectionFailedHandler())
        .addApi(Places.GEO_DATA_API)
        .addApi(LocationServices.API)
        .build();
    Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    LatLngBounds lastLocationBounds = DEFAULT_BOUNDS;
    if (lastLocation != null) {
      // Include current location +1 and -1 latitudes - approx +69 miles, - 69 miles.
      lastLocationBounds = new LatLngBounds.Builder()
          .include(new LatLng(lastLocation.getLatitude() - 1, lastLocation.getLongitude()))
          .include(new LatLng(lastLocation.getLatitude() + 1, lastLocation.getLongitude()))
          .build();
    }
    placeAdapter = new PlaceAutocompleteAdapter(getContext(),
        android.R.layout.simple_list_item_1,
        googleApiClient, lastLocationBounds, null);
    setAdapter(placeAdapter);

    setOnItemClickListener(new ItemClickListener());
  }

  private class ItemClickListener implements AdapterView.OnItemClickListener {
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      selectedPlace = placeAdapter.getItem(position);
    }
  }

  private class ConnectionFailedHandler implements GoogleApiClient.OnConnectionFailedListener {
    @Override public void onConnectionFailed(ConnectionResult connectionResult) {
      Toast.makeText(getContext(),
          "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
          Toast.LENGTH_LONG).show();
    }
  }

  public PlaceInfo getSelectedPlace() {
    if (selectedPlace != null) {
      return selectedPlace;
    }
    return new PlaceInfo().setAddress(getText().toString());
  }

  public void setSelectedPlace(PlaceInfo selectedPlace) {
    this.selectedPlace = selectedPlace;
    setText(selectedPlace.getAddress());
  }
}
