package com.harrison.foodonmymind;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomImageFragment extends Fragment {
    ImageView imageView;
    String imagePath;


    public CustomImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
//        note had to move this code out of the fragment constructor as running this would cause
//        a fragment not attached to activity error since I was trying to get arguments before I
//        even really properly created and attached a fragment to the activity
        imagePath = getArguments().getString(getString(R.string.recipe_image_path), null);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        RelativeLayout rootView = (RelativeLayout) inflater.inflate(
//                R.layout.fragment_custom_image_fragment, container, false);
        imageView = (ImageView) inflater.inflate(R.layout.fragment_custom_image_fragment, container,
                false);
//        imageView = (ImageView) rootView.findViewById(R.id.single_custom_image);
        Log.d(TAG, "onCreateView custom image fragment: " + imagePath);
        imageView.setImageURI(Uri.parse(imagePath));
        return imageView;
    }

}
