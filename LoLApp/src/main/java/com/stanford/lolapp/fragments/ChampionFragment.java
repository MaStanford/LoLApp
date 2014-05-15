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
import com.stanford.lolapp.R;

import com.stanford.lolapp.activities.MainActivity;
import com.stanford.lolapp.adapters.ChampionListAdapter;
import com.stanford.lolapp.dialogs.ChampionDialog;
import com.stanford.lolapp.interfaces.IServiceCallback;
import com.stanford.lolapp.interfaces.OnFragmentInteractionListener;
import com.stanford.lolapp.network.VolleyTask;
import com.stanford.lolapp.network.WebService;
import com.stanford.lolapp.service.LoLAppService;

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

    private static final String TAG = "ChampionFragment";
    private OnFragmentInteractionListener mListener;
    private static final String ARG_PARAM1 = "param1";
    private ProgressBar mProgressBar;
    private boolean mIsLoading = false;
    private int mParam1;
    private LoLAppService mService;

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


    public static ChampionFragment newInstance(int param1) {
        ChampionFragment fragment = new ChampionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1,param1);
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
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }

        //Check if data is stored volatile


        mAdapter = new ChampionListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_champion, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_champion_list);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

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

            //Calllback to Activity
            Bundle mBundle = new Bundle();
            mBundle.putInt("position",position);
            mListener.onFragmentInteraction(mBundle);
        }
    }
}
