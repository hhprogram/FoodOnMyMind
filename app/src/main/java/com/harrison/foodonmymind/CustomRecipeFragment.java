package com.harrison.foodonmymind;


import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.harrison.foodonmymind.data.foodContract;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomRecipeFragment extends Fragment
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
    View rootView;

    public CustomRecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        note: I have to first inflate the whole view and then find the specific view that is the
//        listView and return that. Before I was just trying to return the inflated layout but
//        kept saying it couldn't cast a listView to a viewPager. And since we supply a 'root'
//        because we are supplying the rootview as viewPager then the view returned is that rootview
//        and if now rootview is supplied then the rootview of the xml file itself is used. therefore
//        usually could jsut inflate and the rootview of the xml file would be returned but since we
//        supply the view pager as a rootview then the view returned is the the supplied rootview
//        also needed to add the argument as false - I think because I don't want to add this layout
//        to the hiearchy but rather I'm only using this rootView to get to the rootview of the
//        xml which is the listview (?? need to confirm)
        rootView = inflater.inflate(R.layout.search_result, container, false);
        custom_list = (ListView) rootView.findViewById(R.id.custom_list);
//        note: had to return the rootView as it would give me errors if I returned the view that I
//        didn't inflate within the onCreateView.
        return rootView;
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
                    intent.putExtra(getString(R.string.recipe_uri), recipe);
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
//        note: changed from an array of fixed size because if i do a set array of size 3 and end up
//        not filling them then I will have null values and these null values passed through
//        the selection_args parameter in the db.query() function will cause an error. Therefore,
//        first create an arraylist so if query == null then I can convert an empty arraylist
//        just an empty array which will not cause any "bind value null" errors in the query
        ArrayList<String> select_args = new ArrayList<>();
        if (query != null) {
            //        the selection clause (for WHERE clause). I.e ingredients=? or title=? or directinons=?
            //        note: i changed this as putting '%' characters in the selection clause with the ? does
            //        not give a valid SQL statement. So just do normal statement with ?'s and then in the
            //        selection args array just tack on % characters before and after to get the LIKE
            //        anything that contains the QUERY string in there
            select_clause = ingr_col + " LIKE ? OR " + title_col + " LIKE ? OR " + dir_col +
                    " LIKE ?";
            //        need an entry for each ? in selection string
            select_args.add("%" + query + "%");
            select_args.add("%" + query + "%");
            select_args.add("%" + query + "%");
        }
        String[] select_args_array = select_args.toArray(new String[select_args.size()]);
        Uri custom = foodContract.buildFoodUri(foodContract.CustomRecipes.TABLE_NAME);
        Log.d(TAG, "onCreateLoader: custom uri" + custom);
        return new CursorLoader(getContext(), custom, null, select_clause, select_args_array, null);
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
