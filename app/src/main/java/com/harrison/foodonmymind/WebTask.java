package com.harrison.foodonmymind;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 6/14/17.
 * AsyncTask used to query the web APIs to search recipes and restaurants
 * Note in doInBackground and in the class declaration can't have type declaration for Pair class
 * or else not correctly extending AsyncTask class
 */

public class WebTask extends AsyncTask<Pair, Void, Info> {

    private AsyncListener listener;
    private Context mContext;

    public WebTask(AsyncListener listener, Context context) {
        this.listener = listener;
        this.mContext = context;
    }

    /**
     *
     * @param params - Pair objects element (1) is the type of info within this Info object that
     *               will be returned by the asynctask (2) url to which we must go to, to get that
     *               info
     * @return Info object that has been populated with data
     */
    @Override
    public Info doInBackground(Pair... params) {
        String url = (String) params[0].getSecond();
        String info_type = (String) params[0].getFirst();
        JSONObject response = Utilities.getData(url);
        Info info = new Info(info_type, mContext);
        Log.d(TAG, "doInBackground: url:" + url);
        info.populateData(response);
        for (String name : info.getInfoNames()) {
            Log.d(TAG, "doInBackground: item name: " + name);
        }
        return info;
    }

    /**
     * Method used to call the onTaskCompletion() method so that whatever class that implements the
     * AsyncListener interface will be updated properly once this task is complete
     * @param result The result from doInBackground (just is normal and must have this in order to
     *               properly override
     */
    @Override
    public void onPostExecute(Info result) {
        listener.onTaskCompletion();
    }
}
