package com.stanford.lolapp.network;

/**
 * Created by Mark Stanford on 4/25/14.
 */
public class InputError {
    private String mId;
    private String mMessage;

    public InputError(String id, String message) {
        mId = id;
        mMessage = message;
    }

    public String getId() {
        return mId;
    }

    public String getMessage() {
        return mMessage;
    }
}
