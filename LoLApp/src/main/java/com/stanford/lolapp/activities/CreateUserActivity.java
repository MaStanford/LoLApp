package com.stanford.lolapp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stanford.lolapp.DataHash;
import com.stanford.lolapp.LoLApp;
import com.stanford.lolapp.R;
import com.stanford.lolapp.models.User;
import com.stanford.lolapp.network.JSONBody;
import com.stanford.lolapp.network.LoLAppWebserviceRequest;
import com.stanford.lolapp.network.Requests;
import com.stanford.lolapp.network.VolleyTask;
import com.stanford.lolapp.network.WebService;
import com.stanford.lolapp.service.LoLAppService;
import com.stanford.lolapp.util.Constants;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class CreateUserActivity extends Activity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "CreateUserActivity";
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mUserView;
    private View mProgressView;
    private View mLoginFormView;

    //Globals
    private LoLApp mContext = LoLApp.getApp();
    private DataHash mDataHase =  LoLApp.getApp().getDataHash();

    private boolean mIsLoading = false;

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
        }
        public void onServiceDisconnected(ComponentName className) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        setupActionBar();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.et_email);
        populateAutoComplete();

        mUserView = (EditText) findViewById(R.id.et_user);

        mPasswordView = (EditText) findViewById(R.id.et_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.create || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mBtnCreateUser = (Button) findViewById(R.id.btn_create_user);
        mBtnCreateUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        findViewById(R.id.btn_sign_in).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start new Activity
                Intent mIntent = new Intent(mContext,LoginActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(mIntent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onStart(){
        super.onStart();
        /****************************************
         * SERVICES
         ***************************************/
        // Bind to LocalService
        Intent intent = new Intent(this, LoLAppService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Constants.DEBUG_LOG(TAG, "Service: " + mService);
    }

    @Override
    protected void onStop(){
        super.onStop();
        //Unbind the Services
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mIsLoading == true) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mUserView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String user = mUserView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //Check emails
        if(TextUtils.isEmpty(email)){
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }else if(!isEmailValid(email)){
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        //Check for valid user
        if (TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        } else if (!isUserValid(user)) {
            mUserView.setError(getString(R.string.error_invalid_user));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            atteptCreateUser(user, password, email);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    private boolean isEmailValid(String email){
        return email.contains("@");
    }

    private boolean isUserValid(String user) {
        Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(user);
        boolean b = m.find();
        if (b)
            return false;
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateUserActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private void errorHelper(String body){
        String code = "code";
        String emailTaken = "203";
        String userNameMissing = "200";

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String,String>>(){}.getType();
        Map<String, String> map = gson.fromJson(body,type);

        if(map.containsKey(code))
            if(map.get(code).equals(emailTaken));
                mEmailView.setError("Email Taken");
            if(map.get(code).equals(userNameMissing));
                mEmailView.setError("User Name invalid");
    }

    private void atteptCreateUser(final String user, final String password, String email) {

        mIsLoading = true;
        showProgress(true);

        //Example Create request
        RequestQueue requestQueue = VolleyTask.getRequestQueue(this);
        LoLAppWebserviceRequest request = new Requests.CreateUser();
        request.setRequestMethod(Request.Method.POST);
        JSONBody body = new JSONBody();

        body.addTuple(Requests.CreateUser.PARAM_REQUIRED_USERNAME, user);
        body.addTuple(Requests.CreateUser.PARAM_REQUIRED_PASSWORD, password);
        body.addTuple(Requests.CreateUser.PARAM_OPTIONAL_EMAIL, email);

        WebService.makeRequest(requestQueue, request, null, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        mIsLoading = false;
                        showProgress(false);

                        //Create a user
                        Gson gson = new Gson();
                        User mUser = gson.fromJson(response.toString(),User.class);
                        mUser.setUsername(user);
                        mDataHase.setUser(mUser);
                        mService.saveUserFile(mUser);

                        //Start new Activity
                        Intent mIntent = new Intent(mContext,MainActivity.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(mIntent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        if (error.networkResponse.statusCode == 400) {
                            try {
                                Constants.DEBUG_LOG(TAG, new String(error.networkResponse.data, "UTF-8"));
                                errorHelper(new String(error.networkResponse.data, "UTF-8"));
                            }catch (Exception e){
                                Constants.DEBUG_LOG(TAG,"Can not decode response");
                            }
                            mIsLoading = false;
                            showProgress(false);
                        }
                    }
                }
        );
    }
}



