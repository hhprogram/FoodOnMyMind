package com.harrison.foodonmymind;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.harrison.foodonmymind.data.foodContract;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class CustomRecipeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

//    uri location of the custom recipe we want to specifically look at
    Uri recipeLocation;
    LayoutInflater inflater;
    int numImages;
    ArrayList<String> imageList;
    CustomRecipeImageSlider adapter;
    int CURSOR_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_recipe);
        recipeLocation = getIntent().getParcelableExtra(getString(R.string.recipe_uri));
        inflater = getLayoutInflater();
        adapter = new CustomRecipeImageSlider(getSupportFragmentManager());
//        note since this is an Activity class need to class getSupportLoaderManager vs just
//        getLoaderManager for app.v4 loaderManager. I think if I used just
//        android.app.LoaderManager then could use just getLoaderManager
        getSupportLoaderManager().initLoader(CURSOR_ID, null, this);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            Log.d(TAG, "Cursor has data");
            int title_col = data.getColumnIndex(foodContract.CustomRecipes.TITLE);
            int steps_col = data.getColumnIndex(foodContract.CustomRecipes.DESC);
            int ingredients_col = data.getColumnIndex(foodContract.CustomRecipes.INGREDIENTS);
            int image_col = data.getColumnIndex(foodContract.CustomRecipes.IMG_KEY);
            String title = data.getString(title_col);
            String steps = data.getString(steps_col);
            String ingredients = data.getString(ingredients_col);
            String imagePaths = data.getString(image_col);
            ArrayList<String> ingredientsList = new ArrayList<String>(Arrays.asList(ingredients.split(",")));
            ArrayList<String> stepsList = new ArrayList<String>(Arrays.asList(steps.split(",")));
            imageList = new ArrayList<String>(Arrays.asList(imagePaths.split(",")));
            TextView titleView = (TextView) findViewById(R.id.recipe_title);
            titleView.setText(title);
//            note the secondn argument to arrayAdapter constructor is the layout to be used for
//            each item in the list view. Thus easy to assign this adapter to a listView that is
//            a child of some other view as just make the item view layout a separate xml and then
//            get the listView using findViewById and set this adapter
            ArrayAdapter ingredientsAdapter = new ArrayAdapter(this, R.layout.custom_list_item,
                    ingredientsList);
            ArrayAdapter stepsAdapter = new ArrayAdapter(this, R.layout.custom_list_item,
                    stepsList);
            ListView ingredientsView = (ListView) findViewById(R.id.custom_ingredients);
            ListView stepsView = (ListView) findViewById(R.id.custom_directions);
            ingredientsView.setAdapter(ingredientsAdapter);
            stepsView.setAdapter(stepsAdapter);
            numImages = imageList.size();
            ViewPager pager = (ViewPager)findViewById(R.id.custom_pictures);
            pager.setAdapter(adapter);
        } else {
            Log.d(TAG, "onLoadFinished: no data for some reason");
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, recipeLocation, null, null, null, null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

//doing just an inner class as won't be used other than with this custom class adapter and the code
//    for this adapter is pretty straight forward and limited so better for organization
    private class CustomRecipeImageSlider extends FragmentStatePagerAdapter {

//        any class extending FragmentStatePagerAdapter requires to implement this constructor
        public CustomRecipeImageSlider(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Custom_image_fragment fragment = new Custom_image_fragment();
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.image_index), imageList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return numImages;
        }
    }
}
