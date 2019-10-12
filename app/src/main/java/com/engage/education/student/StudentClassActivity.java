/**
 * StudentClassActivity: primary student interface
 *  - Contains 2 fragments: MeFragment and StudentTimelineFragment
 *
 * Triggered by:
 *  "JOIN CLASS" button in StudentLoginActivity
 *
 * Transitions to:
 *  (None)
 */
package com.engage.education.student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RadioButton;
import com.github.mikephil.charting.data.Entry;

import com.engage.education.FirebaseUtils;
import com.engage.education.R;
import com.engage.education.teacher.StudentLoginActivity;

import java.util.ArrayList;
import java.util.Calendar;

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
    String endTime;
    String mSectionRefKey;
    String name;
    Handler toasty;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI: set to landscape, notification bar hidden
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
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
        name = getIntent().getStringExtra("name");


        //instantiation of fragments and arraylists for timeline data
        meFragment = new MeFragment();
        studentTimelineFragment = new StudentTimelineFragment();
        meValues = new ArrayList<>();
        classAverages = new ArrayList<>();

        //Handler to call toast after section is over!
        mSectionRefKey = FirebaseUtils.sectionRefKey;
        endTime = FirebaseUtils.getEndTime(mSectionRefKey);
        Calendar calendar = Calendar.getInstance();
        long currentTimestamp = calendar.getTimeInMillis();

        String[] parsedComponents = FirebaseUtils.parseTime(endTime);
        int desiredHour = Integer.parseInt(parsedComponents[0]);
        int desiredMinute = Integer.parseInt(parsedComponents[1]);
        String signature = parsedComponents[2];

        if (signature.toLowerCase().equals("pm")) {
            calendar.set(Calendar.HOUR_OF_DAY, desiredHour + 12);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, desiredHour);
        }

        calendar.set(Calendar.MINUTE, desiredMinute);
        calendar.set(Calendar.SECOND, 0);

        long calendarTime = calendar.getTimeInMillis();
        long diffTimestamp = calendarTime - currentTimestamp;

        toasty = new Handler();
        toasty.postDelayed(toastTask, diffTimestamp);

        FirebaseUtils.setUserIdinSectionListener(mSectionRefKey);

        //send timeline data to StudentTimelineFragment
        Bundle bundle = new Bundle();
        bundle.putString("uID", getIntent().getStringExtra("uID"));
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
                FirebaseUtils.removeUser(FirebaseUtils.getMySection(), FirebaseUtils.getPsuedoUniqueID());
                studentTimelineFragment.cancelTimer();
                finish();
            }
        });

        FirebaseUtils.checkIsTakingAttendance(FirebaseUtils.getMySection());
    }

    public Runnable toastTask = new Runnable() {
        public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(StudentClassActivity.this);
            builder.setTitle("Section has ended!");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(StudentClassActivity.this, StudentLoginActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            });
            builder.show();
        };
    };
    @Override
    public void onBackPressed() {
        FirebaseUtils.removeUser(FirebaseUtils.getMySection(), FirebaseUtils.getPsuedoUniqueID());
        studentTimelineFragment.cancelTimer();
        finish();
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
    public String getStartTime() {
        return FirebaseUtils.getStartTime(FirebaseUtils.getMySection());
    }
    //Called in StudentTimelineFragment to get the section end time
    public String getEndTime() {
        return FirebaseUtils.getEndTime(FirebaseUtils.getMySection());
    }

}
