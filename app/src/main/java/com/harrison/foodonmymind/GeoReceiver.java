package com.harrison.foodonmymind;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 6/29/17. Class extending Result Receiver class. We need to declare our
 * own class as we need to send an intent service and then once the intent service is done it
 * returns the result to this receiver and notifies it and an instance object of this class will
 * itself call onReceiveResult - thus we need to declare what we want it to do once the method is
 * called
 */

public class GeoReceiver extends ResultReceiver {

    Context mContext;
    SharedPreferences.Editor editor;
    AsyncListener listener;
    SharedPreferences pref;

    public GeoReceiver(Handler handler, Context context, AsyncListener listener) {
        super(handler);
        mContext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = pref.edit();
        this.listener = listener;
    }

    /**
     * Method that needs to be overridden as this method gets called by the specific receiver object
     * once it is noticed it has received a result. Always put lat and lon in the preferences,
     * if nothing found then both values will be null (see GeoIntentService class)
     * @param resultCode - arg that is passed in by intent service to denote success or failure
     * @param resultData - the bundle that holds the data from the intent service
     */
    @Override
    protected void onReceiveResult(int resultCode, final Bundle resultData) {
        String lat = resultData.getString(mContext.getString(R.string.lat));
        String lon = resultData.getString(mContext.getString(R.string.lon));
        editor.putString(mContext.getString(R.string.lat), lat);
        editor.putString(mContext.getString(R.string.lon), lon);
        Log.d(TAG, "onReceiveResult: lat - "
                + pref.getString(mContext.getString(R.string.lat), null)
                + "; lon - " + pref.getString(mContext.getString(R.string.lon), null));
        switch (resultCode) {
            case Utilities.GEO_RESULT_SUCCESS:
                Log.d(TAG, "onReceiveResult: Location result received...lat: " + lat
                        + ", lon:"+ lon);
                break;
            case Utilities.GEO_RESULT_FAIL:
                Toast.makeText(mContext, mContext.getString(R.string.invalid_addr)
                        , Toast.LENGTH_LONG).show();
                Log.d(TAG, "onReceiveResult: Location result failed");
        }
//        forgot to put this in here and was causing problems as changes to the shared preferences
//        would not be saved when I got out of this method
        editor.commit();
        this.listener.onTaskCompletion();
    }
}
