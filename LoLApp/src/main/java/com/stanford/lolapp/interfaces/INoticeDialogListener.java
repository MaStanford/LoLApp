package com.stanford.lolapp.interfaces;

import android.content.DialogInterface;

/**
 * Created by Mark Stanford on 5/1/14.
 */
public interface INoticeDialogListener {
    public void onDialogPositiveClick(DialogInterface dialog,int type);

    public void onDialogNegativeClick(DialogInterface dialog,int type);
}
