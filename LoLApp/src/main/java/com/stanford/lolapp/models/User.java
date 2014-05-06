package com.stanford.lolapp.models;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mark Stanford on 5/5/14.
 */
public class User {

    private String username;
    private String password;
    private String createdAt;
    private String updatedAt;
    private String objectId;
    private String sessionToken;

    //KEYS FOR JSON
    private String Jusername        = "Jusername";
    private String Jpassword        = "Jpassword";
    private String JcreatedAt       = "JcreatedAt";
    private String JupdatedAt       = "JupdatedAt";
    private String JobjectId        = "JobjectId";
    private String JsessionToken    = "JsessionToken";

    public User() {
    }

    public User(JSONObject jsonObject) throws JSONException{
       this.username = jsonObject.getString(Jusername);
       this.password= jsonObject.getString(Jpassword);
       this.createdAt= jsonObject.getString(JcreatedAt);
       this.updatedAt= jsonObject.getString(JupdatedAt);
       this.objectId= jsonObject.getString(JobjectId);
       this.sessionToken= jsonObject.getString(JsessionToken);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public JSONObject toJSON() throws JSONException{

        JSONObject mJSON = new JSONObject();

        mJSON.put(Jusername,username);
        mJSON.put(Jpassword,password);
        mJSON.put(JcreatedAt,createdAt);
        mJSON.put(JupdatedAt,username);
        mJSON.put(JobjectId,objectId);
        mJSON.put(JsessionToken,sessionToken);

        return mJSON;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", objectId='" + objectId + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                '}';
    }
}
