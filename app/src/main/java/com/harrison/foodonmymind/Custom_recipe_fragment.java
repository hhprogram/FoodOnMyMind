package com.harrison.foodonmymind;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    String like;
    String or;
    String percent;
    String ingr_col;
    String title_col;
    String dir_col;
    ListView custom_list;
    CoordinatorLayout recipe_layout;

    public Custom_recipe_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recipe_layout = (CoordinatorLayout) inflater.inflate(R.layout.search_result, container
                , false);
        custom_list = (ListView) recipe_layout.findViewById(R.id.custom_list);
        return recipe_layout;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Bundle bundle = getArguments();
        query = null;
//        no bundle if launched via the custom recipes button. need to do the check or else will get
//        an error
        if (bundle != null) {
            query = getArguments().getString(getContext().getString(R.string.user_search), null);
            like = getString(R.string.db_like);
            or = getString(R.string.db_or);
            percent = getString(R.string.db_percent);
            ingr_col = getString(R.string.col_ingr);
            title_col = getString(R.string.col_title);
            dir_col = getString(R.string.col_desc);
        }
        getLoaderManager().initLoader(CURSOR_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
//        if no cursor data then we just revert to the webAdapter and create an info object
//        manually and just fill it with one infoItem that denotes that is empty (ie no results that
//        match this query
        if (!data.moveToFirst()) {
            Log.d(TAG, "onLoadFinished: no custom recipes that match that search");
//            Info info = new Info(Info.RECIPE, getContext());
//            info.addNoResult();
//            WebAdapter backupAdapter = new WebAdapter(getContext(), info.getData());
//            custom_list.setAdapter(backupAdapter);
        } else {
            adapter = new DBAdapter(getContext(), data, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            custom_list.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    data.moveToPosition(i);
                    int rowId = data.getInt(0);
                    Uri recipe = foodContract.buildFoodUri(foodContract.CustomRecipes.TABLE_NAME,
                            rowId);
                    Intent intent = new Intent(getActivity(), CustomRecipeActivity.class);
                    getActivity().startActivity(intent);
                }
            });
            custom_list.setAdapter(adapter);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        select_clause and select_args initialized to null as if query is null then that means
//        this fragment being launched via the button and thus no filtering and just should list
//        all the custom recipes in the database
        String select_clause = null;
        String[] select_args = null;
        if (query != null) {
            //        the selection clause (for WHERE clause). I.e ingredients=? or title=? or directinons=?
            //        note: i changed this as putting '%' characters in the selection clause with the ? does
            //        not give a valid SQL statement. So just do normal statement with ?'s and then in the
            //        selection args array just tack on % characters before and after to get the LIKE
            //        anything that contains the QUERY string in there
            select_clause = ingr_col + " LIKE ? OR " + title_col + " LIKE ? OR " + dir_col +
                    " LIKE ?";
            //        need an entry for each ? in selection string
            select_args[0] = "%" + query + "%";
            select_args[1] = "%" + query + "%";
            select_args[2] = "%" + query + "%";
        }
        Uri custom = foodContract.buildFoodUri(foodContract.CustomRecipes.TABLE_NAME);
        return new CursorLoader(getContext(), custom, null, select_clause, select_args, null);
//        this is creating a cursor that will point to a set of rows that satisfies:
//        SELECT * WHERE ingredients LIKE %<query>% OR title LIKE %<query>% OR
//              directions LIKE %<query>%
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        doing a null check as if there are no custom recipes then adapter isn't initialized and
//        thus the adapter will be null and adapter.swapCursor will give an error because this
//        is a null object reference
        if (adapter != null) {
            adapter.swapCursor(null);
        }
    }


}
