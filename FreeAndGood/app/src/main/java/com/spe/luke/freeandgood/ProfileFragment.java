package com.spe.luke.freeandgood;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luke on 6/24/2015.
 */
public class ProfileFragment extends Fragment
{
    //empty constructor
    public ProfileFragment()
    {
    }

    // view for fragment


    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        return rootView;
    }


}
