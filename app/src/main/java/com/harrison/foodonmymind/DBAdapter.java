package com.harrison.foodonmymind;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

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
        String image_path = cursor.getString(img_col); //TBD try a URI instead y a string to load successfully using picasso
        if (image_path.equals("")) {
//            see CustomRecipeActivity around line 100 for logic behind this string. It is string of
//            path to a  mipmap resource
            image_path = mContext.getString(R.string.mipmap_uri_base) + mContext.getPackageName() +"/"+ R.mipmap.recipe_default;
        }
        Log.d(TAG, "bindView: " + image_path);
        Picasso.with(context)
                .load(image_path)
                .into(img);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return view;
    }
}
