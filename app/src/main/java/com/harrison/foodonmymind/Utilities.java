package com.harrison.foodonmymind;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 6/13/17.
 */

public class Utilities {

    /**
     * Creates a String representation of a url
     * @param base - the url base (authority, theme etc..) everything but the query parameters
     * @param params - the query parameters and their values (ie key=<key_value>)
     * @return String representation of URL for API request
     */
    public static String createUrl(String base, String... params) {
        StringBuilder str = new StringBuilder();
        str.append(base);
        for (String param : params) {
            str.append(param);
            str.append("&");
        }
        return str.toString();
    }

    /**
     * Method that returns the result of an API query as a JSON object
     * @param path - the request URL
     * @return the JSON object result
     */
    public static JSONObject getData(String path) {
        StringBuilder str = new StringBuilder();
        try {
            URL urlPath = new URL(path);
            HttpURLConnection connection;
            connection = (HttpURLConnection) urlPath.openConnection();
            InputStream api_output = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(api_output));
            String line;
            while ((line = reader.readLine()) != null) {
                str.append(line);
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "getGoogPlace: Malformed URL");
        } catch (IOException e) {
            Log.d(TAG, "getGoogPlace: IO exception");
        }
        try {
            JSONObject obj = new JSONObject(str.toString());
            return obj;
        } catch (JSONException e) {
            Log.d(TAG, "getGoogPlace: JSON exception error");
        }
        return null;
    }
}
