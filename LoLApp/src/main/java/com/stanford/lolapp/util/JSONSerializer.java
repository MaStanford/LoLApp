package com.stanford.lolapp.util;

import android.util.Log;
import android.widget.Toast;

import com.stanford.lolapp.LoLApp;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Mark Stanford on 5/5/14.
 */
public class JSONSerializer {

    /**
     *  Saves the JSONObject to file
     */
    public static void saveJSON(JSONObject object,File filename) throws JSONException, IOException {
        //Write to disk
        Writer mWriter = null;
        try{
            FileOutputStream mFileOutputStream = new FileOutputStream(filename);
            mWriter = new OutputStreamWriter(mFileOutputStream);
            mWriter.write(object.toString());
        } finally {
            if(mWriter != null){
                mWriter.close();
            }
        }
    }


    public static JSONObject loadJSONFile(File filename) throws JSONException, IOException {

        Toast.makeText(LoLApp.getApp(), "Loading " + filename.getName() + "...", Toast.LENGTH_LONG).show();

        JSONObject object = null;

        BufferedReader mBufferedReader = null;
        try{
            InputStream mInStream = new FileInputStream(filename);
            mBufferedReader = new BufferedReader(new InputStreamReader(mInStream));
            StringBuilder mJSONString = new StringBuilder();
            //Have a placeholder for each string
            String mLine = null;
            //read each line and append to the json string
            while((mLine = mBufferedReader.readLine()) != null ){
                mJSONString.append(mLine);
            }
            //Grab JSON tokens from string
            object = (JSONObject) new JSONTokener(mJSONString.toString()).nextValue();
            Log.d("JSONObject","Loaded JSON: " + object.toString());

        } catch (FileNotFoundException error){

        } finally {
            if(mBufferedReader != null){
                mBufferedReader.close();
            }
        }
        return object;
    }

}
