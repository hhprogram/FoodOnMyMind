package com.harrison.foodonmymind;


import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class Custom_recipe_fragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    WebAdapter adapter;
    int CURSOR_ID = 0;

    public Custom_recipe_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_result, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        getLoaderManager().initLoader(CURSOR_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swap
    }


    @Override
    public void onTaskCompletion() {

    }

}
