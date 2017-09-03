package com.harrison.foodonmymind;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 6/13/17.
 * Page adapter that only creates desired tabs (ie doesn't create tabs that aren't associated with
 * checked checkboxes - unless all 3 boxes aren't checked then it searches all 3)
 * Passes the search query on to the fragments (where the actual web queries are performed) via
 * the setArguments() method
 */

public class PageAdapter extends FragmentPagerAdapter {

    int tabCount;
    CustomRecipeFragment custom;
    PresetRecipeFragment preset;
    RestaurantFragment restaurant;
    Context mContext;
    boolean custom_box, preset_box, rest_box;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Bundle bundle;
    String user_query;
    JSONObject recipe_obj, rest_obj;
//    this used to record which fragments have gone where. First position denotes the index of which
//    tab it has been created. And -1 if the fragment not needed. Need this so that we don't just
//    create the same fragment over and over again
//    ex) if we want the custom and restaurant fragments, positions = {0, -1, 1}
    String[] titles;

    public PageAdapter(FragmentManager fm, int tabCount, Context context) {
        super(fm);
        this.tabCount = tabCount;
        this.mContext = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        user_query = prefs.getString(mContext.getString(R.string.user_search),null);
        Log.d(TAG, "Page Adapter query: " + user_query);
        bundle = new Bundle();
        bundle.putString(mContext.getString(R.string.user_search), user_query);
        custom_box = prefs.getBoolean(mContext.getString(R.string.custom_recipes), false);
        preset_box = prefs.getBoolean(mContext.getString(R.string.preset_recipes), false);
        rest_box = prefs.getBoolean(mContext.getString(R.string.restaurants), false);
        resetCheckBoxes();
        Log.d(TAG, "PageAdapter: custom boolean: " + custom_box);
        Log.d(TAG, "PageAdapter: preset boolean: " + preset_box);
        Log.d(TAG, "PageAdapter: rest boolean: " + rest_box);
//        the below code block basically populates a String array (that will hold at most 3 strings
//        since have only at most 3 tabs. Thus based on what is checked
        int titlePosition = 0;
        titles = new String[3];
        if (custom_box) {
            titles[titlePosition] = mContext.getString(R.string.custom_recipes);
            titlePosition = titlePosition + 1;
        }
        if (preset_box) {
            titles[titlePosition] = mContext.getString(R.string.preset_recipes);
            titlePosition = titlePosition + 1;
        }
        if (rest_box) {
            titles[titlePosition] = mContext.getString(R.string.restaurants);
        }
    }


    @Override
    public Fragment getItem(int tab) {
        Log.d(TAG, "getItem: tab: " + tab);
//        each case will have a check seeing if the fragment object has already been declared and if
//        it has then just reshow that layout with the cached data (no need to re-run the web
//        queries). We do it like this because I need to first check if the user clicked on that
//        check box and then in order to not make not make duplicate fragments and show on each
//        tab need to check and see if the fragments are still null. ie if they aren't null then
//        it has been instantiated and is already being shown in another tab so skip it
        switch (tab) {
            case 0:
                Log.d(TAG, "getItem: 0");
                if (custom_box && custom == null) {
                    custom = new CustomRecipeFragment();
                    custom.setArguments(bundle);
                    return custom;
                }
                if (preset_box && preset == null) {
                    preset = new PresetRecipeFragment();
                    preset.setArguments(bundle);
                    return preset;
                    }
                if (rest_box && restaurant == null) {
                    restaurant = new RestaurantFragment();
                    restaurant.setArguments(bundle);
                    return restaurant;
                }
            case 1:
                Log.d(TAG, "getItem: 1");
                if (custom_box && custom == null) {
                    custom = new CustomRecipeFragment();
                    custom.setArguments(bundle);
                    return custom;
                }
                if (preset_box && preset == null) {
                    preset = new PresetRecipeFragment();
                    preset.setArguments(bundle);
                    return preset;
                }
                if (rest_box && restaurant == null) {
                    Log.d(TAG, "getItem: restaurant frag");
                    restaurant = new RestaurantFragment();
                    restaurant.setArguments(bundle);
                    return restaurant;
                }
            case 2:
                Log.d(TAG, "getItem: 2");
                if (custom_box && custom == null) {
                    custom = new CustomRecipeFragment();
                    custom.setArguments(bundle);
                    return custom;
                }
                if (preset_box && preset == null) {
                    preset = new PresetRecipeFragment();
                    preset.setArguments(bundle);
                    return preset;
                }
                if (rest_box && restaurant == null) {
                    restaurant = new RestaurantFragment();
                    restaurant.setArguments(bundle);
                    return restaurant;
                }
            default:
                Log.d(TAG, "getItem: Invalid tab ");
                return null;
        }
    }

    @Override
    public int getCount() {
        return this.tabCount;
    }


    /**
     * This method needs to be overridden because since I am doing the tabLayout and calling
     * setupWithViewPager() on it. This is a way to put titles on my pages as I can no longer do it
     * manually when I call the addTab() method
     * @param position - which position the viewPager is at. Will be used to grab corresponding
     *                 title
     * @return a string that is that page's title string
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    /**
     * Helper method that resets the preferences to false. As we want them all to reset after every
     * search is completed
     */
    private void resetCheckBoxes() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor;
        editor = pref.edit();
        editor.putBoolean(mContext.getString(R.string.preset_recipes), false);
        editor.putBoolean(mContext.getString(R.string.custom_recipes), false);
        editor.putBoolean(mContext.getString(R.string.restaurants), false);
        editor.commit();
    }
}
