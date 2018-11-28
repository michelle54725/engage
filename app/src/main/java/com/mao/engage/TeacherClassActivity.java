package com.mao.engage;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;

import info.hoang8f.android.segmented.SegmentedGroup;

public class TeacherClassActivity extends AppCompatActivity implements TimelineFragment.OnFragmentInteractionListener{
    SegmentedGroup segmentedBar;
    RadioButton nowTabBtn;
    RadioButton timelineTabBtn;
    FragmentManager fragmentManager;
    NowFragment nowFragment;
    TimelineFragment timelineFragment;
    String mSectionRefKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_teacher_class);

        segmentedBar = findViewById(R.id.segmentedBar);
        nowTabBtn = findViewById(R.id.nowTabBtn);
        timelineTabBtn = findViewById(R.id.timelineTabBtn);

        segmentedBar.setTintColor(getResources().getColor(R.color.colorPrimary));
        nowTabBtn.setTextColor(Color.WHITE);
        timelineTabBtn.setTextColor(Color.WHITE);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        nowFragment = new NowFragment();
        Bundle bundle = new Bundle();
        bundle.putString("magic_word", getIntent().getStringExtra("magic_word"));
        Log.d("TEST-MAGIC", "in TeacherClassActivity: " + getIntent().getStringExtra("magic_word"));
        bundle.putString("section_name", getIntent().getStringExtra("section_name"));
        nowFragment.setArguments(bundle);

        timelineFragment = new TimelineFragment();
        fragmentTransaction.replace(R.id.constraintLayout, nowFragment);
        fragmentTransaction.commit();

        nowTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.constraintLayout, nowFragment);
                fragmentTransaction.commit();
            }
        });

        timelineTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.constraintLayout, timelineFragment);
                fragmentTransaction.commit();
            }
        });

        mSectionRefKey = getIntent().getStringExtra("sectionRefKey");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("BOBOB", "onFragmentInteraction: " + uri.toString());
    }
}
