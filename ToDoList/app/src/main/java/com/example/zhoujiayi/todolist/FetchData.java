package com.example.zhoujiayi.todolist;

/**
 * Created by zhoujiayi on 2017-04-01.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Allows to fetch string data from a server, given the url and a callable.
 * The callable is an object of a class implementing the FetchDataCallbackInterface
 * which defines the callback method fetchDataCallback
 */
public class FetchData extends AsyncTask <String, String, Void>{
    HttpURLConnection urlConnection;
    String url;
    FetchDataCallBackInterface callbackInterface;
    String apiData;
    /**
     * Constructor
     * @param url
     * @param callbackInterface class which defines the callback method
     */
    public FetchData(String url, FetchDataCallBackInterface callbackInterface) {
        this.url = url;
        this.callbackInterface = callbackInterface;
    }

    @Override
    protected Void doInBackground(String... args) {
        //Log.d("API DATA:","Start!");
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
                //Log.d("API DATA:",line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        apiData = result.toString();
        //return result.toString();
        return null;
    }
    @Override
    protected void onPostExecute(Void v) {
        //super.onPostExecute(result);
        // pass the result to the callback function
        this.callbackInterface.fetchDataCallback(apiData);
        //Log.d("onPostExecute","I am here.");
    }

}
