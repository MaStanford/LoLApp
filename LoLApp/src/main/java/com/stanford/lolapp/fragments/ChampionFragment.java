package com.stanford.lolapp.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.R;

import com.stanford.lolapp.activities.MainActivity;
import com.stanford.lolapp.adapters.ChampionListAdapter;
import com.stanford.lolapp.dialogs.ChampionDialog;
import com.stanford.lolapp.interfaces.OnFragmentInteractionListener;
import com.stanford.lolapp.models.ChampionIDListDTO;
import com.stanford.lolapp.models.ChampionListDTO;
import com.stanford.lolapp.network.VolleyTask;
import com.stanford.lolapp.network.WebService;

import org.json.JSONObject;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the sinterface.
 */
public class ChampionFragment extends Fragment implements AbsListView.OnItemClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String LOG_TAG = "ChampionFragment";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LoLApp mAppContext;
    private DataHash mDataHash;

    private ProgressBar mProgressBar;
    private boolean mIsLoading = false;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ChampionListAdapter mAdapter;
    private FragmentManager fragmentManager;


    public static ChampionFragment newInstance(String param1, String param2) {
        ChampionFragment fragment = new ChampionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChampionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getFragmentManager();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAppContext = LoLApp.getApp();
        mDataHash = mAppContext.getDataHash();
        mAdapter = new ChampionListAdapter(mAppContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_champion, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_champion_list);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        if(mDataHash.sizeOfChampionList() == 0 && !mIsLoading){
            loadData();
        }

        // Set the adapter
        mListView = (ListView) view.findViewById(R.id.lv_champion_list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(MainActivity.CHAMPIONS);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Loads the data
     */

    public void loadData(){

        mIsLoading = true;
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        /**
         * Load all the Champion ID
         */
        // Load the request
        RequestQueue requestQueue = VolleyTask.getRequestQueue(mAppContext);

        //Grab a champion for PoC
        WebService.LoLAppWebserviceRequest request = new WebService.GetAllChampionIds();

        Bundle params = new Bundle();
        params.putString(WebService.PARAM_REQUIRED_LOCATION,WebService.location.na.getLocation());

        WebService.makeRequest(mAppContext,requestQueue, request, params, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ChampionIDListDTO champList = gson.fromJson(response.toString(),ChampionIDListDTO.class);
                        mDataHash.setChampionIdList(champList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG,error.toString());
                    }
                }
        );

        // Load the request
        requestQueue = VolleyTask.getRequestQueue(mAppContext);

        //Grab a champion for PoC
        request = new WebService.GetAllChampionData();

        params = new Bundle();

        params.putString(WebService.PARAM_REQUIRED_LOCATION,WebService.location.na.getLocation());
        params.putString(WebService.PARAM_REQUIRED_LOCALE,WebService.locale.en_US.getLocale());
        params.putString(WebService.GetChampionData.PARAM_DATA,WebService.ChampData.all.getData());

        WebService.makeRequest(mAppContext,requestQueue, request, params, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ChampionListDTO champ = gson.fromJson(response.toString(),ChampionListDTO.class);
                        LoLApp.getApp().getDataHash().setChampionList(champ);
                        onDoneLoadData();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(LOG_TAG,error.toString());
                    }
                }
        );
    }

    /**
     * Called when data is done loading
     */
    public void onDoneLoadData(){
        mIsLoading = false;
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Listener from the list.
     * Calls the fragment listener in the activity
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {

            FragmentTransaction mFragmentTrans = fragmentManager.beginTransaction();
            Fragment prev = fragmentManager.findFragmentByTag(ChampionDialog.TAG);
            if (prev != null) {
                mFragmentTrans.remove(prev);
            }
            mFragmentTrans.addToBackStack(null);

            // Create and show the dialog.
            DialogFragment newFragment = ChampionDialog.newInstance(position,true);
            newFragment.show(mFragmentTrans, ChampionDialog.TAG);

            mListener.onFragmentInteraction(position);
        }
    }
}
