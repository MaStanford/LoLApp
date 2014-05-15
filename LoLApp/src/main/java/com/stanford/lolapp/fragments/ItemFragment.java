package com.stanford.lolapp.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
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
import com.stanford.lolapp.adapters.ItemListAdapter;
import com.stanford.lolapp.fragments.dummy.DummyContent;
import com.stanford.lolapp.interfaces.OnFragmentInteractionListener;
import com.stanford.lolapp.models.ItemListDTO;
import com.stanford.lolapp.network.VolleyTask;
import com.stanford.lolapp.network.WebService;

import org.json.JSONObject;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the interface.
 */
public class ItemFragment extends Fragment implements AbsListView.OnItemClickListener {

    public static final String TAG = "ItemFragment";

    private static final String ARG_PARAM1 = "param1";

    private int mParam1;

    private OnFragmentInteractionListener mListener;

    private LoLApp mAppContext;
    private DataHash mDataHash;
    private FragmentManager fragmentManager;

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
    private ItemListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static ItemFragment newInstance(int param1) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }

        fragmentManager = getFragmentManager();

        mAppContext = LoLApp.getApp();
        mDataHash = mAppContext.getDataHash();

        mAdapter = new ItemListAdapter(mAppContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_item_list);
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        if (!mIsLoading && mDataHash.sizeOfItemList() == 0) {
            loadData();
        }

        // Set the adapter
        mListView = (ListView) view.findViewById(R.id.lv_item_list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(MainActivity.ITEMS);
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


    private void loadData() {

        mIsLoading = true;
        mProgressBar.setVisibility(ProgressBar.VISIBLE);

        RequestQueue requestQueue = VolleyTask.getRequestQueue(mAppContext);
        WebService.LoLAppWebserviceRequest request = new WebService.GetAllItems();
        Bundle params = new Bundle();
        params.putString(WebService.PARAM_REQUIRED_LOCATION, WebService.location.na.getLocation());
        params.putString(WebService.PARAM_REQUIRED_LOCALE, WebService.locale.en_US.getLocale());
        params.putString(WebService.GetAllItems.PARAM_DATA, WebService.ItemData.all.getParam());
        WebService.makeRequest(requestQueue, request, params,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ItemListDTO itemList = gson.fromJson(response.toString(), ItemListDTO.class);
                        mDataHash.setItemList(itemList);
                        onDoneLoading();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }
        );

    }

    private void onDoneLoading() {
        mIsLoading = false;
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
           mListener.onFragmentInteraction(null);
        }
    }
}