package com.stanford.lolapp.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.R;
import com.stanford.lolapp.dialogs.ChampionDialog;
import com.stanford.lolapp.fragments.BuilderFragment;
import com.stanford.lolapp.fragments.ChampionFragment;
import com.stanford.lolapp.fragments.DeepsFragment;
import com.stanford.lolapp.fragments.GamesFragment;
import com.stanford.lolapp.fragments.HomeFragment;
import com.stanford.lolapp.fragments.ItemFragment;
import com.stanford.lolapp.fragments.NavigationDrawerFragment;
import com.stanford.lolapp.fragments.SummonerFragment;
import com.stanford.lolapp.interfaces.OnFragmentInteractionListener;
import com.stanford.lolapp.models.ChampionDTO;
import com.stanford.lolapp.models.ChampionIDListDTO;
import com.stanford.lolapp.models.ChampionListDTO;
import com.stanford.lolapp.network.VolleyTask;
import com.stanford.lolapp.network.WebService;

import org.json.JSONObject;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks
        ,OnFragmentInteractionListener
        ,ChampionDialog.NoticeDialogListener{

    public static final int HOME = 0;
    public static final int CHAMPIONS = 1;
    public static final int ITEMS = 2;
    public static final int SUMMONERS = 3;
    public static final int GAMES = 4;
    public static final int DEEPS = 5;
    public static final int BUILDER = 6;

    private static final String LOG_TAG = "MainActivity";

    private LoLApp mAppContext;
    private DataHash mDataHash;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppContext = LoLApp.getApp();
        mDataHash = mAppContext.getDataHash();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Log.d(LOG_TAG, "Position: " + position);

        Fragment mFragment = null;

        switch (position) {
            case HOME:
                mFragment = HomeFragment.newInstance(HOME);
                break;
            case CHAMPIONS:
                mFragment = ChampionFragment.newInstance("Turd", "Bucket");
                break;
            case ITEMS:
                mFragment = ItemFragment.newInstance("Chimp", "Ape");
                break;
            case SUMMONERS:
                mFragment = SummonerFragment.newInstance("Albert", "OneStone");
                break;
            case GAMES:
                mFragment = GamesFragment.newInstance("Scruff", "Mcgruff");
                break;
            case DEEPS:
                mFragment = DeepsFragment.newInstance("Ali", "Baba");
                break;
            case BUILDER:
                mFragment = BuilderFragment.newInstance("3 Legged", "Dog");
                break;
            default:
                mFragment = HomeFragment.newInstance(HOME);
                break;
        }

        if (mFragment != null) {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mFragment)
                    .commit();
        } else {
            throw new NullPointerException("ABORT!!!");
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case HOME:
                mTitle = getString(R.string.title_section1);
                break;
            case CHAMPIONS:
                mTitle = getString(R.string.title_section2);
                break;
            case ITEMS:
                mTitle = getString(R.string.title_section3);
                break;
            case SUMMONERS:
                mTitle = getString(R.string.title_section4);
                break;
            case GAMES:
                mTitle = getString(R.string.title_section5);
                break;
            case DEEPS:
                mTitle = getString(R.string.title_section6);
                break;
            case BUILDER:
                mTitle = getString(R.string.title_section7);
                break;
        }

        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onFragmentInteraction(int id) {

    }

    /**
     * Listener from Champion Dialog
     * Probably don't need but was in the google docs
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog){

    }

    /**
     * Listener from Champion Dialog
     * Probably don't need but was in the google docs
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog){

    }
}
