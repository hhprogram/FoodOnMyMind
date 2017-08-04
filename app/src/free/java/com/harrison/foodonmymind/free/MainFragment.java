//note: we do the normal package and not com.harrison.foodonmymind.free because then we can just
//refer to com.harrison.foodonmymind.MainFragment in the activity_main xml file and then it will
//look for that java file and it will be named that in both the free and paid version. Therefore,
//need one in the paid directoy path and free directory path
package com.harrison.foodonmymind;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.harrison.foodonmymind.R;

import static android.content.ContentValues.TAG;

/**
 * note: the easy way to do this to allow android studio to make the proper directory path is to
 * just (1) make a 'free' directory right under 'src' and then right click on free and create a
 * new fragment
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "in main fragment free");
        View root = inflater.inflate(R.layout.main_fragment, container, false);

        AdView mAdView = (AdView) root.findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        return root;
    }

}
