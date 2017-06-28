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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_result, container, false);
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
        adapter = new WebAdapter(getContext(), info.getData());
        ListView lstView = (ListView)getActivity().findViewById(R.id.search_result);
        lstView.setAdapter(adapter);
    }
}
