package com.harrison.foodonmymind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by harrison on 6/13/17.
 * Adapter class for web queries (non-persistent data)
 */

public class WebAdapter extends ArrayAdapter<Info.InfoItem>{


    private Context mContext;
//    instance variable to reference the list of hashmaps that will be passed into this adapter
    private ArrayList<Info.InfoItem> results;
//    the instance inflater that will be used to inflate the layout for each view in this adapter
    private LayoutInflater inflater;

    public WebAdapter(Context context, ArrayList<Info.InfoItem> results) {
        super(context, R.layout.list_item, results);
        this.mContext = context;
        this.results = results;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            Info.InfoItem item = results.get(position);
            TextView label = (TextView)convertView.findViewById(R.id.item_num);
            label.setText(Integer.toString(position+1) + mContext.getString(R.string.label_end));
            TextView title = (TextView)convertView.findViewById(R.id.item_title);
            title.setText(item.getTitle());
            ImageView img = (ImageView) convertView.findViewById(R.id.item_img);
            Picasso.with(mContext)
                    .load(item.getImgUrl())
                    .into(img);
        }
        return convertView;
    }
}
