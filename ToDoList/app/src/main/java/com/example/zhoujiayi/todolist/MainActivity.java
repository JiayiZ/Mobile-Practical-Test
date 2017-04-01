package com.example.zhoujiayi.todolist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;



public class MainActivity extends AppCompatActivity implements FetchDataCallBackInterface {
    private static final String TAG = "MainActivity";

    // data is class member because this activity should behave like a singleton for data
    // this way data are fetched only once and reused by different class instances
    static String data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // check internet connection

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        if(MainActivity.data == null) {
        // automatically calls the renderData function
            Log.d(TAG, "I am here! ");
            new FetchData("http://jsonplaceholder.typicode.com/users/1/todos", this).execute();
        }
        else {
            renderData();
        }
*/
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
                                Log.d(TAG, "Task to add: " + task);
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
        data = result;
        renderData();
    }

    public void renderData() {
        // do something with your data here
        Log.d(TAG, "Render Data to Load");
    }

}


