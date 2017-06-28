package com.harrison.foodonmymind;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

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

//    using the ViewHolder paradigm (per suggestions seen online).
//    https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
//    basically this helps cache the findViewById() methods so that I don't have to call this
//    method everytime I want to populate a view with content. Can keep cached references to
//    the specific text and image views in each View used by getView. Then I use this ViewHolder
//    object and put it as a tag for the convertView view. And a tag just stores some additional
//    info related to that view to help. And we then get that tag (viewHolder instance object) and
//    then can quickly reference the specific text and image views and populate them as needed
    private static class ViewHolder {
        TextView label;
        TextView title;
        ImageView img;
    }

    public WebAdapter(Context context, ArrayList<Info.InfoItem> results) {
        super(context, R.layout.list_item, results);
        this.mContext = context;
        this.results = results;
        this.inflater = LayoutInflater.from(context);
        for (Info.InfoItem item : results) {
            Log.d(TAG, "WebAdapter: " + item.getTitle());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        need this to be outside the if-else structure as I put it inside before and what happened
//        was that the listView would be showing 30 listView items but would just be repeating
//        items 1-7 (even though there were 30 unique list items that I wanted shown). What happened
//        was that Android OS would make a new view for the first 7, and then to save memory start
//        rececyling these view objects for the items 8-30 but since I had this results.get()
//        code within the if (convertView == null) block so I was only getting the actual relevant
//        info from the RESULTS array when i was creating a brand new view, thus when Android
//        started to reuse existing views that already had content on them this code wouldn't be
//        executed and I was just returning the view and thus just reshowing the same content that
//        was on it before
        Info.InfoItem item = results.get(position);
//       see above comments near the ViewHolder static class lines
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder.label = (TextView)convertView.findViewById(R.id.item_num);
            holder.title = (TextView)convertView.findViewById(R.id.item_title);
            holder.img = (ImageView) convertView.findViewById(R.id.item_img);
            convertView.setTag(holder);
            Log.d(TAG, "getView: " + item.getTitle());
        } else {
//            if recycling a view object then get the tag info from this view to then be used
//            to reference the inner views
            holder = (ViewHolder) convertView.getTag();
        }
        holder.label.setText(String.format(Locale.US, "%d .)",position+1));
        holder.title.setText(item.getTitle());
        Picasso.with(mContext)
                .load(item.getImgUrl())
                .into(holder.img);
        return convertView;
    }
}
