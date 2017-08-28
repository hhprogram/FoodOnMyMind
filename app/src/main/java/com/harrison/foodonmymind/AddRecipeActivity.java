package com.harrison.foodonmymind;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harrison.foodonmymind.data.foodContract;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.direction;
import static android.R.attr.value;
import static android.content.ContentValues.TAG;
import static com.harrison.foodonmymind.R.dimen.quantity;
import static com.harrison.foodonmymind.R.string.ingredients;
import static com.harrison.foodonmymind.R.string.step;

/**
 * Created by harrison on 7/6/17. Activity used to host the layout for adding a user's own
 * custom recipe
 */

public class AddRecipeActivity extends AppCompatActivity {

//    the code used so that when this activity gets a result back from an activity it launched with
//    an intent - it can ensure that it is the correct request data
    private static final int SELECT_PICTURE = 1;
//    used to hold all the photo paths that we want associated with this recipe
    private ArrayList<String> photoPaths;
//    will be used to refer to when we want to add another ingredient
    LinearLayout ingre_layout;
//    will be used to refer to when we want to add another step
    LinearLayout dir_layout;
//    int used to refer to the id of the new ingredient editText box. These don't need to be unique
//    which is why i'm making it a final class variable.
    private final int INGRE_ID = 10;
//    instance variable used to keep track of the number of steps
    private int num_steps, num_ingredients;
//    variable that will be used to be assigned the path of the image that was taken with the camera
//    use a class variable as we want to refer back to it as we want to store the path in the
//    sql database for easy reference later
    String image_location;
    EditText recipe_title_box;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);
        recipe_title_box = (EditText) findViewById(R.id.recipe_title);
        photoPaths = new ArrayList<>();
        ingre_layout = (LinearLayout) findViewById(R.id.ingredient_list);
        dir_layout = (LinearLayout) findViewById(R.id.directions);
        num_steps = 1;
        num_ingredients = 1;
    }

//    called once the Activity started by startActivityForResult() method returns data
//    I guess there is a built in static variable RESULT_OK that is equal to the code used when
//    an activity returns some data properly. Then all you do to 'store' an image is just store
//    the path to the actual image into the database so we can refer to it when loading up this
//    recipe later
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
//                we do this check based on the stack over flow
//                https://stackoverflow.com/questions/9890757/android-camera-data-intent-returns-null
//                basically since we made the take picture intent and passed in the EXTRA_OUTPUT
//                as an extra it defaults to returning null and just saving the media to a file
//                location vs. if we hadn't entered that as an extra it would have returned something
//                for DATA which would have been the thumbnail of the picture just taken. OR if we
//                do an choose file intent and just grab an image from the gallery then it returns
//                the file taken
                if (data == null) {
                    Log.d(TAG, "onActivityResult: data is null using camera");
                    photoPaths.add(image_location);
                } else {
                    Log.d(TAG, "onActivityResult: " + data.getScheme());
                    Uri selectedImage = data.getData();
                    Log.d(TAG, "onActivityResult: selected image uri:" + selectedImage.toString());
//                    don't use the getPath because before I used it and it was just the relative
//                    path and it didn't seem to be working as couldn't find the image using the
//                    relative path. Therefore, selectedImage is just a URI of data. therefore this
//                    is the full path to the image thus we can just convert this to a string a use
//                    it later on to find the image and populate the viewPager
//                    image_location = selectedImage.getPath();
                    image_location = selectedImage.toString();
                    photoPaths.add(image_location);
//                    TBD: NEED TO SAVE IT LOCALLY (specific to APP) and then resize to avoid size
//                    ERRORS
                    Log.d(TAG, "onActivityResult: data not null not using camera");
                    }
            }
        }
    }

    /**
     * method called when the add ingredient button is clicked in the add recipe layout.
     * First we create a new view RelativeLayout. And then we get this new view's layout parameters
     * instance object. Then we set the width and height to constants that match what we have
     * in the initial add_recipe layout
     * @param view
     */
    public void addIngredient(View view) {
        num_ingredients += 1;
        RelativeLayout ingre_line = new RelativeLayout(this);
        RelativeLayout.LayoutParams relParams = new RelativeLayout
                .LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        EditText new_ingre = new EditText(this);
        RelativeLayout.LayoutParams ingreParams = new RelativeLayout
                .LayoutParams((int) getResources().getDimension(R.dimen.ingredient)
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        new_ingre.setHint(getString(R.string.ingre_hint));
        new_ingre.setLayoutParams(ingreParams);
//        needed to add the first argument in setTextSize per the following article:
//        https://stackoverflow.com/questions/6784353/inconsistency-when-setting-textview-font-size-in-code-and-in-resources
//        or else the font size was being converted to different units or something and showing
//        up way bigger than xml defined text size even though pointing to say dimen value
        new_ingre.setTextSize(TypedValue.COMPLEX_UNIT_PX
                , getResources().getDimension(R.dimen.hint_size));
//        need to call setID and feed in any positive int into the arg so that we can refer to this
//        view's id later in the addRule() for the new_quant editText. Unsure why it is giving me
//        this warning
        new_ingre.setId(INGRE_ID);
        EditText new_quant = new EditText(this);
        RelativeLayout.LayoutParams quantParams = new RelativeLayout
                .LayoutParams((int) getResources().getDimension(quantity)
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
//        addRule is how i programatically align the editText view within this relativeLayout to be
//        aligned to the right of its parent and to the right of the new_ingre view
        quantParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        this is where we refer to the ID used that we assigned a few lines above. Without doing
//        setId() before this line, no ID is found and thus this rule did nothing and the format
//        looked weird
        quantParams.addRule(RelativeLayout.RIGHT_OF, new_ingre.getId());
        new_quant.setHint(getString(R.string.quantity_hint));
        new_quant.setTextSize(TypedValue.COMPLEX_UNIT_PX
                , getResources().getDimension(R.dimen.hint_size));
        new_quant.setLayoutParams(quantParams);
//        then adding these views to the parent RelativeLayout view so that android knows these 2
//        should be children of the relativeLayout
        ingre_line.addView(new_ingre);
        ingre_line.addView(new_quant);
        new_ingre.setContentDescription(getString(R.string.ingre_input_desc) +
                Integer.toString(num_ingredients));
        new_quant.setContentDescription(getString(R.string.qty_input_desc) +
                Integer.toString(num_ingredients));
        ingre_line.setLayoutParams(relParams);
//        then at very end we add this new relative layout to the existing linear layout that holds
//        the ingredient lists
        ingre_layout.addView(ingre_line);
        Log.d(TAG, "addIngredient: " + ingre_layout.getChildCount());
    }

    /**
     * method called when the add step button is clicked in the add recipe layout
     * Note: i'm only adding one child view to DIR_LAYOUT as i'm putting the textview and the
     * edittext view into one linear layout view
     * @param view
     */
    public void addStep(View view) {
        num_steps = num_steps + 1;
        LinearLayout new_step = new LinearLayout(this);
        TextView step_num = new TextView(this);
        EditText step_text = new EditText(this);
        LinearLayout.LayoutParams step_params = new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        new_step.setOrientation(LinearLayout.VERTICAL);
        new_step.setLayoutParams(step_params);
        step_num.setText(getString(step) + Integer.toString(num_steps));
//        setting the layout params like this as don't need any relative layout referencing thus
//        just need to define a width and height and thus just do it like this
        step_num.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
        step_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));
        step_text.setHint(getString(R.string.step_hint));
        step_text.setContentDescription(getString(R.string.step_input_desc)
                + Integer.toString(num_steps));
        new_step.addView(step_num);
        new_step.addView(step_text);
        dir_layout.addView(new_step);
        Log.d(TAG, "addStep: # step children:" + dir_layout.getChildCount());
    }

    /**
     * method called when the add picture button is clicked in the add recipe layout. This method
     * is following these links:
     *
     * https://stackoverflow.com/questions/2169649/get-pick-an-image-from-androids-built-in-gallery-app-programmatically
     *
     * https://developer.android.com/reference/android/content/Intent.html#ACTION_GET_CONTENT
     *
     * Basically, first need to make an intent. Then the setType is telling the intent only looking
     * for these type of data types that are allowed to be 'gotten' by the ACTION_GET_CONTENT.
     * The setAction() tells android what it should be doing. This action is just getting any
     * arbitrary file - thus this option will be used for obtaining an image from the gallery
     *
     * Then we need to make another intent that is used to activate the camera.
     *
     * Then we create last intent to hold both the get and camera intents and then make a chooser
     * to allow the user to pick which intent to use. This is done by the putExtra() method and then
     *
     *
     * Then we call startActivityForResult() and supply it with an intent (the one we created) plus
     * we call createChooser() on this intent which itself return an intent that gives the user a
     * interface to choose the specific application it wants to use to ACTION_GET_CONTENT (for
     * example will allow user to add a picture either via from gallery or using the camera)
     * @param view
     */
    public void addPic(View view) {
        Intent intent_get = new Intent();
        intent_get.setType("image/*");
        intent_get.setAction(Intent.ACTION_GET_CONTENT);
        String pic_chooser = getString(R.string.add_picture);
        Intent chooser = Intent.createChooser(intent_get, pic_chooser);
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo_file = null;
        try {
            photo_file = createImageFile();
        } catch (IOException e) {
            Log.d(TAG, "addPic: IO Exception when trying to create file");
        }
        if (photo_file != null) {
            Uri image_uri = FileProvider.getUriForFile(this, getString(R.string.file_provider_authority), photo_file);
            camera.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        }
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS , new Intent[]{camera});
        startActivityForResult(chooser, SELECT_PICTURE);
    }

    /**
     * Helper function to create the file where the image is saved if user is taking a new picture
     * to add to the recipe
     *
     * see below link for reference of how I'm saving files:
     * https://developer.android.com/training/basics/data-storage/files.html
     *
     * Basically, I'm first creating a directory file in the new File() line. And I do it with the
     * getExternalFilesDir(Environment.DIRECTORY_PICTURES) because this create a directory for
     * photos (as noted by the DIRECTORY_PICTURES) and using that method to create the file creates
     * it under a directory specifically related to my app and thus it is private to my app and
     * will be deleted when my app is deleted.(also see my comment in the file_paths. xml file in the
     * xml directory
     *
     * note if user just pics an existing image this File that is returned isn't used (should check
     * if I need to delete this file or if camera action not taken then this file isn't saved)
     * @return a file object that the image will be saved to
     */
    private File createImageFile() throws IOException{
        String time_stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        just use the same album name as don't need to distinguish within the private app pictures
//        directory
        String album_name = getString(R.string.app_name);
        File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), album_name);
//        placeholder for checking total space available and if not pop up a dialog to remedy issue
//        and don't allow user to even the camera app
        if ()
        Log.d(TAG, "createImageFile: directory for pics" + Environment.DIRECTORY_PICTURES);
        file.mkdirs();
//        so this creates a file in which we will save the new image into. arguments are suffix,
//        prefix and then the directory in which the file will belong
        Log.d(TAG, "createImageFile: " + this.getFilesDir() + ";" + file.getAbsolutePath());
        File image = File.createTempFile(time_stamp, getString(R.string.ftype), file);
        image_location = image.getAbsolutePath();
        return image;
    }

    /**
     * method called when the save recipe button is clicked. This triggers app to save all relevant
     * info into the DB
     * @param view
     */
    public void saveRecipe(View view) {
        String title = recipe_title_box.getText().toString();
        if (title.matches("")) {
            titleDialog();
        } else {
            Log.d(TAG, "saveRecipe: Recipe Title" + title);
            saveHelper(title);
        }
    }

    /**
     * helper function that displays a dialog to be used if user tries to save a recipe with no
     * title
     */
    private void titleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Toast toast = Toast.makeText(this, getString(R.string.recipe_not_saved)
                , Toast.LENGTH_LONG);
//        see comments in the setUpDialogs() method in MainActivity to why I need to inflate layout
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout title_layout = (LinearLayout) inflater.inflate(R.layout.add_title_dialog, null);
        builder.setView(title_layout);
//        see setUpDialogs() method in MainActivity again to why i need final
        final EditText title_input = (EditText) title_layout.findViewById(R.id.recipe_title);
        builder.setPositiveButton(getString(R.string.add_recipe)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = title_input.getText().toString();
                saveHelper(title);
            }
        });
//        cancelling just closes dialog and then doesn't save recipe and just goes back to my
//        activity
        builder.setNegativeButton(getString(R.string.manual_cancel)
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                toast.show();
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    /**
     * helper that puts all necessary info from Add recipe activity into a content values to be
     * then uploaded in the database
     * @param title
     * @return
     */
    public ContentValues packageRecipe(String title) {
        ContentValues recipe = new ContentValues();
        recipe.put(foodContract.CustomRecipes.TITLE, title);
        recipe.put(foodContract.CustomRecipes.INGREDIENTS, combineIngredients());
        recipe.put(foodContract.CustomRecipes.DESC, combineSteps());
        recipe.put(foodContract.CustomRecipes.IMG_KEY, combineImagePaths());
        return recipe;
    }

    /**
     * helper function called when we are actually ready to save the recipe
     * @param title - the string that will be used to save the recipe title
     */
    private void saveHelper(String title) {
        ContentValues recipe = packageRecipe(title);
        Uri customRecipeUri = foodContract.buildFoodUri(foodContract.CustomRecipes.TABLE_NAME);
        Log.d(TAG, "saveHelper: " + customRecipeUri.toString());
        Uri newRow = getContentResolver().insert(customRecipeUri, recipe);
        Log.d(TAG, "saveHelper: " + newRow.toString());
        if (newRow == null) {
            Log.d(TAG, "saveHelper: problem inserting recipe");
        } else {
            Toast.makeText(this, getString(R.string.recipe_saved), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, CustomRecipeActivity.class);
            intent.putExtra(getString(R.string.recipe_uri), newRow);
            startActivity(intent);
        }
    }

    /**
     * Helper that returns a comma delimited string. Used to put all the steps and ingredients all
     * in one string to be saved in one column of sql database
     * This works by taking the linear layout INGRE_LAYOUT and going through its 2nd children and
     * all children after that. Works like this because the first child is a static textView and
     * then when we 'add' an ingredient it adds a relativeLayout that has 2 child EditText views.
     * Therefore, we first look through the relative layout children. Then for each relative
     * layout child view we must go through the 2 EditText children it has (note no need for
     * dynamic referencing as the first child is always the ingredient description and the 2nd
     * is the quantity)
     * @return
     */
    public String combineIngredients() {
        StringBuilder combined = new StringBuilder();
        Log.d(TAG, "combineIngredients: number of children" + ingre_layout.getChildCount());
        for (int i = 1; i < ingre_layout.getChildCount(); i++) {
            RelativeLayout r_layout = (RelativeLayout) ingre_layout.getChildAt(i);
            EditText value = (EditText) r_layout.getChildAt(0);
            String ingredient = value.getText().toString();
            Log.d(TAG, "combineIngredients: loop # " + i);
            if (!ingredient.equals("")) {
                EditText quantity = (EditText) r_layout.getChildAt(1);
                String quantity_str = quantity.getText().toString();
                Log.d(TAG, "combine: next ingredient:" + ingredient + " " + quantity_str);
                combined.append(ingredient);
                combined.append(" - ");
                combined.append(quantity_str);
                combined.append(",");
            }
        }
        if (combined.toString().equals("")) {
//        putting  a comma if ingredient is empty and thus
//        we want to put no ingredients in the ingredients column and thus in CustomRecipeActivity
//        we want the arrayLists that we build from separating the string by commas to be zero.
//        thus if we put a leading comma this will happen. but if we don't populate the
//        stringBuilder with anything then it will just be an empty string but when we parse out
//        the string it will add the empty string and make the ingredientsList have length 1 with
//        just an empty string which is not what i want
            combined.append(",");
        }
        Log.d(TAG, "combineIngredients: " + combined.toString() + ":");
        return combined.toString();
    }

    public String combineSteps() {
//        don't need a leading comma here since doing it a different way where I always add a
//        comma since there will always at least be 2 children and go through thsi for loop at
//        least once. And if step is empty it just adds the leading comma for me
        StringBuilder combined = new StringBuilder();
        Log.d(TAG, "combineSteps: " + dir_layout.getChildCount());
        for (int i = 0; i < dir_layout.getChildCount(); i++) {
            LinearLayout l_layout = (LinearLayout)  dir_layout.getChildAt(i);
//            always get the 2nd child as every linear layout only has 2 children (1) number label
//            (2) the input text
            EditText direction = (EditText) l_layout.getChildAt(1);
            String step = direction.getText().toString();
            Log.d(TAG, "combine: step #" +  i + ":" + step);
            combined.append(step);
            combined.append(",");
        }
        Log.d(TAG, "combineSteps: " + combined.toString() + ":");
        return combined.toString();
    }

    /**
     * helper that takes all paths in PHOTOPATHS arrayList and puts them into one comma delimited
     * string to be saved in sql database
     * @return
     */
    public String combineImagePaths() {
        StringBuilder combined = new StringBuilder();
        for (String path : photoPaths) {
            combined.append(path);
            combined.append(",");
        }
        return combined.toString();
    }


}
