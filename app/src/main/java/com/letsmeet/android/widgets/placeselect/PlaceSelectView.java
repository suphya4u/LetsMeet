package com.letsmeet.android.widgets.placeselect;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * View to select place.
 * TODO(suhas): Add attribution 'Powered by Google'
 */
public class PlaceSelectView extends AutoCompleteTextView {

  private PlaceAutocompleteAdapter placeAdapter;
  private OnPlaceSelection placeSelectionListener;

  private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
      new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

  public PlaceSelectView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void init(FragmentActivity activity) {
    GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getContext())
        .enableAutoManage(activity, 0 /* clientId */, new ConnectionFailedHandler())
        .addApi(Places.GEO_DATA_API)
        .build();
    placeAdapter = new PlaceAutocompleteAdapter(getContext(),
        android.R.layout.simple_list_item_1,
        googleApiClient, BOUNDS_GREATER_SYDNEY, null);
    setAdapter(placeAdapter);

    setOnItemClickListener(new ItemClickListener());
  }

  public void setOnPlaceSelectionCallback(OnPlaceSelection callback) {
    placeSelectionListener = callback;
  }

  private class ItemClickListener implements AdapterView.OnItemClickListener {
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      if (placeSelectionListener != null) {
        PlaceInfo placeInfo = placeAdapter.getItem(position);
        placeSelectionListener.handlePlaceSelect(placeInfo);
      }
    }
  }

  private class ConnectionFailedHandler implements GoogleApiClient.OnConnectionFailedListener {
    @Override public void onConnectionFailed(ConnectionResult connectionResult) {
      Toast.makeText(getContext(),
          "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
          Toast.LENGTH_LONG).show();
    }
  }

  public static interface OnPlaceSelection {
    public void handlePlaceSelect(PlaceInfo placeInfo);
  }
}
