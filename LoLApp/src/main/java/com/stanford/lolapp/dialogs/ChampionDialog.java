package com.stanford.lolapp.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.R;
import com.stanford.lolapp.interfaces.NoticeDialogListener;
import com.stanford.lolapp.models.ChampionDTO;

/**
 * Created by Mark Stanford on 4/30/14.
 */
public class ChampionDialog extends DialogFragment {

    private static final String ARG_CHAMPION_ID = "com.stanford.lolapp.championID";
    private static final String ARG_ISDIALOG = "com.stanford.lolapp.isDialog";
    public static final String TAG = "com.stanford.lolapp.dialogs.ChampionDialog";

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    private static LoLApp mContext;
    private static DataHash mDataHash;

    private boolean mIsLargeLayout = true;
    private ChampionDTO mChampion;

    private TextView mTextView;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static ChampionDialog newInstance(int championID, boolean displayAsDialog) {
        ChampionDialog mDialog = new ChampionDialog();

        //Get singeltons
        mContext = LoLApp.getApp();
        mDataHash = mContext.getDataHash();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putBoolean(ARG_ISDIALOG, displayAsDialog);
        args.putInt(ARG_CHAMPION_ID, championID);
        mDialog.setArguments(args);

        return mDialog;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up initial state
        mIsLargeLayout = getArguments().getBoolean(ARG_ISDIALOG);
        int championID = getArguments().getInt(ARG_CHAMPION_ID);
        mChampion = mDataHash.getChampionByPos(championID);

        int theme = android.R.style.Theme_Holo_Light_Dialog;
        int style = DialogFragment.STYLE_NORMAL;
        setStyle(style, theme);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_champion, container);
        mTextView = (TextView) v.findViewById(R.id.tv_dialog_champion_details);
        mTextView.setText(mChampion.toString());

        getDialog().setTitle(mChampion.getName());

        return v;
    }
}
