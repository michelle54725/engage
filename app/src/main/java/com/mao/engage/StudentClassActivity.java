/**
 * StudentClassActivity: primary student interface
 *  - Contains 2 fragments: MeFragment and ClassFragment
 *
 * Triggered by:
 *  "JOIN CLASS" button in StudentLoginActivity
 *
 * Transitions to:
 *  (None)
 */
package com.mao.engage;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import info.hoang8f.android.segmented.SegmentedGroup;

public class StudentClassActivity extends AppCompatActivity {

    SegmentedGroup segmentedBar;
    RadioButton meTabBtn;
    RadioButton classTabBtn;
    ImageButton backBtn;
    MeFragment meFragment;
    ClassFragment classFragment;
    FragmentManager fragmentManager;

    //for ease of access to different data
    DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
    DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI: set to landscape, notification bar hidden
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        setContentView(R.layout.activity_student_class);

        segmentedBar = findViewById(R.id.segmentedBar);
        meTabBtn = findViewById(R.id.meTabBtn);
        classTabBtn = findViewById(R.id.classTabBtn);
        backBtn = findViewById(R.id.backBtn);

        segmentedBar.setTintColor(getResources().getColor(R.color.colorPrimary));
        meTabBtn.setTextColor(Color.WHITE);
        classTabBtn.setTextColor(Color.WHITE);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        meFragment = new MeFragment();
        // send data to Fragment
        Bundle bundle = new Bundle();
        bundle.putString("uID", getIntent().getStringExtra("uID"));
        Log.d("TEST", "put bundle: " + getIntent().getStringExtra("uID"));

        meFragment.setArguments(bundle);

        classFragment = new ClassFragment();
        fragmentTransaction.replace(R.id.constraintLayout, meFragment);
        fragmentTransaction.commit();

        meTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.constraintLayout, meFragment);
                fragmentTransaction.commit();
            }
        });

        classTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.constraintLayout, classFragment);
                fragmentTransaction.commit();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
