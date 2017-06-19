package com.harrison.foodonmymind;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by harrison on 6/19/17.
 * A cursor adapter to show the tab when searching the custom recipes that user has created
 * themselves (persistent data)
 */

public class DBAdapter extends CursorAdapter {

    private Context mContext;
    private LayoutInflater inflater;

    public DBAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int img_col = cursor.getColumnIndex(mContext.getString(R.string.col_img));
        int title_col = cursor.getColumnIndex(mContext.getString(R.string.col_title));
        TextView title = (TextView)view.findViewById(R.id.item_title);
        title.setText(cursor.getString(title_col));
        TextView label = (TextView)view.findViewById(R.id.item_num);
        label.setText(Integer.toString(cursor.getPosition()+1)
                + context.getString(R.string.label_end));
        ImageView img = (ImageView)view.findViewById(R.id.item_img);
        Picasso.with(context)
                .load(cursor.getString(img_col))
                .into(img);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.search_result, parent, false);
        return view;
    }
}
