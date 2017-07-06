package com.harrison.foodonmymind;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * This is the activity used to host the layout with the pageAdapter tabs. Created from the Main
 * Activity after the MainActivity has processed the search intent
 */

public class TabsActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private TabLayout tabLayout;
    private ViewPager pager;


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
