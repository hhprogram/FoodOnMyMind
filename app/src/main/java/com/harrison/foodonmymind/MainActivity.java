package com.harrison.foodonmymind;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        }
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
        boolean boxOne = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.preset_recipes), false);
        boolean boxTwo = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.custom_recipes), false);
        boolean boxThree = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.restaurants), false);
        if (boxThree) {
//            only initialize it if we need for searching restaurants
            geoDude = new Geocoder(this);
            decideDialog();
        } else {
            Intent tabIntent = makeTabIntent(boxOne, boxTwo, boxThree);
            startActivity(tabIntent);
        }

        Log.d(TAG, "handleSearchIntent: Preset -" + boxOne + ", Custom -" + boxTwo + ", Rest.-"
                + boxThree);
    }

    /**
     * helper function. Used so instead of copy and pasting the intent creating and put lines of
     * code just need to have one line that is redundant - which is calling this method
     * @return an intent that will be used to launch the TabActivity
     */
    private Intent makeTabIntent(boolean boxOne, boolean boxTwo, boolean boxThree) {
        Intent tabIntent = new Intent(this, TabsActivity.class);
        tabIntent.putExtra(getString(R.string.preset_recipes), boxOne);
        tabIntent.putExtra(getString(R.string.custom_recipes), boxTwo);
        tabIntent.putExtra(getString(R.string.restaurants), boxThree);
    }

    private void startLocationPermissionRequest() {


    }

    /**
     * helper function that shows a Dialog in which user either chooses (1) to use fine location,
     * in which they allow app to use fine location (2) deny location and manually enter location
     */
    private void decideDialog() {
        AlertDialog.Builder[] dialogs = setUpDialogs();
        AlertDialog.Builder home_dialog = dialogs[0];
        home_dialog.create();
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
        LayoutInflater inflater = this.getLayoutInflater();
//        getting the main layout view to put in in the 2nd argument of inflate as I want the dialog
//        view to be a child of the main activity view
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
//        this is inflating my dialog_layout to be the view in the MANUAL_INPUT dialog
        manual_input.setView(inflater.inflate(R.layout.dialog_layout, mainLayout));
//        make toast out here same rease as making manual_input dialog (context reason)
        final Toast toast = Toast.makeText(this, getString(R.string.invalid_addr)
                , Toast.LENGTH_LONG);
        final Toast toast_canel = Toast.makeText(this, getString(R.string.toast_cancel)
                , Toast.LENGTH_LONG);
//        now I am coding what will happen when the 'positive' action button is pressed in the
//        dialog box. Below in the OnClick we get the EditText field. Then we use the Geocoder
//        class to translate a string address or location name and it converts to an Address object
//        which will have a lat and lon. (getFromLocationName() 's 2nd arg is 1 as we want to limit
//        the return values from this class to at most one Address object). Then we will check
//        if Geocoder returned any Address objects - if not we will ask user to re-enter, if so
//        then we assume that these objects have lat and lons and just go ahead with search. note
//        the 2nd arg of setPositiveButton is a listener and we are doing an anonymous class
//        declaration for ease
        manual_input.setPositiveButton(getString(R.string.manual_confirm)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText input = (EditText)findViewById(R.id.addr_input);
                        ArrayList<Address> addr;
                        try {
                            addr = (ArrayList<Address>) geoDude.getFromLocationName(
                                    input.getText().toString(), 1);
                            if (addr != null && !addr.isEmpty()) {
                                popLatLon(addr.get(0).getLatitude(), addr.get(0).getLongitude());
                            } else {
                                toast.show();
                            }

                        } catch (IOException e) {
                            Log.d(TAG, "onClick: Exception when translation addr to lat lon");
                            toast.show();
                        }

                    }});
//        if the user cancels then we want to dismuss the current dialog and go back one dialog
//        and create the home_dialog again
        manual_input.setNegativeButton(getString(R.string.manual_cancel)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toast_canel.show();
                        dialogInterface.cancel();
                        dialog_builder.create();
            }
        });
        String[] choices = new String[]{getString(R.string.gps_loc)
                , getString(R.string.manual_loc)};
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        startLocationPermissionRequest();
                    case 1:
                        dialog.cancel();
                        manual_input.create();
                }
            }
        };
        dialog_builder.setSingleChoiceItems(choices, -1, listener);
        return new AlertDialog.Builder[]{dialog_builder, manual_input};
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
        editor.putBoolean(getString(R.string.custom_recipes), box);
        editor.commit();
    }

    public void onBoxThreeChecked (View view) {
        Log.d(TAG, "onBoxThreeChecked: Clicked");
        boolean box = ((CheckBox) view).isChecked();
        editor.putBoolean(getString(R.string.restaurants), box);
        editor.commit();
    }

}
