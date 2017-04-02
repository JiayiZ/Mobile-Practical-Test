package com.example.zhoujiayi.todolist;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhoujiayi on 2017-04-01.
 */

public abstract class SendData extends AsyncTask<String, Void, Void> implements FetchDataCallBackInterface {

    Object caller;
    String apiData;
    String requestType;

    public SendData(Object caller) {
        this.caller = caller;
    }

    public abstract void fetchDataCallback(String result);
    protected Void doInBackground(String[] params) {
        HttpURLConnection connection;
        requestType = params[1];
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.setRequestMethod(params[1]);
            if (params[1].equals("DELETE")) {
                connection.setRequestProperty("Content-Type", "application/json");
            } else {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                osw.write(params[2]);
                osw.flush();
                osw.close();
            }
            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
                Log.d(params[1]+" Request Result",line);
            }
            apiData = result.toString();
        } catch (Exception e) {
            apiData = params[2].toString();
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void v) {
        fetchDataCallback(apiData);
    }

}
