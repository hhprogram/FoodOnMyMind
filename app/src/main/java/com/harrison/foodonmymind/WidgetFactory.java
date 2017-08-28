package com.harrison.foodonmymind;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.harrison.foodonmymind.data.foodContract;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 8/22/17.
 */

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory{

    Context mContext;
    Cursor mCursor;
    String display_mode;

    public WidgetFactory(Context context, Intent intent) {
        mContext = context;
        Log.d(TAG, "WidgetFactory: initialized");
    }

    @Override
    public void onCreate() {
//        get a cursor for all quotes in the DB currently - not sure if this is best way but using
//        this cursor to update the widget (can I leverage the adapter somehow?
        Log.d(TAG, "onCreate: WidgetFactory");
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        Log.d(TAG, "onDataSetChanged: WidgetFactory");
//        just querying the whole DB to get all custom recipes. How does it notify when the cursor
//        has been returned?
        mCursor = mContext.getContentResolver().query(foodContract.CustomRecipes.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onDestroy() {
//        disconnect the cursor as no longer needed
        mCursor = null;
    }

    //    note this has to return the actual value that corresponds to the number of 'views' (ie number
//    of items in the collection (list) of items to be shown in this widget in order for anything to
//    show up. Defaults to zero and if have zero it will never call getViewAt
    @Override
    public int getCount() {
//        gets the number of rows attached to this cursor (ie the number of stock symbols in our
//        DB)
        return mCursor.getCount();
    }

    //    similar to the StockAdapter's onBindViewHolder method. Basically, is called to populate each
//    different item in our collection widget
    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.custom_list_item);
        if (!mCursor.moveToFirst()) {
            Log.d(TAG, "getViewAt: nothing in cursor for widget factory");
        }
        Log.d(TAG, "getViewAt: Trying to get views now");
        if (mCursor.moveToPosition(i)) {
            int col_num = mCursor.getColumnIndex(foodContract.CustomRecipes.TITLE);
            view.setTextViewText(R.id.widget_list_item, mCursor.getString(col_num));
        }
        return view;
    }

    //    custom placeholder that shows a custom loading view in the interim when the actual view
//    returned by getViewAt
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    //note when this just returns 0 then all i get is a 'loading..." in each view but when set to 1
//    a proper view shows up
    @Override
    public int getViewTypeCount() {
        return 1    ;
    }

    //    should return the unique ID of the row of the item at position i in my cursor
    @Override
    public long getItemId(int i) {
        return 0;
    }

    //  telling me if the IDs returned in getItemID are unique and no 2 IDs refer to same thing.
//    ie changes to underlying data won't impact ID mapping
    @Override
    public boolean hasStableIds() {
        return false;
    }


}
