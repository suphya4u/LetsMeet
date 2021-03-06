package com.letsmeet.android.widgets.placeselect;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.letsmeet.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Adapter that handles Autocomplete requests from the Places Geo Data API.
 * Results are encoded as PlaceAutocomplete
 * objects
 * that contain both the Place ID and the text description from the autocomplete query.
 * <p>
 * Note that this adapter requires a valid {@link com.google.android.gms.common.api.GoogleApiClient}.
 * The API client must be maintained in the encapsulating Activity, including all lifecycle and
 * connection states. The API client must be connected with the {@link Places#GEO_DATA_API} API.
 * <p>
 * Code taken from - https://github.com/googlesamples/android-play-places/tree/master/PlaceComplete/Application/src/main/java/com/example/google/playservices/placecomplete
 * </p>
 */
public class PlaceAutocompleteAdapter
    extends ArrayAdapter<PlaceInfo> implements Filterable {

  private static final String TAG = "PlaceAutocompleteAdapter";
  private static final String FOOTER_PLACE_ID = "FOOTER_PLACEHOLDER";
  /**
   * Current results returned by this adapter.
   */
  private ArrayList<PlaceInfo> mResultList;

  /**
   * Handles autocomplete requests.
   */
  private GoogleApiClient mGoogleApiClient;

  /**
   * The bounds used for Places Geo Data autocomplete API requests.
   */
  private LatLngBounds mBounds;

  /**
   * The autocomplete filter used to restrict queries to a specific set of place types.
   */
  private AutocompleteFilter mPlaceFilter;

  /**
   * Initializes with a resource for text rows and autocomplete query bounds.
   *
   * @see android.widget.ArrayAdapter#ArrayAdapter(android.content.Context, int)
   */
  public PlaceAutocompleteAdapter(Context context, GoogleApiClient googleApiClient,
                                  LatLngBounds bounds, AutocompleteFilter filter) {
    super(context, R.layout.place_autocomplete_item, R.id.place_autocomplete_address);
    mGoogleApiClient = googleApiClient;
    mBounds = bounds;
    mPlaceFilter = filter;
  }

  /**
   * Sets the bounds for all subsequent queries.
   */
  public void setBounds(LatLngBounds bounds) {
    mBounds = bounds;
  }

  /**
   * Returns the number of results received in the last autocomplete query.
   */
  @Override
  public int getCount() {
    return mResultList.size();
  }

  /**
   * Returns an item from the last autocomplete query.
   */
  @Override
  public PlaceInfo getItem(int position) {
    return mResultList.get(position);
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    PlaceInfo placeInfo = getItem(position);
    View view;

    view = super.getView(position, convertView, parent);
    View address = view.findViewById(R.id.place_autocomplete_address);
    View attribution = view.findViewById(R.id.place_autocomplete_attribution);

    if (FOOTER_PLACE_ID.equals(placeInfo.getPlaceId())) {
      address.setVisibility(View.GONE);
      attribution.setVisibility(View.VISIBLE);
      attribution.setOnClickListener(null);
      attribution.setEnabled(false);

    } else {
      address.setVisibility(View.VISIBLE);
      attribution.setVisibility(View.GONE);
    }
    return view;
  }

  /**
   * Returns the filter for the current set of autocomplete results.
   */
  @Override
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // Skip the autocomplete query if no constraints are given.
        if (constraint != null) {
          // Query the autocomplete API for the (constraint) search string.
          mResultList = getAutocomplete(constraint);
          if (mResultList != null) {
            // The API successfully returned results.
            if (mResultList.size() > 0) {
              mResultList.add(new PlaceInfo().setPlaceId(FOOTER_PLACE_ID));
            }
            results.values = mResultList;
            results.count = mResultList.size();
          }
        }
        return results;
      }

      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
        if (results != null && results.count > 0) {
          // The API returned at least one result, update the data.
          notifyDataSetChanged();
        } else {
          // The API did not return any results, invalidate the data set.
          notifyDataSetInvalidated();
        }
      }
    };
  }

  /**
   * Submits an autocomplete query to the Places Geo Data Autocomplete API.
   * Results are returned as PlaceAutocomplete
   * objects to store the Place ID and description that the API returns.
   * Returns an empty list if no results were found.
   * Returns null if the API client is not available or the query did not complete
   * successfully.
   * This method MUST be called off the main UI thread, as it will block until data is returned
   * from the API, which may include a network request.
   *
   * @param constraint Autocomplete query string
   * @return Results from the autocomplete API or null if the query was not successful.
   * @see Places#GEO_DATA_API#getAutocomplete(CharSequence)
   */
  private ArrayList<PlaceInfo> getAutocomplete(CharSequence constraint) {
    if (mGoogleApiClient.isConnected()) {

      // Submit the query to the autocomplete API and retrieve a PendingResult that will
      // contain the results when the query completes.
      PendingResult<AutocompletePredictionBuffer> results =
          Places.GeoDataApi
              .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                  mBounds, mPlaceFilter);

      // This method should have been called off the main UI thread. Block and wait for at most 60s
      // for a result from the API.
      AutocompletePredictionBuffer autocompletePredictions = results
          .await(60, TimeUnit.SECONDS);

      // Confirm that the query completed successfully, otherwise return null
      final Status status = autocompletePredictions.getStatus();
      if (!status.isSuccess()) {
        Toast.makeText(getContext(), "Error contacting API: " + status.toString(),
            Toast.LENGTH_SHORT).show();
        autocompletePredictions.release();
        return null;
      }

      // Copy the results into our own data structure, because we can't hold onto the buffer.
      // AutocompletePrediction objects encapsulate the API response (place ID and description).

      Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
      ArrayList<PlaceInfo> resultList = new ArrayList<>(autocompletePredictions.getCount());
      while (iterator.hasNext()) {
        AutocompletePrediction prediction = iterator.next();
        // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
        PlaceInfo placeInfo = new PlaceInfo().setPlaceId(prediction.getPlaceId())
            .setAddress(prediction.getDescription());
        resultList.add(placeInfo);
      }

      // Release the buffer now that all data has been copied.
      autocompletePredictions.release();

      return resultList;
    }
    return null;
  }
}
