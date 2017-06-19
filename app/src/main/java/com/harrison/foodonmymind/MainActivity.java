package com.harrison.foodonmymind;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private TabLayout tabLayout;
    private ViewPager pager;


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
        Log.d(TAG, "handleSearchIntent: " + query);
//        first get all the checkbox preferences. and use that to help me determine what to return
//        as a search result
        boolean boxOne = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.preset_recipes), false);
        boolean boxTwo = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.custom_recipes), false);
        boolean boxThree = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.restaurants), false);
        Log.d(TAG, "handleSearchIntent: Preset -" + boxOne + ", Custom -" + boxTwo + ", Rest.-"
                + boxThree);
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
        }
        pager = (ViewPager) findViewById(R.id.pager);
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount()
                , this);
        pager.setAdapter(adapter);
        resetCheckBoxes();
    }

    /**
     * Helper method that resets the preferences to false. As we want them all to reset after every
     * search is completed
     */
    private void resetCheckBoxes() {
        editor.putBoolean(getString(R.string.preset_recipes), false);
        editor.putBoolean(getString(R.string.custom_recipes), false);
        editor.putBoolean(getString(R.string.restaurants), false);
        editor.commit();
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
