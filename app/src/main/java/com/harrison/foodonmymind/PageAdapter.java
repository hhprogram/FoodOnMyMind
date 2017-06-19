package com.harrison.foodonmymind;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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

public class PageAdapter extends FragmentStatePagerAdapter {

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

    public PageAdapter(FragmentManager fm, int tabCount, Context context) {
        super(fm);
        this.tabCount = tabCount;
        this.mContext = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        user_query = prefs.getString(mContext.getString(R.string.user_search),null);
        bundle = new Bundle();
        bundle.putString(mContext.getString(R.string.user_search), null);
        custom_box = prefs.getBoolean(mContext.getString(R.string.custom_recipes), false);
        preset_box = prefs.getBoolean(mContext.getString(R.string.preset_recipes), false);
        rest_box = prefs.getBoolean(mContext.getString(R.string.restaurants), false);

    }

    @Override
    public Fragment getItem(int tab) {

//        each case will have a check seeing if the fragment object has already been declared and if
//        it has then just reshow that layout with the cached data (no need to re-run the web
//        queries)
        switch (tab) {
            case 0:
                if (custom == null) {
                    custom = new Custom_recipe_fragment();
                    custom.setArguments(bundle);
                    return custom;
                } else {

                }
            case 1:
                if (preset == null) {
                    preset = new Preset_recipe_fragment();
                    preset.setArguments(bundle);
                    return preset;
                } else {

                }
            case 2:
                if (restaurant == null) {
                    restaurant = new Restaurant_fragment();
                    restaurant.setArguments(bundle);
                    return restaurant;
                } else {

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
