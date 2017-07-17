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
    Custom_recipe_fragment custom;
    Preset_recipe_fragment preset;
    Restaurant_fragment restaurant;
    Context mContext;
    boolean custom_box, preset_box, rest_box;
    SharedPreferences prefs;
    Bundle bundle;
    String user_query;
    JSONObject recipe_obj, rest_obj;
//    this used to record which fragments have gone where. First position denotes the index of which
//    tab it has been created. And -1 if the fragment not needed. Need this so that we don't just
//    create the same fragment over and over again
//    ex) if we want the custom and restaurant fragments, positions = {0, -1, 1}
    int[] positions;

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
        positions = new int[]{-1, -1, -1};
    }

    @Override
    public Fragment getItem(int tab) {

//        each case will have a check seeing if the fragment object has already been declared and if
//        it has then just reshow that layout with the cached data (no need to re-run the web
//        queries)
        switch (tab) {
            case 0:
                Log.d(TAG, "getItem: 0");
                if (custom_box && custom == null) {
                    custom = new Custom_recipe_fragment();
                    custom.setArguments(bundle);
                    positions[0] = 0;
                    return custom;
                }
                if (preset_box && preset == null) {
                    preset = new Preset_recipe_fragment();
                    preset.setArguments(bundle);
                    positions[1] = 0;
                    return preset;
                    }
                if (rest_box && restaurant == null) {
                    restaurant = new Restaurant_fragment();
                    restaurant.setArguments(bundle);
                    positions[2] = 0;
                    return restaurant;
                }
            case 1:
                Log.d(TAG, "getItem: 1");
                if (custom_box && custom == null) {
                    custom = new Custom_recipe_fragment();
                    custom.setArguments(bundle);
                    positions[0] = 1;
                    return custom;
                }
                if (preset_box && preset == null) {
                    preset = new Preset_recipe_fragment();
                    preset.setArguments(bundle);
                    positions[1] = 1;
                    return preset;
                }
                if (rest_box && restaurant == null) {
                    restaurant = new Restaurant_fragment();
                    restaurant.setArguments(bundle);
                    positions[2] = 1;
                    return restaurant;
                }
            case 2:
                Log.d(TAG, "getItem: 2");
                if (custom_box && custom == null) {
                    custom = new Custom_recipe_fragment();
                    custom.setArguments(bundle);
                    positions[0] = 2;
                    return custom;
                }
                if (preset_box && preset == null) {
                    preset = new Preset_recipe_fragment();
                    preset.setArguments(bundle);
                    positions[1] = 2;
                    return preset;
                }
                if (rest_box && restaurant == null) {
                    restaurant = new Restaurant_fragment();
                    restaurant.setArguments(bundle);
                    positions[2] = 2;
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
}
