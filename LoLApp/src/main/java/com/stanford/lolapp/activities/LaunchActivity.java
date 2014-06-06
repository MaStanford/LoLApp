package com.stanford.lolapp.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.stanford.lolapp.R;
import com.stanford.lolapp.dialogs.ErrorDialog;
import com.stanford.lolapp.interfaces.INoticeDialogListener;
import com.stanford.lolapp.service.LoLAppService;
import com.stanford.lolapp.util.Constants;
import com.stanford.lolapp.util.NetWorkConn;

/**
 * Launcher Activity.
 * Checks connectivity and if there is an active user
 */
public class LaunchActivity extends Activity implements INoticeDialogListener {

    private final static String TAG = "LaunchActivity";

    private LoLAppService mService;
    private boolean mBound = false;

    /***************************************************
     * Services
     *************************************************/

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            LoLAppService.LocalBinder binder = (LoLAppService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            checkConnection();
        }
        public void onServiceDisconnected(ComponentName className) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = new Intent(this, LoLAppService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void checkConnection(){
        Constants.DEBUG_LOG(TAG,"Checking connection");
        //Check network connection, do not allow access unless all data has been loaded in tournament mode
        if(!NetWorkConn.isNetworkOnline(this)){
            //Dialog mDialog = onAlertDialog(ErrorDialog.DIALOG_NETWORK_ERROR);
            ErrorDialog mDialog = ErrorDialog.newInstance(ErrorDialog.DIALOG_NETWORK_ERROR,LaunchActivity.this);
            mDialog.show(getFragmentManager(),TAG);
        }else{
            checkUser();
        }
    }

    private void checkUser(){
        Constants.DEBUG_LOG(TAG,"Checking User: ");
        //Check if user is null, if null send to login, if not null send to main
        if(mService.isUserAvailible()) {
            Constants.DEBUG_LOG(TAG,"User is availible.");
            Intent mIntent = new Intent(this,MainActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(mIntent);
        }else {
            //If no user object then send to log in
            Constants.DEBUG_LOG(TAG, "No User is availible.");
            Intent mIntent = new Intent(this, LoginActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(mIntent);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog,int type) {
        Constants.DEBUG_LOG(TAG,"Checking connection after user input");
        checkConnection();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog,int type) {
        Constants.DEBUG_LOG(TAG,"User exits");
        finish();
    }
}
