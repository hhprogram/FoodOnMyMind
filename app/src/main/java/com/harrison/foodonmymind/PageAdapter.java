package com.harrison.foodonmymind;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 6/13/17.
 */

public class PageAdapter extends FragmentStatePagerAdapter {

    int tabCount;

    public PageAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int tab) {
        switch (tab) {
            case 0:
                return new custom_recipe_fragment();
            case 1:
                return new preset_recipe_fragment();
            case 2:
                return new restaurant_fragment();
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
