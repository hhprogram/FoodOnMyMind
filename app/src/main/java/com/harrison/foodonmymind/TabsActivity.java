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
    private int numTabs;


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
//        below is a variable that records how many tabs I need to show. Before When i wasn't doing
//        this and just was manually adding tabs to the tabLayout I could then instantiate a
//        pagerAdapter using the TabLayout.getTabCount() method but since I am not manually adding
//        tabs anymore that kept returning zero thus needed to pass in number of tabs another way
        numTabs = 0;
        if (boxOne) {
            numTabs++;
        }
        if (boxTwo) {
            numTabs++;
        }
        if (boxThree) {
            numTabs++;
        }
        if (!boxOne && !boxTwo && ! boxThree) {
            editor = pref.edit();
            editor.putBoolean(getString(R.string.preset_recipes), true);
            editor.putBoolean(getString(R.string.custom_recipes), true);
            editor.putBoolean(getString(R.string.restaurants), true);
            editor.commit();
        }
//        is no check boxes are checked then assume the user wanted to search all three and thus
//        want to create all tabs
        /** took out below piece of code as using tabLayout.setupWithViewPager() just overrides the
//         * manually construction of the tabLayout - ie adding the tabs with the set texts.
//         therefore when I had this and didn't update the pagerAdapter getTitle() method I had the
//         tabs working and working after being clicked or swiped but no title text

//        }
//        if (boxOne) {
//            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.preset_recipes)));
//        }
//        if (boxTwo) {
//            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.custom_recipes)));
//        }
//        if (boxThree) {
//            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.restaurants)));
//
         **/
        pager = (ViewPager) findViewById(R.id.pager_tab);
//        i'm using the setupWithViewPager as this is a much easier way to get the tab layout look
//        i want but also have the pages / views change when I either swipe or click on the tab
//        title.
        tabLayout.setupWithViewPager(pager);
//        do i need this PageChangeListener??? (the swiping seems to work without it. Do i only need
//        this if I want to do something extra when changing tabs?
//        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                pager.getChildAt(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
        PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), numTabs, this);
        pager.setAdapter(adapter);
//        then reset the checkboxes as if we go back to the mainActivity and then don't touch the
//        checkboxes at all then the preferences will be saved and give an incorrect query.
//        note we have to do this after the declaration of teh PageAdapter as the PageAdapter uses
//        the SharedPreferences to record the boolean values associated with whether or not the
//        boxes are checked. but after the declaration we keep them in variables in the PageAdapter
//        so can then changed the preferences after
        Log.d(TAG, "Before reset: " + pref.getString(getString(R.string.user_search), null));
    }


}
