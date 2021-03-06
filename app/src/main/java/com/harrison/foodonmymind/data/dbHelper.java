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

    //just the name of the file where the database will be stored. So the SQLITE open helper knows
    //where to access to go get the actual database. needs a .db extension or else onCreate not
    //called
    public static String DATABASE_NAME = "food.db";
    public static final int DATABASE_VERSION = 2;

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//    note: when doing these SQL commands there needs to be a space between the column name
//    foodContract.CustomRecipes.TITLE and the descriptors -> "text NOT NULL," hence the blank space
//    right before each one " text NOT NULL," -> is the actual string
    @Override
    public void onCreate(SQLiteDatabase database) {
        final String SQL_CREATE_CUSTOM = "CREATE TABLE "
                + foodContract.CustomRecipes.TABLE_NAME
                + " ("
                + foodContract.CustomRecipes._ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + foodContract.CustomRecipes.TITLE + " text NOT NULL,"
                + foodContract.CustomRecipes.IMG_KEY + " text NOT NULL,"
                + foodContract.CustomRecipes.INGREDIENTS + " text NOT NULL,"
                + foodContract.CustomRecipes.DESC + " text NOT NULL"
                + " );";

        final String SQL_CREATE_FAV = "CREATE TABLE "
                + foodContract.Favorites.TABLE_NAME
                + " ("
                + foodContract.Favorites._ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + foodContract.Favorites.TITLE + " text NOT NULL,"
                + foodContract.Favorites.IMG_KEY + " text NOT NULL,"
                + foodContract.Favorites.INGREDIENTS + " text NOT NULL,"
                + foodContract.Favorites.ADDR + " text NOT NULL,"
                + foodContract.Favorites.PRICE + " integer NOT NULL,"
                + foodContract.Favorites.RATING + " real NOT NULL,"
                + foodContract.Favorites.SRC + " text NOT NULL"
                + " );";

        final String SQL_CREATE_REST = "CREATE TABLE "
                + foodContract.Restaurants.TABLE_NAME
                + " ("
                + foodContract.Restaurants._ID + " integer PRIMARY KEY AUTOINCREMENT,"
                + foodContract.Restaurants.TITLE + " text NOT NULL,"
                + foodContract.Restaurants.IMG_KEY + " text NOT NULL,"
                + foodContract.Restaurants.ADDR + " text NOT NULL,"
                + foodContract.Restaurants.PRICE + " integer NOT NULL,"
                + foodContract.Restaurants.RATING + " real NOT NULL"
                + " );";
//        need to put these in or else the actual tables don't get created.
        database.execSQL(SQL_CREATE_CUSTOM);
        database.execSQL(SQL_CREATE_FAV);
        database.execSQL(SQL_CREATE_REST);
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
