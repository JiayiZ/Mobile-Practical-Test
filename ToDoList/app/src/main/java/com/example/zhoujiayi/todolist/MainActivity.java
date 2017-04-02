package com.example.zhoujiayi.todolist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements FetchDataCallBackInterface {
    private static final String TAG = "MainActivity";

    // data is class member because this activity should behave like a singleton for data
    // this way data are fetched only once and reused by different class instances
    static String data = null;
    private ArrayAdapter<String> mAdapter;
    private ListView mTaskListView;
    ArrayList<String> idList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTaskListView = (ListView) findViewById(R.id.list_todo);
        if(MainActivity.data == null) {
            new FetchData(Property.USER_TODOS, this).execute();
        } else {
            fetchDataCallback(data);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());

                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("userId", Property.USER_ID);
                                    jsonObject.put("title", task);
                                    jsonObject.put("completed", "false");
                                    String json = jsonObject.toString();
                                    SendData sd = new SendData(this) {
                                        @Override
                                        public void fetchDataCallback(String result) {
                                            try {
                                                JSONObject jObject = new JSONObject(result);
                                                mAdapter.insert(jObject.getString("title"),0);
                                                mAdapter.notifyDataSetChanged();
                                                idList.add(0,jObject.getString("id"));
                                            } catch (JSONException e) {
                                                Log.e("JSONException", "Error: " + e.toString());
                                            }
                                        } //
                                    };
                                    sd.execute(Property.TODOS_API_URL,"POST",json);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.d("Task list for display", mAdapter.toString());
                            }

                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void fetchDataCallback(String result) {
        //Log.d("fetchDataCallback","I am here.");
        data = result;
        mTaskListView = (ListView) findViewById(R.id.list_todo);
        ArrayList<String> taskList = new ArrayList<>();
        Log.d("renderData",data);
        try {
            JSONArray jArray = new JSONArray(data);
            for(int i=0; i < jArray.length(); i++) {

                JSONObject jObject = jArray.getJSONObject(i);
                taskList.add(jObject.getString("title"));
                idList.add(jObject.getString("id"));
            }
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }
        renderData(taskList);
    }

    public void renderData(ArrayList taskList) {
        // do something with your data here
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            //Log.d("Task list for display", taskList.toString());
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
        //Log.d(TAG, "Render Data to Load");
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        Log.d("Remove", task);
        int idPos = mAdapter.getPosition(task);
        String id = idList.get(idPos);
        idList.remove(idPos);
        mAdapter.remove(task);
        mAdapter.notifyDataSetChanged();
        String url = Property.TODOS_API_URL + "/" + id;
        SendData sd = new SendData(this) {
            @Override
            public void fetchDataCallback(String result) {
            }

        };
        sd.execute(url,"DELETE","");

    }

    public void editTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String rawTask = String.valueOf(taskTextView.getText());
        final EditText taskEditText = new EditText(this);
        final int pos = mAdapter.getPosition(rawTask);
        taskEditText.setText(rawTask);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit task")
                .setView(taskEditText)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("userId", Property.USER_ID);
                            jsonObject.put("id",idList.get(pos));
                            jsonObject.put("title", task);
                            jsonObject.put("completed", "false");
                            String json = jsonObject.toString();
                            SendData sd = new SendData(this) {
                                @Override
                                public void fetchDataCallback(String result) {
                                    try {
                                        if (result !=null) {
                                            JSONObject jObject = new JSONObject(result);
                                            int pos = idList.indexOf(jObject.getString("id"));
                                            String item = mAdapter.getItem(pos);
                                            mAdapter.remove(item);
                                            idList.remove(pos);
                                            mAdapter.insert(jObject.getString("title"), 0);
                                            mAdapter.notifyDataSetChanged();
                                            idList.add(0, jObject.getString("id"));
                                        }
                                    } catch (JSONException e) {
                                        Log.e("JSONException", "Error: " + e.toString());
                                    }
                                } //
                            };
                            sd.execute(Property.TODOS_API_URL+"/"+idList.get(pos),"PUT",json);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("Task list for display", mAdapter.toString());
                    }

                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }



}
