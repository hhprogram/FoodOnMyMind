package com.harrison.foodonmymind;

import android.Manifest;
import android.support.v4.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.harrison.foodonmymind.R.string.lat;
import static com.harrison.foodonmymind.R.string.lon;
import static com.harrison.foodonmymind.Utilities.REQUEST_PERMISSION_STATE;

/**
 * Implementing OnRequestPermissionResultCallback here to get access to fine location when we need
 * it. Do it here instead of TabsActivity as by this activity we will know whether or not we
 * need to ask for location (in the handle search intent method). Therefore, putting all the
 * location requesting and getting methods in here makes for an easier implementation the rest of
 * the way down
 *
 * Also implement AsyncListenener interface as will pass an instance of this Activity into the
 * GeoReceiver and thus after the GeoReceiver has procured a lat and lon for a manually entered
 * addr then it will call the onTaskCompletion and we will have it implemented here to launch the
 * TabsActivity. Do it this way so that we don't launch the TabActivity until we have all the
 * necessary info to do a google search
 *
 * use AlertDialog.Builder class. Similar to Uri.Builder or StringBuilder. It just a helpful class
 * to help 'build' and put together an AlertDialog. But then need to create() them to actually
 * transform the builder object into a built AlertDialog that can then be shown (show() does both
 * this creation and building of the object). But only after create() is called on an
 * AlertDialog.Builder can you call methods that an AlertDialog object can
 */

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, AsyncListener{

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Geocoder geoDude;
//    making these class variables so I can refer to them for any method within this class and not
//    have to pass them along through arguments etc.. (makes it easier for situation when i want
//    to launch tabIntent but have to wait for the gps request information to be returned before
//    i launch the activity as I need these 3 boolean values but also need to ensure the gps
//    location has been returned before launching the tabActivity which will in turn launch the
//    web query to search nearby  restuarants
    boolean boxOne, boxTwo, boxThree;
    GeoReceiver receiver;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "on create Main called");
        setContentView(R.layout.activity_main);
//        this line get any intent that might have been used to start this activity. If just
//        launching the app then this will be blank. But since we declared this activity as
//        a 'searchable activity' that can handle search intents (in the manifest) when someone
//        enters a search query and hits enter then it will basically relaunch the main Activity
//        but now with an intent
        Intent intent = getIntent();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
//        taking out any previous search query that was stored in shared preferences
        editor.remove(getString(R.string.user_search));
        editor.commit();
//        if the intent is a search intent then do something.
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            handleSearchIntent(intent);
//            resetCheckBoxes();
        }
//        thus everytime we load the view for the mainActivity we reset all the booleans in the
//        preferenceManager since we already reset all checkbox values already and don't want to
//        confuse the user. AND WE CALL it here because basically

    }


    /**
     * Helper method that is only called when the intent action that starts this activity is a
     * search action
     * @param intent - intent that is has action SEARCH
     */
    public void handleSearchIntent(Intent intent) {
//        SearchManager.QUERY is a special key that gets the string that was entered in the search
//        dialog
        String query = intent.getStringExtra(SearchManager.QUERY);
//        takes this query string and then put it in the shared preferences to be referenced later
        editor.putString(getString(R.string.user_search), query);
        editor.commit();
        Log.d(TAG, "handleSearchIntent: " + pref.getString(getString(R.string.user_search), null));
//        first get all the checkbox preferences. and use that to help me determine what to return
//        as a search result
        boxOne = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.preset_recipes), false);
        boxTwo = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.custom_recipes), false);
        boxThree = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.restaurants), false);
        Log.d(TAG, "handleSearchIntent: Preset -" + boxOne + ", Custom -" + boxTwo + ", Rest.-"
                + boxThree);
        if (boxThree) {
//            only initialize it if we need for searching restaurants
            receiver = new GeoReceiver(null, this, this);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            decideDialog();
        } else {
//            if no searching restaurants then we just launch the intent, no need to go about
//            asking for gps locatino etc..
            launchTabs();
        }
    }

    /**
     * method to populate the location preference with the current last location
     * Uses the FusedLocationClient and calls getLastLocation() to get a task object. And then we
     * add an OnSuccessListener to this task object to execute code once the location task has
     * completed
     *
     * gives a warning that shouldn't use the method getLastLocation() in a method that doesn't
     * explicitly request location permission but it's ok because this only called after all
     * permissions are granted
     * only called once confirmed permissions have been properly granted
     */
    private void getLastLocation() {
//        this method getLastLocation() uses the client to get the location and then assigns it a
//        listener so once the location retrieval is successful then it can execute the below code
//        which is to put the lat and lon in the editPreferences
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this
                , new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
//               inside this method do whatever you want with the retrieved location data. Rare
//                cases location arg will be null thus should deal with that case
                if (location == null) {
                    Log.d(TAG, "onSuccess GPS: getLastLocation returned a null location");
                    editor.putString(getString(lat), null);
                    editor.putString(getString(lon), null);
                } else {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    popLatLon(lat, lon);
                    Log.d(TAG, "onSuccess GPS: getLastLocation returned"
                            + String.format(Locale.US, "Lat: %f , Lon: %f", lat, lon));

                }
                launchTabs();
            }
        });
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
            showSnackBar(getString(R.string.permission_bar), R.string.snack_confirm, listener);
        } else {
//            if no rationale for permission required then we just kick off the permission sequence
//            needed to access fine location
            startLocationPermissionRequest();
        }
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
                , Utilities.REQUEST_PERMISSION_STATE);
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
     * helper function that shows a Dialog in which user either chooses (1) to use fine location,
     * in which they allow app to use fine location (2) deny location and manually enter location
     * Note: need to do .show() and not .create()...Because .create() just creates the dialog but
     * doesn't display it at all. While, show() creates the AlertDialog but then also immediately
     * displays it
     */
    private void decideDialog() {
        AlertDialog.Builder[] dialogs = setUpDialogs();
        AlertDialog.Builder home_dialog = dialogs[0];
        Log.d(TAG, "decideDialog: inside");
        home_dialog.show();
    }


    /**
     * helper function to setup the manual input dialog. And the 'home' dialog whcih is the dialog
     * that asks if user wants to use GPS location or manually enter an addr. Refactored out of
     * decideDialog() to keep that method cleaner
     * @return - returns an array of DialogBuilders
     *
     */
    private AlertDialog.Builder[] setUpDialogs() {
//        create the manual address input dialog outside of the OnClickListener class definiton as
//        if we do it inside then we'd have to somehow refernce the context by passing it as an
//        argument to this method etc.. so just do it out here. Must be final as we are calling
//        it in an inner class
        final AlertDialog.Builder manual_input = new AlertDialog.Builder(this);
        final AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);
//        make toast out here same rease as making manual_input dialog (context reason)
        final Toast toast_canel = Toast.makeText(this
                , getString(R.string.toast_cancel)
                , Toast.LENGTH_LONG);
//        doing the intent for the same reason. So that 'this' refers to this activity vs. the
//        dialogInterface OnClickListener
        final Intent intent = new Intent(this, GeoIntentService.class);
//        getting an inflater that will be used to inflate my dialog_layout
        LayoutInflater inflater = getLayoutInflater();
//        inflate this view so i can (1) set it as the view for my alertDialog via my builder obj
//        manual_input (2) refer to this view and call findViewById on it to find my EditText view
//        that lives within dialog_layout
        View dialog_home = inflater.inflate(R.layout.dialog_layout, null);
//        now I am coding what will happen when the 'positive' action button is pressed in the
//        dialog box. Below in the OnClick we get the EditText field. Then we use the Geocoder
//        class to translate a string address or location name and it converts to an Address object
//        which will have a lat and lon. (getFromLocationName() 's 2nd arg is 1 as we want to limit
//        the return values from this class to at most one Address object). Then we will check
//        if Geocoder returned any Address objects - if not we will ask user to re-enter, if so
//        then we assume that these objects have lat and lons and just go ahead with search. note
//        the 2nd arg of setPositiveButton is a listener and we are doing an anonymous class
//        declaration for ease
//        just putting the id of the layout - because if I inflate it within the setView method then
//        it will immediately inflate and display that layout when that line is executed not when
//        I want it when the actual alert dialog is created
        manual_input.setView(dialog_home);
        final EditText input = (EditText) dialog_home.findViewById(R.id.addr_input);
        Log.d(TAG, "setUpDialogs: " + input.toString());
        manual_input.setPositiveButton(getString(R.string.manual_confirm)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intent.putExtra(getString(R.string.manual_loc)
                                , input.getText().toString());
                        Log.d(TAG, "onClick: input is : " + input.getText().toString());
                        intent.putExtra(getString(R.string.receiver), receiver);
                        startService(intent);
                    }})
//        if the user cancels then we want to dismiss the current dialog and go back one dialog
//        and create the home_dialog again
                .setNegativeButton(getString(R.string.manual_cancel)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toast_canel.show();
                        dialogInterface.cancel();
//                        see comments for decideDialog() for why i use show() and not create()
                        dialog_builder.show();
            }
        });
//        setting up listener if user is ok with using the GPS on the phone for location
        String[] choices = new String[]{getString(R.string.gps_loc)
                , getString(R.string.manual_loc)};
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (checkPermission()) {
                            getLastLocation();
                        } else {
                            requestPermissions();
                        }
                        break;
                    case 1:
                        dialog.cancel();
//                        see comments for decideDialog() for why i use show() and not create()
                        manual_input.show();
                }
            }
        };
        dialog_builder.setSingleChoiceItems(choices, -1, listener);
        return new AlertDialog.Builder[]{dialog_builder, manual_input};
    }

    /**
     * Helper method called either only once we successfully get the GPS location and store it in
     * our preferences or get a location from a location name and have stored its location in
     * shared pref
     */
    private void launchTabs() {
        Intent intent = makeTabIntent();
        startActivity(intent);
    }

    /**
     * helper method that takes lat and lon and populates the sharedPreferences
     * @param lat
     * @param lon
     */
    private void popLatLon(double lat, double lon) {
        editor.putString(getString(R.string.lat),Double.toString(lat));
        editor.putString(getString(R.string.lon),Double.toString(lat));
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
//        This below code is required to then link the searchView defined in menu/main.xml to the
//        searchable element defined in the XML directory. Then the searchView shown in the menu
//        will have the same layout as searchable.xml
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    /**
     * helper function. Used so instead of copy and pasting the intent creating and put lines of
     * code just need to have one line that is redundant - which is calling this method.
     * Only called once the three boolean values have been properly set therefore ok to reference
     * class variable
     * @return an intent that will be used to launch the TabActivity
     */
    private Intent makeTabIntent() {
        Intent tabIntent = new Intent(this, TabsActivity.class);
        tabIntent.putExtra(getString(R.string.preset_recipes), boxOne);
        tabIntent.putExtra(getString(R.string.custom_recipes), boxTwo);
        tabIntent.putExtra(getString(R.string.restaurants), boxThree);
        return tabIntent;
    }

    @Override
    public void onTaskCompletion() {
        Log.d(TAG, "MainActivity onTaskCompletion: lat: " + pref.getString(getString(R.string.lat), null)
                + "; lon: " + pref.getString(getString(R.string.lon), null));
        Map<String, ?> keys = pref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d(TAG, "MainActivity onTaskCompletion: key - " + entry.getKey() + "; value - "
                    + entry.getValue().toString());
        }
        launchTabs();
    }

    //each of the below methods sets the boolean value in the preference manager so that I can
//    reference that value when I do a search. Since a search intent re-creates an activity and
//    resets the checkbox value to false (by default - as the box is unchecked after a search is done)
//    thus I cannot just reference a normal boolean variable - need to put it in the
//    sharedPreferences so that it persists
    public void onBoxOneChecked (View view) {
        Log.d(TAG, "onBoxOneChecked: Clicked");
        boolean box = ((CheckBox) view).isChecked();
        Log.d(TAG, "onBoxOneChecked: " + ((CheckBox) view).isChecked());
        editor.putBoolean(getString(R.string.preset_recipes), box);
        editor.commit();
    }

    public void onBoxTwoChecked (View view) {
        Log.d(TAG, "onBoxTwoChecked: Clicked");
        boolean box = ((CheckBox) view).isChecked();
        Log.d(TAG, "onBoxTwoChecked: " + ((CheckBox) view).isChecked());
        editor.putBoolean(getString(R.string.custom_recipes), box);
        editor.commit();
    }

    public void onBoxThreeChecked (View view) {
        Log.d(TAG, "onBoxThreeChecked: Clicked");
        boolean box = ((CheckBox) view).isChecked();
        Log.d(TAG, "onBoxThreeChecked: " + ((CheckBox) view).isChecked());
        editor.putBoolean(getString(R.string.restaurants), box);
        editor.commit();
    }

    /**
     * called when the Add Recipe button is clicked in Main Activity. Launches the Add Recipe
     * Activity
     * @param view
     */
    public void addRecipe(View view) {
        Intent intent = new Intent(this, AddRecipeActivity.class);
        startActivity(intent);
    }

    /**
     * function called when the Custom Recipes button is clicked
     * @param view
     */
    public void showCustom(View view) {
        Log.d(TAG, "showCustom: clicked");
        Intent intent = new Intent(this, CustomListActivity.class);
        startActivity(intent);
    }
    
    public void foodGallery(View view) {
        Log.d(TAG, "foodGallery: clicked");
    }

    public void findFood(View view) {
        Log.d(TAG, "find Food: clicked");
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
