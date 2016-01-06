package com.spe.luke.freeandgood;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Luke on 6/24/2015.
 */
public class MyPostsFragment extends Fragment
{
    public MyPostsFragment() {
    }

    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_myposts, container, false);
        return rootView;
    }
}
