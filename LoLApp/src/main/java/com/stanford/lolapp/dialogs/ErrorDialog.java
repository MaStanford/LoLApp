package com.stanford.lolapp.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.R;
import com.stanford.lolapp.interfaces.INoticeDialogListener;

/**
 * Created by Mark Stanford on 5/6/14.
 */
public class ErrorDialog extends DialogFragment{

    private static final String TAG = "ErrorDialog";

    public static final int DIALOG_LOGIN_ERROR      =   0;
    public static final int DIALOG_CREATE_ERROR     =   1;
    public static final int DIALOG_NETWORK_ERROR    =   2;

    private static Context mContext;
    private static DataHash mDataHash;

    private boolean mIsLargeLayout = true;
    private INoticeDialogListener mListener;
    private int mDialogType = 0;

    private static String ARG_DIALOG_TYPE = "argDialogType";

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static ErrorDialog newInstance(int dialogType,Context context) {
        ErrorDialog mDialog = new ErrorDialog();

        mContext = context;

        if(mDialog == null)
            mDataHash = LoLApp.getApp().getDataHash();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_TYPE, dialogType);
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
            mListener = (INoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)} does not need
     * to be implemented since the AlertDialog takes care of its own content.
     * <p/>
     * <p>This method will be called after {@link #onCreate(android.os.Bundle)} and
     * before {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}.  The
     * default implementation simply instantiates and returns a {@link android.app.Dialog}
     * class.
     * <p/>
     * <p><em>Note: DialogFragment own the {@link android.app.Dialog#setOnCancelListener
     * Dialog.setOnCancelListener} and {@link android.app.Dialog#setOnDismissListener
     * Dialog.setOnDismissListener} callbacks.  You must not set them yourself.</em>
     * To find out about these events, override {@link #onCancel(android.content.DialogInterface)}
     * and {@link #onDismiss(android.content.DialogInterface)}.</p>
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Set up initial state
        mDialogType = getArguments().getInt(ARG_DIALOG_TYPE);

        int theme = android.R.style.Theme_Holo_Light_Dialog;
        int style = DialogFragment.STYLE_NORMAL;
        setStyle(style, theme);

        AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);

        switch(mDialogType){
            case DIALOG_LOGIN_ERROR:
                break;
            case DIALOG_CREATE_ERROR:
                break;
            case DIALOG_NETWORK_ERROR:
                mDialog.setTitle(getString(R.string.network_error_title));
                mDialog.setMessage(getString(R.string.network_error_body));
                mDialog.setPositiveButton(getString(R.string.network_error_positive),
                    new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(dialog);
                    }
                });
                mDialog.setNegativeButton(getString(R.string.network_error_negative)
                        ,new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(dialog);
                    }
                });
                break;
        }
        return mDialog.create();
    }
}
