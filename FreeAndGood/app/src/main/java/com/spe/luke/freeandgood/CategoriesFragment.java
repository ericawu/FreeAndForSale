package com.spe.luke.freeandgood;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class CategoriesFragment extends Fragment implements View.OnClickListener
{
    //empty constructor
    public CategoriesFragment()
    {
    }

    // view for fragment
    View rootView;
    Fragment fragment = null;
    Bundle arguments = new Bundle();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_categories, container, false);


        Button categoryButtonFurniture = (Button) rootView.findViewById(R.id.categoryButtonFurniture);
        categoryButtonFurniture.setOnClickListener(this);

        Button categoryButtonAppliances = (Button) rootView.findViewById(R.id.categoryButton_Appliances);
        categoryButtonAppliances.setOnClickListener(this);

        Button categoryButtonElectronics = (Button) rootView.findViewById(R.id.categoryButton_Electronics);
        categoryButtonElectronics.setOnClickListener(this);

        Button categoryButtonGames = (Button) rootView.findViewById(R.id.categoryButton_Games);
        categoryButtonGames.setOnClickListener(this);

        Button categoryButtonMisc = (Button) rootView.findViewById(R.id.categoryButton_Misc);
        categoryButtonMisc.setOnClickListener(this);

        Button categoryButtonTextbooks = (Button) rootView.findViewById(R.id.categoryButton_Textbooks);
        categoryButtonTextbooks.setOnClickListener(this);

        Button categoryButtonClothing = (Button) rootView.findViewById(R.id.categoryButtonClothing);
        categoryButtonClothing.setOnClickListener(this);

        Button categoryButtonFood = (Button) rootView.findViewById(R.id.categoryButtonFood);
        categoryButtonFood.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.categoryButtonFurniture:
                fragment = new CategoryFeedFragment();
                arguments.putString("Category", "furniture");
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getId(), fragment).addToBackStack(null).commit();
                break;
            case R.id.categoryButton_Appliances:
                fragment = new CategoryFeedFragment();
                arguments.putString("Category", "appliances");
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getId(), fragment).addToBackStack(null).commit();
                break;
            case R.id.categoryButton_Electronics:
                fragment = new CategoryFeedFragment();
                arguments.putString("Category", "electronics");
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getId(), fragment).addToBackStack(null).commit();
                break;
            case R.id.categoryButton_Games:
                fragment = new CategoryFeedFragment();
                arguments.putString("Category", "games");
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getId(), fragment).addToBackStack(null).commit();
                break;
            case R.id.categoryButton_Misc:
                fragment = new CategoryFeedFragment();
                arguments.putString("Category", "misc");
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getId(), fragment).addToBackStack(null).commit();
                break;
            case R.id.categoryButton_Textbooks:
                fragment = new CategoryFeedFragment();
                arguments.putString("Category", "textbooks");
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getId(), fragment).addToBackStack(null).commit();
                break;
            case R.id.categoryButtonClothing:
                fragment = new CategoryFeedFragment();
                arguments.putString("Category", "clothing");
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getId(), fragment).addToBackStack(null).commit();
                break;
            case R.id.categoryButtonFood:
                fragment = new CategoryFeedFragment();
                arguments.putString("Category", "food");
                fragment.setArguments(arguments);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getId(), fragment).addToBackStack(null).commit();
                break;
            default:
                break;

        }
    }


//
//    public void furnitureSwitcher()
//    {
//        fragment = new CategoryFeedFragment();
//        arguments.putString("Category", "furniture");
//        fragment.setArguments(arguments);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_feed, fragment).commit();
//    }
//
//    public void foodSwitcher()
//    {
//        fragment = new CategoryFeedFragment();
//        arguments.putString("Category", "food");
//        fragment.setArguments(arguments);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_feed, fragment).commit();
//    }
//
//    public void clothingSwitcher()
//    {
//        fragment = new CategoryFeedFragment();
//        arguments.putString("Category", "clothing");
//        fragment.setArguments(arguments);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_feed, fragment).commit();
//    }
//
//    public void electronicsSwitcher()
//    {
//        fragment = new CategoryFeedFragment();
//        arguments.putString("Category", "electronics");
//        fragment.setArguments(arguments);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_feed, fragment).commit();
//    }
//
//    public void appliancesSwitcher()
//    {
//        fragment = new CategoryFeedFragment();
//        arguments.putString("Category", "appliances");
//        fragment.setArguments(arguments);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_feed, fragment).commit();
//    }
//
//    public void gamesSwitcher()
//    {
//        fragment = new CategoryFeedFragment();
//        arguments.putString("Category", "games");
//        fragment.setArguments(arguments);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_feed, fragment).commit();;;
//    }
//
//    public void textbooksSwitcher()
//    {
//        fragment = new CategoryFeedFragment();
//        arguments.putString("Category", "textbooks");
//        fragment.setArguments(arguments);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_feed, fragment).commit();
//    }
//
//    public void miscSwitcher()
//    {
//        fragment = new CategoryFeedFragment();
//        arguments.putString("Category", "misc");
//        fragment.setArguments(arguments);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_feed, fragment).commit();
    //   }
}

