package com.example.android.stap;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int CLASS_EDIT = 1;
    public static final int CLASS_ADD = -1;

    private RecyclerView mRecyclerView;
    private ClassListAdapter mAdapter;
    private ClassListOpenHelper mDB;
    private CoordinatorLayout main;




    public static int  light = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = (CoordinatorLayout) findViewById(R.id.main_activity);



        mDB = new ClassListOpenHelper(this);
        // Create recycler view.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        // Create an mAdapter and supply the data to be displayed.
        mAdapter = new ClassListAdapter(this,mDB);
        // Connect the mAdapter with the recycler view.
        mRecyclerView.setAdapter(mAdapter);
        // Give the recycler view a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add a floating action click handler for creating new entries.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start empty edit activity.
                Intent intent = new Intent(getBaseContext(), EditClassActivity.class);
                startActivityForResult(intent, CLASS_EDIT);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem mode = (MenuItem) menu.findItem(R.id.night_mode);
        if(light ==1 ) {
            mode.setTitle("Night Mode");
        } else {
            mode.setTitle("Day Mode");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.night_mode) {
            Log.d("MOde",item.getTitle().toString());
            if(item.getTitle().toString()=="Day Mode") {
                main.getBackground().setColorFilter(null);
                light = 1;
            }
            else {
                main.getBackground().setColorFilter(getResources().getColor(R.color.layer), PorterDuff.Mode.MULTIPLY);
                light = 0;
            }
        }
        recreate();
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Add code to update the database.
        if(requestCode == CLASS_EDIT) {
            if(resultCode == RESULT_OK) {
                String word = data.getStringExtra(EditClassActivity.EXTRA_REPLY);
                if(!TextUtils.isEmpty(word)) {
                    int id = data.getIntExtra(ClassListAdapter.EXTRA_ID,-99);
                    if(id == CLASS_ADD) {
                        mDB.insert(word);
                    }
                    if(id >= 0) {
                        mDB.update(id,word);
                    }
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getApplicationContext(),R.string.empty_not_saved, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
