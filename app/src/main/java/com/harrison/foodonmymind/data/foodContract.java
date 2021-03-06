package com.harrison.foodonmymind.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
//make sure the import is com.harrison.foodonmymind.BuildConfig.As lots of BuildConfig libraries
//and lots of times defaults to app.compat which isn't correct
import com.harrison.foodonmymind.BuildConfig;

/**
 * Created by harrison on 6/12/17.
 */

public class foodContract {
//    public static final String CONTENT_AUTHORITY = "com.harrison.foodonmymind.foodProvider";
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".foodProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String CUSTOM_RECIPES = "custom_recipes";
    public static final String RESTAURANTS = "restaurants";
    public static final String FAV = "favorites";
//  this will be the column of either the recipe title or the restaurant name
    public static final String TITLE = "title";
//  this will be used to find the image associated with the line item
    public static final String IMG_KEY = "img_key";
//  this will be the column used to put the list of ingredients associated with the recipe.
    public static final String INGREDIENTS = "ingredients";
//  the address of the restaurant
    public static final String ADDR = "address";
//  some short description of either the restaurant or recipe
    public static final String DESC = "description";
//    the built in column that is the autoincremented unique id identifier
    public static final String ID = "_id";

    public static final class CustomRecipes implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(CUSTOM_RECIPES).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CUSTOM_RECIPES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CUSTOM_RECIPES;
        public static final String TABLE_NAME = CUSTOM_RECIPES;
        //  this will be the column of either the recipe title or the restaurant name
        public static final String TITLE = "title";
        //  this will be used to find the image associated with the line item
        public static final String IMG_KEY = "img_key";
        //  this will be the column used to put the list of ingredients associated with the recipe.
        public static final String INGREDIENTS = "ingredients";
        //  directions for the recipe
        public static final String DESC = "description";
    }


    public static final class Restaurants implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(RESTAURANTS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + RESTAURANTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + RESTAURANTS;
        public static final String TABLE_NAME = RESTAURANTS;
        //  this will be the column of either the recipe title or the restaurant name
        public static final String TITLE = "title";
        //  this will be used to find the image associated with the line item
        public static final String IMG_KEY = "img_key";
        //  some short description of either the restaurant or recipe
        public static final String DESC = "description";
        //  the address of the restaurant
        public static final String ADDR = "address";
        //  the rating of the restaurant
        public static final String RATING = "rating";
        //  the price level of the restaurant
        public static final String PRICE = "price_level";
    }

    public static final class Favorites implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(FAV).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + FAV;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + FAV;
        public static final String TABLE_NAME = FAV;
        //  this will be the column of either the recipe title or the restaurant name
        public static final String TITLE = "title";
        //  this will be used to find the image associated with the line item
        public static final String IMG_KEY = "img_key";
        //  some short description of either the restaurant or recipe
        public static final String DESC = "description";
        //  the address of the restaurant
        public static final String ADDR = "address";
        //  this will be the column used to put the list of ingredients associated with the recipe.
        public static final String INGREDIENTS = "ingredients";
        //  the rating of the restaurant
        public static final String RATING = "rating";
        //  the price level of the restaurant
        public static final String PRICE = "price_level";
        //        url that links to the original source of this recipe
        public static final String SRC = "source_url";
    }

    public static Uri buildFoodUri(String table) {
        return BASE_CONTENT_URI.buildUpon().appendPath(table).build();
    }

    /**
     * Use the ContentUris class to help us build the Uri easily. Use its method withAppendId that
     * takes 2 arguments (1) the base Uri path - ie the base content uri and then the table to which
     * this new Uri belongs to (2) the id number associated with the row id (notice i don't need
     * to convert to a string when using this convenience method).
     * this is the build uri method that is for a specific inserted row
     * @param table - the table we want to insert the row into
     * @param id - the row id of where it is inserted. (this is a unique identifier maintained by
     *           the db via autoincrement
     * @return the Uri of of this new row
     */
    public static Uri buildFoodUri(String table, long id) {
        return ContentUris.withAppendedId(BASE_CONTENT_URI.buildUpon().appendPath(table).build(), id);
    }


    public static String getTableUri(Uri uri) {
        return uri.getPathSegments().get(0);
    }

    /**
     * helper function to return the rowID. Used for when we first insert a row of data and since
     * the uri returned in insert method it returns a new URI of the newly inserted data. So we
     * just use this int to use to query / filter on the _id column to find the specific row
     * @param uri
     * @return
     */
    public static int getRowUriNum(Uri uri) {
        return Integer.parseInt(uri.getPathSegments().get(1));
    }
}
