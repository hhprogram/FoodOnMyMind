package com.harrison.foodonmymind;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Custom_image_fragment extends Fragment {
    ImageView imageView;
    Uri imageUri;
    String imagePath;


    public Custom_image_fragment() {
        // Required empty public constructor
        imagePath = getArguments().getString(getString(R.string.recipe_image_path), null);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        imageView = (ImageView) inflater.inflate(R.layout.fragment_custom_image_fragment, container,
                false);
        imageView.setImageURI(Uri.parse(imagePath));
        return imageView;
    }

}
