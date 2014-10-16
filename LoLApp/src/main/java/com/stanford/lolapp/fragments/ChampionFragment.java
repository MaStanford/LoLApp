package com.stanford.lolapp.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.R;

import com.stanford.lolapp.activities.MainActivity;
import com.stanford.lolapp.adapters.ChampionListAdapter;
import com.stanford.lolapp.dialogs.ChampionDialog;
import com.stanford.lolapp.dialogs.ErrorDialog;
import com.stanford.lolapp.interfaces.INoticeDialogListener;
import com.stanford.lolapp.interfaces.OnFragmentInteractionListener;
import com.stanford.lolapp.network.Requests;
import com.stanford.lolapp.network.WebService;
import com.stanford.lolapp.service.LoLAppService;
import com.stanford.lolapp.util.Constants;
import com.stanford.lolapp.util.NetWorkConn;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the interface.
 */
public class ChampionFragment extends Fragment implements AbsListView.OnItemClickListener, INoticeDialogListener {

    public static final String TAG = "ChampionFragment";

    private OnFragmentInteractionListener mListener;
    private static final String ARG_PARAM1              = "param1";
    public static final String mSelectedChamp           = "position";
    private ProgressBar mProgressBar;
    private boolean mIsLoading = false;

    private int mParamFocusChampID = 0;

    private Bundle mRequestParams;
    private Bundle mRequestParamsChamp;
    private Bundle mRequestParamsIds;

    private LoLApp mContext;
    private DataHash mDataHash;

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
     * Broadcast receiver
     */
    BroadcastReceiver dataReceiever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getActivity(), "onReceive Action: " + intent.getAction(), Toast.LENGTH_SHORT).show();
            if (intent.getAction().equals(LoLAppService.ACTION_FETCH_CHAMPIONS_DONE)) {
                onDoneLoadData(true);
            } else if (intent.getAction().equals(LoLAppService.ACTION_FETCH_CHAMPIONS_DONE_FAIL)) {
                onDoneLoadData(false);
            }
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChampionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = LoLApp.getApp();
        mDataHash = mContext.getDataHash();

        fragmentManager = getFragmentManager();

        if (getArguments() != null) {
            mParamFocusChampID = getArguments().getInt(ARG_PARAM1);
        }

        //Put in the params. TODO: get these from the shared prefs or have defaults.
        mRequestParams = new Bundle();
        mRequestParams.putString(WebService.PARAM_REQUIRED_LOCATION, WebService.location.na.getLocation());
        mRequestParams.putString(WebService.PARAM_REQUIRED_LOCALE,WebService.locale.en_US.getLocale());

        mRequestParamsChamp = new Bundle();
        mRequestParamsChamp.putAll(mRequestParams);
        mRequestParamsChamp.putString(Requests.GetAllChampionData.PARAM_DATA,Requests.GetAllChampionData.ChampData.all.getData());

        mRequestParamsIds =  new Bundle();
        mRequestParamsIds.putAll(mRequestParams);

        //Check if data is stored volatile
        mAdapter = new ChampionListAdapter(getActivity());
        mAdapter.setFocus(mParamFocusChampID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_champion, container, false);

        setRetainInstance(true);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_champion_list);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // Set the adapter
        mListView = (ListView) view.findViewById(R.id.lv_champion_list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        //CheckData
        if (mDataHash.isChampionListAvailible()){
            Constants.DEBUG_LOG(TAG,"Bound and Champs are available.");
            onDoneLoadData(true);
        }else{//TODO: only download what we need, set that here?
            Constants.DEBUG_LOG(TAG,"Bound and Champs are not available");
            loadData();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        //Broadcast
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoLAppService.ACTION_FETCH_CHAMPIONS_DONE);
        filter.addAction(LoLAppService.ACTION_FETCH_CHAMPIONS_DONE_FAIL);
        getActivity().registerReceiver(dataReceiever, filter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        getActivity().unregisterReceiver(dataReceiever);
    }

    /**
     * Loads the data
     */
    public void loadData(){
        mIsLoading = true;
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        Bundle params = new Bundle();
        params.putString(WebService.PARAM_REQUIRED_LOCATION, WebService.location.na.getLocation());
        params.putString(WebService.PARAM_REQUIRED_LOCALE, WebService.locale.en_US.getLocale());
        params.putString(Requests.GetAllChampionData.PARAM_DATA, Requests.GetAllChampionData.ChampData.all.getData());


        Intent loadDataIntent = new Intent();
        loadDataIntent.putExtras(params);
        loadDataIntent.setAction(LoLAppService.ACTION_FETCH_CHAMPIONS);
        loadDataIntent.setClass(getActivity(), LoLAppService.class);
        getActivity().startService(loadDataIntent);
    }

    /**
     * Called when data is done loading
     */
    public void onDoneLoadData(boolean success){
        Constants.DEBUG_LOG(TAG, "Data Loaded! isChampionListAvailable(): " + mDataHash.isChampionListAvailible());
        if(success) {
            mIsLoading = false;
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            mAdapter.notifyDataSetChanged();
        }else{
            ErrorDialog mDialog = ErrorDialog.newInstance(ErrorDialog.DIALOG_DOWNLOAD_ERROR, getActivity());
            mDialog.show(getFragmentManager(), TAG);
            Constants.DEBUG_LOG(TAG,"Attempt to load data but no connection.");
        }
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

            //Callback to Activity
            Bundle mBundle = new Bundle();
            mBundle.putInt(mSelectedChamp,position);
            mListener.onFragmentInteraction(mBundle);
        }
    }

    /**
     * The dialog for the connection
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int type) {
        if(type == ErrorDialog.DIALOG_NETWORK_ERROR) {
            if (!NetWorkConn.isNetworkOnline(getActivity())) {
                ErrorDialog mDialog = ErrorDialog.newInstance(ErrorDialog.DIALOG_NETWORK_ERROR, getActivity());
                mDialog.show(getFragmentManager(), TAG);
            }
        }else if(type == ErrorDialog.DIALOG_DOWNLOAD_ERROR){
            //TODO: do something here I guess
        }
    }

    /**
     * The dialog for the connection
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int type) {
        if(type == ErrorDialog.DIALOG_NETWORK_ERROR) {
            if (!NetWorkConn.isNetworkOnline(getActivity())) {
                getActivity().finish();
            }
        }else if(type == ErrorDialog.DIALOG_DOWNLOAD_ERROR){
            //TODO: do something here I guess
        }
    }
}
