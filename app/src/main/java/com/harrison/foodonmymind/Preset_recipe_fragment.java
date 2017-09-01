package com.harrison.foodonmymind;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

/**

 */
public class Preset_recipe_fragment extends Fragment implements AsyncListener{

    Info info;
    WebAdapter adapter;
    ListView listView;
    View rootView;


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
        rootView = inflater.inflate(R.layout.search_result_online_recipe, container);
        listView = (ListView) rootView.findViewById(R.id.list_online_recipe);
        return listView;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        WebTask task = new WebTask(this, getContext());
        String query = getArguments().getString(getContext().getString(R.string.user_search), null);
        String url = buildUrl(query);
        Log.d(TAG, "onCreate: search query" + query);
        Pair pair = new Pair(Info.RECIPE, url);
        try {
            info = task.execute(pair).get();
            Log.d(TAG, "onCreate: " + info.getData().size());
        } catch (InterruptedException e) {
            Log.d(TAG, "onCreate: task in preset recipe interrupted)");
        } catch (ExecutionException e) {
            Log.d(TAG, "onCreate: execution exception in preset recipe interrupted)");
        }
    }

    /**
     * helper function that builds actual api query url
     * @return String representation of that url
     */
    private String buildUrl(String user_query) {
        String base = getContext().getString(R.string.recipeSearchApi);
        String query = getContext().getString(R.string.ff_q_search) + user_query;
        String key = getContext().getString(R.string.api_key) + getContext().getString(R.string.ffApiKey);
        String url = Utilities.createUrl(base, query, key);
        return url;
    }


    @Override
    public void onTaskCompletion() {
        if (adapter == null) {
            adapter = new WebAdapter(getContext(), info.getData());
        } else {
            adapter.notifyDataSetChanged();
        }
        listView.setAdapter(adapter);
    }
}
