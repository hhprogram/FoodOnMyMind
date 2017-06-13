package com.harrison.foodonmymind.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 6/12/17.
 */

public class dbHelper extends SQLiteOpenHelper{

    public static String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 2;

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String SQL_CREATE_CUSTOM = "CREATE TABLE "
                + foodContract.CustomRecipes.TABLE_NAME
                + " ("
                + foodContract.CustomRecipes._ID + " integer PRIMARY KEY AUTOINCREMENT"
                + foodContract.CustomRecipes.TITLE + " text NOT NULL"
                + foodContract.CustomRecipes.DESC + " text NOT NULL"
                + foodContract.CustomRecipes.IMG_KEY + " text NOT NULL"
                + foodContract.CustomRecipes.INGREDIENTS + "text NOT NULL"
                + " );";

        final String SQL_CREATE_FAV = "CREATE TABLE "
                + foodContract.Favorites.TABLE_NAME
                + " ("
                + foodContract.Favorites._ID + " integer PRIMARY KEY AUTOINCREMENT"
                + foodContract.Favorites.TITLE + " text NOT NULL"
                + foodContract.Favorites.DESC + " text NOT NULL"
                + foodContract.Favorites.IMG_KEY + " text NOT NULL"
                + foodContract.Favorites.INGREDIENTS + " text NOT NULL"
                + foodContract.Favorites.ADDR + " text NOT NULL"
                + " );";

        final String SQL_CREATE_REST = "CREATE TABLE "
                + foodContract.Restaurants.TABLE_NAME
                + " ("
                + foodContract.Restaurants._ID + " integer PRIMARY KEY AUTOINCREMENT"
                + foodContract.Restaurants.TITLE + " text NOT NULL"
                + foodContract.Restaurants.DESC + " text NOT NULL"
                + foodContract.Restaurants.IMG_KEY + " text NOT NULL"
                + foodContract.Restaurants.ADDR + " text NOT NULL"
                + " );";
    }

    /**
     * Not implemented yet as I just want to alter the favorites table
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //
        Log.d(TAG, "onUpgrade: not implemented yet");
    }
}
