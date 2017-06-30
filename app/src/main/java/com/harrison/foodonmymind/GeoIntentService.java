package com.harrison.foodonmymind;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeoIntentService extends IntentService {

    ResultReceiver receiver;

    public GeoIntentService() {
        super("GeoIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String loc_input = intent.getStringExtra(getString(R.string.manual_loc));
        receiver = intent.getParcelableExtra(getString(R.string.receiver));
        Geocoder geoDude = new Geocoder(this);
        try {
            ArrayList<Address> addrs = (ArrayList<Address>)geoDude
                    .getFromLocationName(loc_input, 1);
            if (addrs != null && !addrs.isEmpty()) {
                Address addr = addrs.get(0);
                String lat = Double.toString(addr.getLatitude());
                String lon = Double.toString(addr.getLongitude());
                deliverResult(Utilities.GEO_RESULT_SUCCESS, lat, lon);
            } else {
                Log.d(TAG, "onHandleIntent: no addresses found");
                deliverResult(Utilities.GEO_RESULT_FAIL, null, null);
            }
        } catch (IOException e) {
            Log.d(TAG, "onHandleIntent: Exception when getting loc from name");
        }
    }

    /**
     * helper function to help ease the transfer of information from this intentservice to the
     * service receiver
     * using this github as a guide:
     * https://github.com/obaro/SimpleGeocodeApp/blob/master/app/src/main/java/com/sample/foo/simplegeocodeapp/GeocodeAddressIntentService.java
     */
    private void deliverResult(int resultCode, String lat, String lon) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.lat), lat);
        bundle.putString(getString(R.string.lon), lon);
//        will notify the receiver and the receiver will call its onReceiveResult
        receiver.send(resultCode, bundle);
    }

}
