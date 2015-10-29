package com.letsmeet.android.widgets.placeselect;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.model.LatLngBounds;
import com.letsmeet.android.R;
import com.letsmeet.android.common.GoogleApiHelper;

/**
 * View to select place.
 * TODO(suhas): Add attribution 'Powered by Google'
 */
public class PlaceSelectView extends AutoCompleteTextView {

  private PlaceAutocompleteAdapter placeAdapter;
  private PlaceInfo selectedPlace;

  public PlaceSelectView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void init(FragmentActivity activity) {
    GoogleApiHelper googleApiHelper = new GoogleApiHelper(getContext(), activity, 0 /* clientId */);
    LatLngBounds lastLocationBounds = googleApiHelper.getBoundsAroundLastKnownLocation();
    placeAdapter = new PlaceAutocompleteAdapter(getContext(),
        googleApiHelper.getGoogleApiClient(), lastLocationBounds, null);
    setAdapter(placeAdapter);

    setOnItemClickListener(new ItemClickListener());
  }

  private class ItemClickListener implements AdapterView.OnItemClickListener {
    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      selectedPlace = placeAdapter.getItem(position);
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
