package com.harrison.foodonmymind;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by harrison on 6/13/17.
 * Adapter associated with persisent data (in the db)
 */

public class DbAdapter extends CursorAdapter {

    private LayoutInflater inflater;

    public DbAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        inflater = LayoutInflater.from(context);
    }

    /**
     * This is the method that actually 'inserts' content that will be seen in the VIEW
     * @param view - view to be populated with data
     * @param context - context that is calling this
     * @param cursor - the cursor that will be used to populate the view
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    /**
     * This is the method that actually creates a new view object to be populated
     * @param context- context that is calling this
     * @param cursor - the cursor that will be used to populate the view
     * @param parent - the parent view of the new view being created
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return view;
    }
}
