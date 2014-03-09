package com.complimenter.ecg.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.complimenter.ecg.R;

/**
 * Created by sam.jr on 3/9/14.
 */
public class SharingFragment extends Fragment{

    public interface OnSharingFragmentActivatedListener{
        public void beginSharing();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.ecg_share_view, container, false);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            final OnSharingFragmentActivatedListener callback = (OnSharingFragmentActivatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void setup(){
        final OnSharingFragmentActivatedListener callback = (OnSharingFragmentActivatedListener) this.getActivity();
        ImageView shareImage = (ImageView)this.getView().findViewById(R.id.image_share);
        shareImage.setVisibility(View.VISIBLE);
        shareImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.beginSharing();
                    }
                }
        );
    }
}
