package com.harrison.foodonmymind;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

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
    private int num_steps;
//    variable that will be used to be assigned the path of the image that was taken with the camera
    String image_location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);
        photoPaths = new ArrayList<>();
        ingre_layout = (LinearLayout) findViewById(R.id.ingredient_list);
        dir_layout = (LinearLayout) findViewById(R.id.directions);
        num_steps = 1;
    }

//    called once the Activity started by startActivityForResult() method returns data
//    I guess there is a built in static variable RESULT_OK that is equal to the code used when
//    an activity returns some data properly. Then all you do to 'store' an image is just store
//    the path to the actual image into the database so we can refer to it when loading up this
//    recipe later
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri imageUri = data.getData();
                String imagePath = imageUri.toString();
                photoPaths.add(imagePath);
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
                .LayoutParams((int) getResources().getDimension(R.dimen.quantity)
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
        ingre_line.setLayoutParams(relParams);
//        then at very end we add this new relative layout to the existing linear layout that holds
//        the ingredient lists
        ingre_layout.addView(ingre_line);
    }

    /**
     * method called when the add step button is clicked in the add recipe layout
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
        step_num.setText(getString(R.string.step) + Integer.toString(num_steps));
//        setting the layout params like this as don't need any relative layout referencing thus
//        just need to define a width and height and thus just do it like this
        step_num.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT));
        step_text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));
        step_text.setHint(getString(R.string.step_hint));
        new_step.addView(step_num);
        new_step.addView(step_text);
        dir_layout.addView(new_step);
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
            Uri image_uri = FileProvider.getUriForFile(this, getString(R.string.authority), photo_file);
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
     * will be deleted when my app is deleted.(also see my comment in the file_paths.xml file in the
     * xml directory
     * @return a file object that the image will be saved to
     */
    private File createImageFile() throws IOException{
        String time_stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        just use the same album name as don't need to distinguish within the private app pictures
//        directory
        String album_name = getString(R.string.app_name);
        File file = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), album_name);
//        so this creates a file in which we will save the new image into. arguments are suffix,
//        prefix and then the directory in which the file will belong
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

    }

}
