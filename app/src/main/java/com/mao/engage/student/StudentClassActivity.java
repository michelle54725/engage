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
package com.mao.engage.student;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
import com.github.mikephil.charting.data.Entry;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;

import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

public class StudentClassActivity extends AppCompatActivity {

    SegmentedGroup segmentedBar;
    RadioButton meTabBtn;
    RadioButton classTabBtn;
    ImageButton backBtn;
    MeFragment meFragment;
    StudentTimelineFragment studentTimelineFragment;
    FragmentManager fragmentManager;

    //List of Entry type inputs used to graph timelines in StudentTimelineFragment
    ArrayList<Entry> meValues;
    ArrayList<Entry> classAverages;

    //for ease of access to different data
    DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
    DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions");

    //
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
        FirebaseUtils.setSliderListener(FirebaseUtils.getPsuedoUniqueID());
        setContentView(R.layout.activity_student_class);

        //instantiating layout components
        segmentedBar = findViewById(R.id.segmentedBar);
        meTabBtn = findViewById(R.id.meTabBtn);
        classTabBtn = findViewById(R.id.classTabBtn);
        backBtn = findViewById(R.id.backBtn);
        segmentedBar.setTintColor(getResources().getColor(R.color.colorPrimary));
        meTabBtn.setTextColor(Color.WHITE);
        classTabBtn.setTextColor(Color.WHITE);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        //instantiation of fragments and arraylists for timeline data
        meFragment = new MeFragment();
        studentTimelineFragment = new StudentTimelineFragment();
        meValues = new ArrayList<>();
        classAverages = new ArrayList<>();


        //send timeline data to StudentTimelineFragment
        Bundle bundle = new Bundle();
        bundle.putString("uID", getIntent().getStringExtra("uID"));
        Log.d("TEST", "put bundle: " + getIntent().getStringExtra("uID"));
        meFragment.setArguments(bundle);
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
                fragmentTransaction.replace(R.id.constraintLayout,
                        studentTimelineFragment);
                fragmentTransaction.commit();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseUtils.checkIsTakingAttendance(FirebaseUtils.getMySection());
    }

    //Methods to pass values from StudentClassActivity to StudentTimelineFragment

    //when called, passes an ArrayList of a user's slider values
    //Called in StudentTimelineFragment to render graph
    public ArrayList<Entry> getMeValues() {
        return meValues;
    }

    //when called, passes an ArrayList of a sections's slider value averages
    //Called in StudentTimelineFragment to render graph
    public ArrayList<Entry> getClassValues() {
        return classAverages;
    }

    //Called in StudentTimelineFragment to get the section start time
    public String getStartTime() { return FirebaseUtils.getStartTime(FirebaseUtils.getMySection());}
    //Called in StudentTimelineFragment to get the section end time
    public String getEndTime() {
        return FirebaseUtils.getEndTime(FirebaseUtils.getMySection());
    }

}
