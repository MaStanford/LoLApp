package com.stanford.lolapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.R;
import com.stanford.lolapp.models.ChampionDTO;
import com.stanford.lolapp.network.VolleyTask;

import java.util.ArrayList;


/**
 * Created by Mark Stanford on 4/28/14.
 */
public class ChampionListAdapter extends BaseAdapter{

    private static final String TAG = "ChampionListAdapter";

    private boolean mIsLoading = false;

    private LayoutInflater mInflater;
    private Context mContext;

    private LoLApp mAppContext;
    private DataHash mDataHash;

    /**
     * Construct the Adapter.
     */
    public ChampionListAdapter(Context context) {
        super();
        mContext = context;
        mAppContext = LoLApp.getApp();
        mDataHash = mAppContext.getDataHash();
        mInflater = LayoutInflater.from(context);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mDataHash.sizeOfChampionList();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mDataHash.getChampionByPos(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        if(mDataHash.getChampionByPos(position) != null)
            return mDataHash.getChampionByPos(position).getId();
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if(convertView == null){
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.list_layout_champion,null);

            holder.name = (TextView) convertView.findViewById(R.id.tv_list_champ_name);
            holder.tag = (TextView) convertView.findViewById(R.id.tv_list_champ_tag);
            holder.icon = (NetworkImageView) convertView.findViewById(R.id.iv_champion);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        /**
         * Error check null here.
         * Create default values in the xml to display until this is done.
         */
        //Get the champion by the position number
        ChampionDTO champ;
        if((champ = mAppContext.getDataHash().getChampionByPos(position)) != null) {
            Log.d(TAG,"Champ " + position + " is not null");
            holder.name.setText(champ.getName());
            holder.tag.setText(champ.getTag());
            holder.icon.setImageUrl(champ.getImageURL(), VolleyTask.getImageLoader(mContext));
        }else{
            Log.d(TAG,"Champ " + position + " is null");
            loadPosition(position);
        }

        //Load the next page if we are close to the end of the data we have.
        if(isCloseToEnd(position) && !mIsLoading){
            loadMoreData(position);
        }

        return convertView;
    }

    /**
     * Loads a certain part of the list.
     * @param position
     */
    private void loadPosition(int position) {
        if(mIsLoading)
            return;
        mIsLoading = true;
        //TODO: Make a call to the ChampionTask to get that champion by position and set Loading to false
    }

    private boolean isCloseToEnd(int position){

        //Grab the size of the champion map here
        int sizeOfMap = mAppContext.getDataHash().sizeOfChampionList();

        return (position > sizeOfMap - 10) ? true : false;
    }

    private void loadMoreData(int position){
        //TODO: Make a call to the ChampionTask to load the next few items from the keys list.
        //Figure out what to load, grab the keys from the ChampionIDList and get keys for the map
        //Create new webservice request here
        //Iterate through the keys you get from the IDlist
        mIsLoading = true;
        //Recieved the results
        mIsLoading = false;
        mAppContext.getDataHash().appendChampions(new ArrayList<ChampionDTO>()); //Getting this from the result
        this.notifyDataSetChanged();
    }

    private class ViewHolder{
        public TextView name;
        public TextView tag;
        public NetworkImageView icon;
    }
}
