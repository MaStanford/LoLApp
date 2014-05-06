package com.stanford.lolapp.interfaces;

import android.app.DialogFragment;

/**
 * Created by Mark Stanford on 5/1/14.
 */
public interface NoticeDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog);

    public void onDialogNegativeClick(DialogFragment dialog);
}
