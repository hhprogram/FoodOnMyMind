package com.harrison.foodonmymind;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;

import static android.content.ContentValues.TAG;
import static com.harrison.foodonmymind.R.string.lat;
import static com.harrison.foodonmymind.R.string.lon;

public class TabsActivity extends AppCompatActivity
        implements  {

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private TabLayout tabLayout;
    private ViewPager pager;
    public static final int REQUEST_PERMISSION_STATE = 1;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean boxOne = getIntent().getBooleanExtra(getString(R.string.preset_recipes), false);
        boolean boxTwo = getIntent().getBooleanExtra(getString(R.string.custom_recipes), false);
        boolean boxThree = getIntent().getBooleanExtra(getString(R.string.restaurants), false);
        //        below adding tabs only if the associated box is checked. i.e only create as many tabs
//        as the user wants to search
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
//        is no check boxes are checked then assume the user wanted to search all three and thus
//        want to create all tabs
        if (!boxOne && !boxTwo && ! boxThree) {
            boxOne = true;
            boxTwo = true;
            boxThree = true;
        }
        if (boxOne) {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.preset_recipes)));
        }
        if (boxTwo) {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.custom_recipes)));
        }
        if (boxThree) {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.restaurants)));
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//            a check if we already have permission then no need to go through requesting permission
//            and just access location. Otherwise start request permission process
            if (!checkPermission()) {
                requestPermissions();
            } else {
                getLastLocation();
            }

        }
        pager = (ViewPager) findViewById(R.id.pager);
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount()
                , this);
        pager.setAdapter(adapter);
//        then reset the checkboxes as if we go back to the mainActivity and then don't touch the
//        checkboxes at all then the preferences will be saved and give an incorrect query.
//        note we have to do this after the declaration of teh PageAdapter as the PageAdapter uses
//        the SharedPreferences to record the boolean values associated with whether or not the
//        boxes are checked. but after the declaration we keep them in variables in the PageAdapter
//        so can then changed the preferences after
        Log.d(TAG, "Before reset: " + pref.getString(getString(R.string.user_search), null));
        resetCheckBoxes();
    }

    /**
     * helper method that just checks if user has already given permission for app to use
     * fine location.
     * First we call checkSelfPermission which checks if we have the permission of the
     * 3rd Arg (this guess to access fine location)
     * Then that outputs either PackageManager.PERMISSION_GRANTED or
     * PackageManager.PERMISSION_DENIED. Then we just output if what we got back equals either
     * of this static variables
     * @return - returns true if permission already granted
     */
    public boolean checkPermission() {
        int checkedPermissionState = ActivityCompat.checkSelfPermission(this
                , Manifest.permission.ACCESS_FINE_LOCATION);
        return checkedPermissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * simpler helper method that just called the request permissions method from the
     * ActivityCompat class for access to fine location
     * Kicks of the request permission callback sequence. Once we get the response the callback
     * calls the built in onRequestPermissionsResult which is part of the
     * ActivityCompat.OnRequestPermissionsResultCallback interface.
     */
    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this
                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                , REQUEST_PERMISSION_STATE);
    }

    /**
     * Helper method that displays a 'snackbar':
     * https://developer.android.com/reference/android/support/design/widget/Snackbar.html
     *
     * This snackbar goes to the front of the screen
     * @param snackText - label / description of snack bar user will see
     * @param str_id_action - the string resource id # for xml resources used to describe the
     *                      snackBar action
     * @param listener - the View.onClickListener will be added to the snackbar action (it is a
     *                 required argument of the setAction() so that the user can click it and
     *                 accept the permission or not)
     */
    private void showSnackBar(String snackText, int str_id_action, View.OnClickListener listener) {
//        3 args (1) the view that will serve as the parent to this snackbar. (2) the label to this
//        pop up snackbar (3) duration of time snackbar is being shown. Want it indefinite as need
//        to wait for user input
        Snackbar snack = Snackbar.make(findViewById(R.id.activity_tabs), snackText
                , Snackbar.LENGTH_INDEFINITE);
//        then we set this snackBar to have an action. 2 args (1) the action's label (2) the
//        listener that will execute some sort of code when it is clicked
        snack.setAction(getString(str_id_action), listener);
    }

    /**
     * method to populate the location preference with the current last location
     * Uses the FusedLocationClient and calls getLastLocation() to get a task object. And then we
     * add an OnSuccessListener to this task object to execute code once the location task has
     * completed
     *
     * only called once confirmed permissions have been properly granted
     */
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
//               inside this method do whatever you want with the retrieved location data. Rare
//                cases location arg will be null thus should deal with that case
                if (location == null) {
                    Log.d(TAG, "onSuccess: getLastLocation returned a null location");
                    editor.putString(getString(lat), null);
                    editor.putString(getString(lon), null);
                } else {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    editor.putString(getString(R.string.lat), Double.toString(lat));
                    editor.putString(getString(R.string.lon), Double.toString(lon));
                    Log.d(TAG, "onSuccess: getLastLocation returned"
                            + String.format(Locale.US, "Lat: %f , Lon: %f", lat, lon));
                }
            }
        });
    }

    /**
     * Helper method that is called when we need to check and then potentially request permission to
     * use GPS location data
     */
    private void requestPermissions() {
        boolean rationale = ActivityCompat.shouldShowRequestPermissionRationale(this
                , Manifest.permission.ACCESS_FINE_LOCATION);
        if (rationale) {
            Log.d(TAG, "requestPermissions: will display to user rationale of requesting fine " +
                    "location permission");
//            this listener will be used to trigger the location permission request if user
//            clicks ok. fed into the setAction() method of the snackBar
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startLocationPermissionRequest();
                }
            };
            showSnackBar(getString(R.string.permission_bar), R.string.snackBar_action, listener);
        } else {
//            if no rationale for permission required then we just kick off the permission sequence
//            needed to access fine location
            startLocationPermissionRequest();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_STATE:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: permission denied");
                    } else {
                        getLastLocation();
                        Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                    }
                }
        }
    }

    /**
     * Helper method that resets the preferences to false. As we want them all to reset after every
     * search is completed
     */
    private void resetCheckBoxes() {
        editor = pref.edit();
        editor.putBoolean(getString(R.string.preset_recipes), false);
        editor.putBoolean(getString(R.string.custom_recipes), false);
        editor.putBoolean(getString(R.string.restaurants), false);
        editor.commit();
    }
}
