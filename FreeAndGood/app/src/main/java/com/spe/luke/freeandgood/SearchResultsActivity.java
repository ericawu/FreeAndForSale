package com.spe.luke.freeandgood;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Luke on 6/26/2015.
 */
public class SearchResultsActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        if(Intent.ACTION_SEARCH.equals((intent.getAction()))){
            String query = intent.getStringExtra(SearchManager.QUERY);
            // now we will use this query to search through all our data... somehow
        }
    }
}
