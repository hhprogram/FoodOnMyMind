package com.harrison.foodonmymind.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by harrison on 6/12/17.
 */

public class foodProvider extends ContentProvider {
    private dbHelper mDBhelper;
    private UriMatcher mUriMatcher = buildUriMatcher();
    static final int FAVORITES = 100;
    static final int CUSTOM = 101;
    static final int RESTAURANTS = 102;


    //When we create a content provider we need to create the associated DBhelper instance object
    //so we can interact with the underlying DB and make sure the table actually is created first
    //if this is the first time creating it. Use this helper instance to get instance objects
    //of the database so we can actually interact with it via query, insert etc...
    @Override
    public boolean onCreate() {
        mDBhelper = new dbHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(foodContract.CONTENT_AUTHORITY, foodContract.CustomRecipes.TABLE_NAME, CUSTOM);
        matcher.addURI(foodContract.CONTENT_AUTHORITY, foodContract.Favorites.TABLE_NAME, FAVORITES);
        matcher.addURI(foodContract.CONTENT_AUTHORITY, foodContract.Restaurants.TABLE_NAME, FAVORITES);
        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return ContentResolver.CURSOR_DIR_BASE_TYPE;
            case CUSTOM:
                return ContentResolver.CURSOR_DIR_BASE_TYPE;
            case RESTAURANTS:
                return ContentResolver.CURSOR_DIR_BASE_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }
    }

    /**
     * Method that must be implemented when extending the contentProvider class. Updates the content
     * provider's current data (ie changing the values in some rows and in some columns). Assumes
     * the values is filled out like the user intended (ie does no checks)
     * Only in these methods do we access the db directly / make an actual instance object of a
     * SQLiteDatabase to alter the existing db
     * @param uri - the URI denotes which table needs updating
     * @param values - the new values to be put. ContentValues is basically a wrapper class of
     *               HashMap. Basically the contentValues is a HashMap, in its entirety is just
     *               one row of data. Each key should be a column Name of the table and each value
     *               associated with their key should be a value that pertains to that specific row
     *               of data for that column
     * @param selection - the string that is the SQL clause for what to filter for
     * @param selectionArgs - the string array where each value is a column and in order replaces
     *                      the '?''s in the SELECTION parameter
     * @return an int value which is the number of rows that have been updated
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsupdated = 0;
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                rowsupdated = db.update(foodContract.Favorites.TABLE_NAME, values, selection
                        , selectionArgs);
                break;
            case CUSTOM:
                rowsupdated = db.update(foodContract.CustomRecipes.TABLE_NAME, values, selection
                        , selectionArgs);
                break;
            case RESTAURANTS:
                rowsupdated = db.update(foodContract.Restaurants.TABLE_NAME, values, selection
                        , selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:"+uri);
        }
        if (rowsupdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsupdated;
    }

    /**
     * required method to override when extending contentProviders. Note: the SQLITEDATABASE
     * insert() method returns a long ID
     * Only in these methods do we access the db directly / make an actual instance object of a
     * SQLiteDatabase to alter the existing db
     * @param uri - the URI to the location of the database and table that we want to insert into
     *            note this URI is just necessarily the exact URI to use to find the actual location
     *            of the database. Just the URI to tell the content provider where to look. A URI
     *            used by the App to communicate with movieProvider (can be only of the form
     *            given by the buildUri methods in movieContract)
     * @param values - the values in the columns that we want to insert of this row of data
     * @return the URI that points to the new row's location in the table
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
//        this is the variable that the database returns when you do an insert. It will return the
//        row id that it inserted at (-1 if insert not successful). Will use this unique rowid
//        as the unique identifier to append to the table name URI as the URI to return from this
//        method as the location of the newly inserted row
        long rowId;
        Uri insertedRow;
        switch (match) {
            case FAVORITES:
                rowId = db.insert(foodContract.Favorites.TABLE_NAME, null, values);
                insertedRow = foodContract.buildFoodUri(foodContract.Favorites.TABLE_NAME, rowId);
                break;
            case CUSTOM:
                rowId = db.insert(foodContract.CustomRecipes.TABLE_NAME, null, values);
                insertedRow = foodContract.buildFoodUri(foodContract.Favorites.TABLE_NAME, rowId);
                break;
            case RESTAURANTS:
                rowId = db.insert(foodContract.Restaurants.TABLE_NAME, null, values);
                insertedRow = foodContract.buildFoodUri(foodContract.Favorites.TABLE_NAME, rowId);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return insertedRow;
    }


    /**
     * method to perform a batch of inserts, more efficient than doing multiple individual inserts.
     * More efficient as do in one DB 'transaction' allowing for faster writing. Vs. individual
     * inserts would have to do a transaction for each insert
     * @param uri - the uri in which we want to do the bulkinsert (ie the table we want to insert)
     * @param values - the row values we want inserted. An array of contentvalues with each
     *               contentvalues element is a row
     * @return number of rows inserted in the db
     */
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        int rowsInserted = 0;
        int match = mUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
//                use 'transactions' as since we are committing potentially rows (committing is
//                inserting rows and updating the db to reflect these changes) we will just do it
//                in one 'commit'. Ie we commit an insert only once we close the transaction.
//                So we insert all the rows and then do one bulk commit. Beneficial for data
//                integrity (b/c if crash or lose connection mid bulk insert the db will revert
//                back to the state before the bulk insert started so that we don't have a db
//                that now has half of the new rows). Also, speed since one bulk commit better than
//                doing a db commit per row inserted
                db.beginTransaction();
                for (ContentValues value: values) {
                    db.insert(foodContract.Favorites.TABLE_NAME, null, value);
                    rowsInserted++;
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            case CUSTOM:
                db.beginTransaction();
                for (ContentValues value: values) {
                    db.insert(foodContract.CustomRecipes.TABLE_NAME, null, value);
                    rowsInserted++;
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            case RESTAURANTS:
                db.beginTransaction();
                for (ContentValues value: values) {
                    db.insert(foodContract.Restaurants.TABLE_NAME, null, value);
                    rowsInserted++;
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }
        if (rowsInserted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
    }


    /**
     * Only in these methods do we access the db directly / make an actual instance object of a
     * SQLiteDatabase to alter the existing db
     * Only in these methods do we access the db directly / make an actual instance object of a
     * SQLiteDatabase to alter the existing db
     * @param uri - the URI given by app that will be used by provider to tell which pieces of data
     *            should be deleted in actual DB
     * @param selection - the clause in SQL that will be used to delete on given rows
     * @param selectionArgs - the selection criteria by which to delete rows
     * @return the number of rows deleteds
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        SQLiteDatabase db = mDBhelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                rowsDeleted = db.delete(foodContract.Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            case CUSTOM:
                rowsDeleted = db.delete(foodContract.Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            case RESTAURANTS:
                rowsDeleted = db.delete(foodContract.Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:"+ uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    /**
     *
     * @param uri- the URI given by app that will be used by provider to tell which pieces of data
     *            should be deleted in actual DB
     * @param projection - the specific columns that we want returned
     * @param selection - the clause in SQL that will be used to delete on given rows
     * @param selectionArgs - the selection criteria by which to delete rows
     * @param sortOrder - the sort order of the rows
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs
            , String sortOrder) {
        int match = mUriMatcher.match(uri);
        SQLiteDatabase db = mDBhelper.getReadableDatabase();
        Cursor pointer;
        switch (match) {
            case FAVORITES:
                pointer = db.query(foodContract.Favorites.TABLE_NAME, projection, selection
                        , selectionArgs, null, null, sortOrder);
                break;
            case CUSTOM:
                pointer = db.query(foodContract.CustomRecipes.TABLE_NAME, projection, selection
                        , selectionArgs, null, null, sortOrder);
                break;
            case RESTAURANTS:
                pointer = db.query(foodContract.Restaurants.TABLE_NAME, projection, selection
                        , selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }
//        this allows the cursor to know what ContentProvider uri it was created for and when we
//        use a CursorLoader (in one of the activities, it registers a "ForceLoadContentObserver"
//        (its cursor) and if we have called setNotificationUri then the CursorLoader will register
//        the observer to be this cursor associated with this query and thus if the underlying db
//        is ever changed (like in update, delete with notifychange() method) then this cursor as
//        as observer will be notified and either update itself or do something else
        pointer.setNotificationUri(getContext().getContentResolver(), uri);
        return pointer;
    }

}