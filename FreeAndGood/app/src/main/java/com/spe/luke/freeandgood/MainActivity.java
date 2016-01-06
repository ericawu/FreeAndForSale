package com.spe.luke.freeandgood;

import android.app.Activity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

// This should be in lukeBranch but not in master
public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener

{
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    //has user signed in before?
    private boolean signedIn = false;
    ViewSwitcher viewSwitcher;
    int signIn;
    int mainApp;

    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.mapview, null);

        View signInView = vi.inflate(R.layout.mapview, null);
        View mainAppView = (View) findViewById(R.id.drawer_layout);

        signIn = viewSwitcher.indexOfChild(signInView);
        mainApp = viewSwitcher.indexOfChild(mainAppView);

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) fragmentManager.findFragmentById(R.id.navigation_drawer);
        Fragment startingFragment = new FeedFragment();
        fragmentManager.beginTransaction().add(R.id.fragment_feed, startingFragment).addToBackStack(null).commit();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mTitle = getTitle();

        // Build out GoogleAPIClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();


        if (!mGoogleApiClient.isConnected())
        {
            showSignedOutUI();
        } else
        {
            showSignedInUI();
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    // Google Sign in Stuff
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d("GOOGLE connect fail", "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve)
        {
            if (connectionResult.hasResolution())
            {
                try
                {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e)
                {
                    Log.e("NO CONNECTION", "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else
            {
                // Could not resolve the connection result, show the user an
                // error dialog.
                showErrorDialog(connectionResult);
            }
        } else
        {
            // Show the signed-out UI
            showSignedOutUI();
        }
    }

    private void showSignedOutUI()
    {
        mGoogleApiClient.disconnect();

        viewSwitcher.setDisplayedChild(signIn);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        getSupportActionBar().hide();
    }

    private void showSignedInUI()
    {
        viewSwitcher.setDisplayedChild(mainApp);
        // Set up the drawer.

        restoreActionBar();
    }

    void showErrorDialog(ConnectionResult connectionResult)
    {
        GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 1);
    }


    @Override
    public void onConnected(Bundle bundle)
    {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.

        Log.d("CONNECTED", "onConnected:" + bundle);
        mShouldResolve = false;

        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        Log.d("EMAIL", email);
        if (!email.endsWith("@princeton.edu"))
        {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mShouldResolve = true;
            Toast toast = Toast.makeText(this,
                    "You must sign in with a princeton account to use the app.",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else
        {
            // Show the signed-in UI
            showSignedInUI();
        }
    }


    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.sign_in_button)
        {
            onSignInClicked();
        }

    }

    private void onSignInClicked()
    {
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // tell user they're signing in
        Toast toast = Toast.makeText(this,
                "Choose your Princeton email.",
                Toast.LENGTH_SHORT);
        toast.show();

    }

//    private class GetUserId extends AsyncTask<Void, String, Void>
//    {
//
//        @Override
//        protected Void doInBackground(Void... params)
//        {
//
//        }
//
//        @Override
//        protected void onPostExecute()
//        {
//
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ON_ACTIVITY_RESULT", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN)
        {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK)
            {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();

        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch (position)
        {
            //feed
            case 0:
            {
                fragment = new FeedFragment();
            }
            break;
            //my posts
            case 1:
            {
                fragment = new MyPostsFragment();
            }
            break;
            // watchlist
            case 2:
            {

            }
            break;
            //categories
            case 3:
            {
                fragment = new CategoriesFragment();
            }
            break;
            //profile
            case 4:
            {
                fragment = new ProfileFragment();
            }
            case 5:
            {
                if (mGoogleApiClient.isConnected())
                {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                }
                showSignedOutUI();
                Toast toast = Toast.makeText(this,
                        ("You are now signed out." + "\n" + "Sign in to access the app!"),
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            break;
        }

        if (fragment != null)
        {
            fragmentManager.beginTransaction()

                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed()
    {
        if (fragmentManager.getBackStackEntryCount() > 1)
        {
            fragmentManager.popBackStack();
        } else
        {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Quit")
                    .setMessage("Really quit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.show();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;
        if (searchItem != null)
        {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null)
        {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        // return true;
        // }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.newpost_button)
        {
            startActivity(new Intent(this, NewPostActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
