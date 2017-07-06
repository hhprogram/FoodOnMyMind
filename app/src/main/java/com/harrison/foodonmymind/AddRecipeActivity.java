package com.harrison.foodonmymind;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_recipe);
        photoPaths = new ArrayList<>();
        ingre_layout = (LinearLayout) findViewById(R.id.ingredient_list);
        dir_layout = (LinearLayout) findViewById(R.id.directions);
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
                .LayoutParams((int) getResources().getDimension(R.dimen.ingredient)
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        EditText new_ingre = new EditText(this);
        RelativeLayout.LayoutParams ingreParams = new RelativeLayout
                .LayoutParams((int) getResources().getDimension(R.dimen.quantity)
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
        new_ingre.setHint(getString(R.string.ingre_hint));
        new_ingre.setLayoutParams(ingreParams);
        EditText new_quant = new EditText(this);
        RelativeLayout.LayoutParams quantParams = new RelativeLayout
                .LayoutParams((int) getResources().getDimension(R.dimen.ingredient)
                , RelativeLayout.LayoutParams.WRAP_CONTENT);
//        addRule is how i programatically align the editText view within this relativeLayout to be
//        aligned to the right of its parent and to the right of the new_ingre view
        quantParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        quantParams.addRule(RelativeLayout.RIGHT_OF, new_ingre.getId());
        new_quant.setHint(getString(R.string.ingre_hint));
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
     * The setAction() tells android what it should be doing
     *
     * Then we call startActivityForResult() and supply it with an intent (the one we created) plus
     * we call createChooser() on this intent which itself return an intent that gives the user a
     * interface to choose the specific application it wants to use to ACTION_GET_CONTENT (for
     * example will allow user to add a picture either via from gallery or using the camera)
     * @param view
     */
    public void addPic(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        String pic_chooser = getString(R.string.add_picture);
        startActivityForResult(intent.createChooser(intent, pic_chooser), SELECT_PICTURE);
    }

    /**
     * method called when the save recipe button is clicked. This triggers app to save all relevant
     * info into the DB
     * @param view
     */
    public void saveRecipe(View view) {

    }

}
