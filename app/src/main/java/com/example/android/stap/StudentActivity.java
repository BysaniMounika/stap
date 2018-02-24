package com.example.android.stap;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {

    public static final int STUDENT_EDIT = 1;
    public static final int STUDENT_ADD = -1;
    public static int CLASS_ID;
    private static RecyclerView mRecyclerView2;
    private static StudentListAdapter mAdapter2;
    private static ClassListOpenHelper mDB;
    private CoordinatorLayout studentActivity;
    static ArrayList<StudentItem> students=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Bundle extra = getIntent().getExtras();
        studentActivity = (CoordinatorLayout) findViewById(R.id.student_activity);
        if(MainActivity.light == 1) {
            studentActivity.getBackground().setColorFilter(null);
        }
        else {
            studentActivity.getBackground().setColorFilter(getResources().getColor(R.color.layer), PorterDuff.Mode.MULTIPLY);
        }
        CLASS_ID = extra.getInt(ClassListAdapter.EXTRA_ID);
        mDB = new ClassListOpenHelper(this);
        // Create recycler view.
        mRecyclerView2 = (RecyclerView) findViewById(R.id.student_recyclerview);
        // Create an mAdapter and supply the data to be displayed.
        mAdapter2 = new StudentListAdapter(this,students,mDB);
        // Connect the mAdapter with the recycler view.
        Log.d("classid",CLASS_ID+" ");
        getStudents(CLASS_ID);

        // Give the recycler view a default layout manager.
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start empty edit activity.
                Intent intent = new Intent(getBaseContext(), EditStudentActivity.class);
                startActivityForResult(intent, STUDENT_EDIT);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Log","Destroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Log","Stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Log","pause");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                Log.d("pre","dsfjsdjhfh");
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.animator.activit_back_in, R.animator.activity_back_out);
                return true;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d("Log","postResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Log","start");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Log","resume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Log","restart");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Add code to update the database.
        if(requestCode == STUDENT_EDIT) {
            if(resultCode == RESULT_OK) {
                String name = data.getStringExtra(EditStudentActivity.EXTRA_REPLY_NAME);
                String phn = data.getStringExtra(EditStudentActivity.EXTRA_REPLY_PHN);
                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phn) && phn.length()==10) {
                    int id = data.getIntExtra(StudentListAdapter.EXTRA_STUDENT_ID,-99);
                    int classId = data.getIntExtra(StudentListAdapter.EXTRA_CLASS_ID, -99);
                    if(id == STUDENT_ADD) {
                        mDB.insertStudent(name,phn,classId);
                        Log.d("new record",id+" "+name+" "+phn+" "+classId);
                    }
                    if(id >= 0) {
                        mDB.updateStudent(id,name,phn);
                    }
                    getStudents(classId);
                    //mAdapter2.notifyDataSetChanged();
                }
                else if(TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(),"Not Saved Because name is empty", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(phn)) {
                    Toast.makeText(getApplicationContext(),"Not Saved Because Phone Number is empty", Toast.LENGTH_SHORT).show();
                }
                else if(phn.length() < 10) {
                    Toast.makeText(getApplicationContext(),"Not Saved Because Phone number is less than 10 digits", Toast.LENGTH_SHORT).show();
                }
                else if(phn.length() > 10){
                    Toast.makeText(getApplicationContext(),"Not Saved Because Phone number is greater than 10 digits", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void getStudents(int classId) {
        students.clear();

        StudentItem p=null;
        Cursor c=mDB.retrieve(classId);
        while (c.moveToNext())
        {
            int id=c.getInt(0);
            String name=c.getString(1);
            String phn = c.getString(2);
            int cid = c.getInt(3);

            p=new StudentItem();
            p.setmStudentId(id);
            p.setmStudentName(name);
            p.setmStudentPhn(phn);
            p.setmClassId(cid);

            students.add(p);

        }


        mRecyclerView2.setAdapter(mAdapter2);
    }

}
