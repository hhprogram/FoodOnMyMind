package com.harrison.foodonmymind;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.harrison.foodonmymind.data.foodContract;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class Custom_recipe_fragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    DBAdapter adapter;
    int CURSOR_ID = 0;
    String query;
    String like = getString(R.string.db_like);
    String or = getString(R.string.db_or);
    String percent = getString(R.string.db_percent);
    String db_var = "?";
    String ingr_col = getString(R.string.col_ingr);
    String title_col = getString(R.string.col_title);
    String dir_col = getString(R.string.col_desc);

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
        query = getArguments().getString(getContext().getString(R.string.user_search), null);
        getLoaderManager().initLoader(CURSOR_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ListView view = (ListView)getActivity().findViewById(R.id.search_result);
//        if no cursor data then we just revert to the webAdapter and create an info object
//        manually and just fill it with one infoItem that denotes that is empty (ie no results that
//        match this query
        if (!data.moveToFirst()) {
            Log.d(TAG, "onLoadFinished: no custom recipes that match that search");
            Info info = new Info(Info.RECIPE, getContext());
            info.addNoResult();
            WebAdapter backupAdapter = new WebAdapter(getContext(), info.getData());
            view.setAdapter(backupAdapter);
        } else {
            adapter = new DBAdapter(getContext(), data, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            view.setAdapter(adapter);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        the selection clause (for WHERE clause). I.e ingredients=? or title=? or directinons=?
        String select_clause = ingr_col + " " + like + " " + percent + db_var + percent
                + or + " " + title_col + " " + like + " " + percent + db_var + percent
                + or + " " + dir_col + " " + like + " " + percent + db_var + percent;
        String[] select_args = {query};
        Uri custom = foodContract.buildFoodUri(foodContract.CustomRecipes.TABLE_NAME);
        return new CursorLoader(getContext(), custom, null, select_clause, select_args, null);
//        this is creating a cursor that will point to a set of rows that satisfies:
//        SELECT * WHERE ingredients LIKE %<query>% OR title LIKE %<query>% OR
//              directions LIKE %<query>%
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


}
