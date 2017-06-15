package com.harrison.foodonmymind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by harrison on 6/13/17.
 * Adapter class for web queries (non-persistent data)
 */

public class WebAdapter extends ArrayAdapter<HashMap<String, String>>{


    private Context mContext;
//    instance variable to reference the list of hashmaps that will be passed into this adapter
    private ArrayList<HashMap<String, String>> results;
//    the instance inflater that will be used to inflate the layout for each view in this adapter
    private LayoutInflater inflater;

    public WebAdapter(Context context, ArrayList<HashMap<String, String>> results) {
        super(context, R.layout.list_item, results);
        this.mContext = context;
        this.results = results;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }
        return convertView;
    }
}
