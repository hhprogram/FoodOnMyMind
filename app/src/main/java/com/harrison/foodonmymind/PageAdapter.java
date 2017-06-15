package com.harrison.foodonmymind;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 6/13/17.
 */

public class PageAdapter extends FragmentStatePagerAdapter {

    int tabCount;
    Custom_recipe_fragment custom;
    Preset_recipe_fragment preset;
    Restaurant_fragment restaurant;
    Context mContext;
    boolean custom_box, preset_box, rest_box;
    JSONObject recipe_obj, rest_obj;

    public PageAdapter(FragmentManager fm, int tabCount, Context context) {
        super(fm);
        this.tabCount = tabCount;
        this.mContext = context;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String user_query = prefs.getString(mContext.getString(R.string.user_search),null);
        custom_box = prefs.getBoolean(mContext.getString(R.string.custom_recipes), false);
        preset_box = prefs.getBoolean(mContext.getString(R.string.preset_recipes), false);
        rest_box = prefs.getBoolean(mContext.getString(R.string.restaurants), false);
        if (preset_box) {
            recipe_obj = WebTask
        }
    }

    @Override
    public Fragment getItem(int tab) {

        switch (tab) {
            case 0:
                if (custom == null) {
                    custom = new Custom_recipe_fragment();
                    custom.setArguments();
                }
                return new Custom_recipe_fragment();
            case 1:
                return new Preset_recipe_fragment();
            case 2:
                return new Restaurant_fragment();
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
