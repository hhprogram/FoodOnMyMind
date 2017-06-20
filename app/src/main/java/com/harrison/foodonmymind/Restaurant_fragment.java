package com.harrison.foodonmymind;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

/**
Restaurant fragment. Created when the user wants to search restaurants. From here is where we launch
 a task to web query and find restaurants.
 We build the necessary google place search api url in this fragment
 */
public class Restaurant_fragment extends Fragment implements AsyncListener{

    Info info;
    WebAdapter adapter;


    public Restaurant_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.search_result, container, false);

    }

    /**
     * creates a webTask (asyncTask) then gets the phones current location. Then builds the api
     * query url. Then executes the asyncTask to obtain the results
     * reference for how I am getting the location:
     * https://stackoverflow.com/questions/2227292/how-to-get-latitude-and-longitude-of-the-mobile-device-in-android
     *
     * @param savedInstance
     */
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        WebTask task = new WebTask(this, getContext());
        String query = getArguments().getString(getContext().getString(R.string.user_search));
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) {
                Log.d(TAG, "onCreate: location not available");
            }
            double latitude = loc.getLatitude();
            double longitude = loc.getLongitude();
            String api_url = buildUrl(latitude, longitude, query);
            Pair pair = new Pair(Info.RESTAURANT, api_url);
            info = task.execute(pair).get();
        } catch (SecurityException e) {
            Log.d(TAG, "onCreate: Don't have relevant security permission");
        } catch (InterruptedException e) {
            Log.d(TAG, "onCreate: AsyncTask was interrupted when retrieving restaurant info");
        } catch (ExecutionException e) {
            Log.d(TAG, "onCreate: AsyncTask execution exception when retrieving restaurant info");
        }
    }
    
    /**
     * Helper method to make the appriorate url to request data
     * @param lat - phone's current latitude
     * @param lon - phone's current longitude
     * @param query - search query
     * @return a string url
     */
    private String buildUrl(double lat, double lon, String query) {
        String lat_str = Double.toString(lat);
        String lon_str = Double.toString(lon);
        String loc = lat_str + "," + lon_str;
        String base = getContext().getString(R.string.restaurantSearchApi);
        String apiKey = getContext().getString(R.string.api_key)
                + getContext().getString(R.string.googApiKey);
        query = getContext().getString(R.string.goog_q_query) + query;
        String url = Utilities.createUrl(base, loc, query, apiKey);
        return url;
    }

    /**
     * After the web query is completed we want to update the listview with the results
     * implementing this method is what ensures that the layout shown will be synced once the
     * task is completed
     */
    @Override
    public void onTaskCompletion() {
        ArrayList<Info.InfoItem> lst = info.getData();
        adapter = new WebAdapter(getContext(), lst);
        ListView lstView = (ListView)getActivity().findViewById(R.id.search_lst);
        lstView.setAdapter(adapter);
    }

}