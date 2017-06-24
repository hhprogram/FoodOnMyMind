package com.harrison.foodonmymind;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by harrison on 6/13/17.
 * Class to be used to store relevant info pertaining to recipes or restaurants found online
 */

public class Info {

//    static strings to be referenced when declaring instances of Info objects
    public static final String RESTAURANT = "restaurant";
    public static final String RECIPE = "recipe";
//    used as easy variable to see what type of info lives in this object. true = restaurant info
//    false = recipe info
    private boolean restaurant;
    private Context mContext;
//    an arraylist of hashmaps. each individual hashmap holds attributes on each result
    private ArrayList<InfoItem> data;
//  array of Pair objects. Will populate these pairs with the first element being the column name
//    in the db and the 2nd element will be the result JSON key from the API query. Did this as
//    a way to more 'intelligently' populate the list DATA without having to copy a bunch of lines
//    of code to put a bunch of key, value pairs in
    private Pair[] required_attrs;
//    this is the string key for the specific api that relates to this instance object. ex.) if this
//    Info object holds recipes this string will be equal to 'recipes' which is the key for the
//    food2fork api JSON response that holds the array of recipe results
    private String result_str;
//    the string that the user typed in that spawned this Info object
    private String query;
    private String title_key;
    private String img_key;
    private String addr_key;
    private String rating_key;
    private String price_key;
    private String source_key;
    private String ingre_key;


    /**
     * Constructor
     * @param type - one of the two static strings defined in this class to tell each instance what
     *             type of data it holds (recipe or restaurant)
     * @param context - used in order to easily utilize xml resources defined strings etc...
     */
    public Info(String type, Context context) {
        this.mContext = context;
        title_key = mContext.getString(R.string.col_title);
        img_key = mContext.getString(R.string.col_img);
        addr_key = mContext.getString(R.string.col_addr);
        rating_key = mContext.getString(R.string.col_rating);
        price_key = mContext.getString(R.string.col_price);
        source_key = mContext.getString(R.string.col_src);
        ingre_key = mContext.getString(R.string.col_ingr);
        if (type == RESTAURANT) {
            this.restaurant = true;
            this.required_attrs = new Pair[R.integer.rest_attrs];
//            don't need explicit type arguments here. Only need them when iterating through the
//            array and looking at each Pair element
            this.required_attrs[0] = new Pair<>(title_key
                    , mContext.getString(R.string.goog_title));
            this.required_attrs[1] = new Pair<>(img_key
                    , mContext.getString(R.string.goog_photo_ref));
            this.required_attrs[2] = new Pair<>(addr_key
                    , mContext.getString(R.string.goog_addr));
            this.required_attrs[3] = new Pair<>(rating_key
                    , mContext.getString(R.string.goog_rating));
            this.required_attrs[4] = new Pair<>(price_key
                    , mContext.getString(R.string.goog_price));
            this.result_str = mContext.getString(R.string.goog_results);
        } else {
            this.restaurant = false;
            this.required_attrs = new Pair[3];
            this.required_attrs[0] = new Pair<>(mContext.getString(R.string.col_title)
                    , mContext.getString(R.string.f2f_title));
            this.required_attrs[1] = new Pair<>(img_key
                    , mContext.getString(R.string.f2f_img));
            this.required_attrs[2] = new Pair<>(source_key
                    , mContext.getString(R.string.f2f_source));
            this.result_str = mContext.getString(R.string.f2f_recipes);
        }
        data = new ArrayList<>();
    }


    /**
     * Takes a JSON object (either JSON response from food2fork or google place search api)
     * and then populates DATA object
     * @param obj - either JSON response from food2fork or google place search api
     */
    public void populateData(JSONObject obj) {
        try {
            JSONArray results = obj.getJSONArray(result_str);
            for (int i = 0; i < results.length(); i++) {
                InfoItem attrs = new InfoItem(this.result_str);
                JSONObject result = results.getJSONObject(i);
//                need the declaration of types for each element within the pair or else line
//                result.getString(p.getSecond()) won't compile because p.getSecond() will just be
//                of type Object and result.getString() requires a string as an arg. Populates
//                some of the necessary data whether it be for recipes or restaurants
                for (Pair<String, String> p : required_attrs) {
                    attrs.put(p.getFirst(), result.getString(p.getSecond()));
                }
//                some restaurant / recipe specific tasks. Want the actual photo URL string in
//                DB so need to do that via google photo api url. So replace the col_img initial
//                key value pair where value was just the photo reference returned via google place
//                api, the value is now the photo api url
                if (this.restaurant) {
                    String photoUrl = photo_url_helper(attrs.get(mContext.getString(R.string.col_img))
                            ,result.getString(mContext.getString(R.string.goog_photo_width)));
                    attrs.put(img_key, photoUrl);
                } else if (!this.restaurant) {
                    String ingredients = getIngredients(result.getString(mContext.getString(R.string.f2f_id)));
                    attrs.put(ingre_key, ingredients);
                }
                data.add(attrs);
            }
        } catch (JSONException e) {
            Log.d(TAG, "getGoogSearch: JSON exception");
        }
    }

    /**
     * Helper method to build the google photo api url
     * @param ref - the photo reference code String that google place search returns
     * @return - String representation of the google photo url required to obtain the photo
     */
    private String photo_url_helper(String ref, String width) {
        String photoRef = mContext.getString(R.string.goog_photo_ref) + ref;
        String photoW = mContext.getString(R.string.goog_q_maxw) + width;
        String key = mContext.getString(R.string.api_key)
                + mContext.getString(R.string.googApiKey);
        String url = Utilities.createUrl(mContext.getString(R.string.googPhotoApi)
                , photoRef, photoW, key);
        return url;
    }

    /**
     * Helper function that returns a concatenated string of the strings within the ingredients
     * string array found in the JSON response for get recipe in the food2fork api
     * @param recipeId - the unique recipe identifier
     * @return comma delimited string of ingredients for the recipe (ie pork,salt,pepper,...)
     */
    private String getIngredients(String recipeId) {
        String key = mContext.getString(R.string.api_key)
                + mContext.getString(R.string.ffApiKey);
        String rId = mContext.getString(R.string.ff_q_rId)
                + recipeId;
        String url = Utilities.createUrl(mContext.getString(R.string.recipeGetApi), key
                , rId);
        JSONObject obj = Utilities.getData(url);
        try {
            JSONObject recipe = obj.getJSONObject(mContext.getString(R.string.f2f_get_recipe));
            JSONArray ingredients = recipe.getJSONArray(mContext.getString(R.string.f2f_get_ingr));
//            build the stringbuilder object like this so I don't have a trailing comma at the end
//            of my string. will add empty string, then first element, then next loop will add
//            a comma and then the next element. Thus when I reach end of the loop the last thing
//            added is a word not a comma
            StringBuilder lst = new StringBuilder();
            String prefix = "";
            for (int i = 0; i < ingredients.length(); i++) {
                lst.append(prefix);
                prefix = ",";
                lst.append(ingredients.get(i));
            }
            return lst.toString();
        } catch (JSONException e) {
            Log.d(TAG, "getIngredients: JSON exception");
        }
        return null;
    }

    /**
     * function that adds a InfoItem to be used to show user that there were no results. Just adds
     * an empty InfoItem
     */
    public void addNoResult() {
        InfoItem item = new InfoItem(this.result_str);
        data.add(item);
    }

    /**
     * Returns the arraylist data
     * @return
     */
    public ArrayList getData() {
        return data;
    }

    private void getFood2Fork(JSONObject obj) {

    }

    class InfoItem {
//        Making a small inner class to abstract away the String keys and having to work with a
//        Hashmap in order to extract attributes for each search result. Will be better if want to
//        add more attributes per item.

//        hashmap that holds the data for this particular search item
        private HashMap<String, String> data;
//        the string indicating what type of Info this item holds
        private String itemType;

        public InfoItem(String type) {
            this.itemType = type;
            this.data = new HashMap<>();
        }

        /**
         * just a wrapper method for the hashmap method put
         * @param key
         * @param Value
         */
        public void put(String key, String Value) {
            data.put(key, Value);
        }

        /**
         * just a wrapper method for the hashmap method get
         * @param key
         */
        public String get(String key) {
            return data.get(key);
        }

        //below are just a bunch of getter methods. Use this so that the user of these items don't
        //need to know the key Strings used for returning the data within an InfoItem
        public String getImgUrl() {
            return data.get(img_key);
        }

        public String getTitle() {
            return data.get(title_key);
        }

        public String getAddr() {
            String result = null;
            if (itemType == Info.RESTAURANT) {
                result = data.get(addr_key);
            }
            return result;
        }

        public String getIngredients() {
            String result = null;
            if (itemType == Info.RECIPE) {
                result = data.get(ingre_key);
            }
            return result;
        }

        public String getPriceLevel() {
            String result = null;
            if (itemType == Info.RESTAURANT) {
                result = data.get(price_key);
            }
            return result;
        }

        public String getRating() {
            String result = null;
            if (itemType == Info.RESTAURANT) {
                result = data.get(rating_key);
            }
            return result;
        }

        public String showEmpty() {
            if (data == null) {
                return mContext.getString(R.string.no_results);
            }
            return null;
        }

    }



}
