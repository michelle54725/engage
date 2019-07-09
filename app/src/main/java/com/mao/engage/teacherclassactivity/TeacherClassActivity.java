/*
    Activity that manages and contains the fragments (timeline and attendance) associated with the teacher view
    Triggered by: intent call from section adapter.
 */

package com.mao.engage.teacherclassactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import info.hoang8f.android.segmented.SegmentedGroup;

public class TeacherClassActivity extends AppCompatActivity implements TimelineFragment.OnFragmentInteractionListener{

    // will store information passed on from SectionAdapter to this activity
    String mSectionRefKey;
    // items related to UI design
    SegmentedGroup segmentedBar;
    RadioButton nowTabBtn;
    RadioButton timelineTabBtn;

    FragmentManager fragmentManager;
    AttendanceFragment attendanceFragment;
    TimelineFragment timelineFragment;
    String endTime;
    Activity me;

    //NowFragment nowFragment; //now fragment is not used anymore

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

        //nowFragment = new NowFragment(); // not used anymore

        /*
            sends information (magic word, section name, section ref key, and timeline data)
            to attendance and timeline fragment in a bundle
        */
        attendanceFragment = new AttendanceFragment();
        timelineFragment = new TimelineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("magic_word", getIntent().getStringExtra("magic_word"));
        ArrayList<Integer> timelineData = new ArrayList();
        bundle.putString("section_name", getIntent().getStringExtra("section_name"));
        mSectionRefKey = getIntent().getStringExtra("sectionRefKey");
        bundle.putString("sectionRefKey", getIntent().getStringExtra("sectionRefKey"));
        bundle.putIntegerArrayList("timelinedata", timelineData);
        bundle.putString("start_time", getIntent().getStringExtra("start_time"));
        bundle.putString("end_time", getIntent().getStringExtra("end_time"));
        attendanceFragment.setArguments(bundle);
        timelineFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.constraintLayout, attendanceFragment);
        fragmentTransaction.commit();

        FirebaseUtils.setUserIdinSectionListener(mSectionRefKey);

        endTime = FirebaseUtils.getEndTime(mSectionRefKey);

        //Handler to call toast after section is over!
        Calendar calendar = Calendar.getInstance();
        long currentTimestamp = calendar.getTimeInMillis();
        int desiredHour = Integer.parseInt(endTime.substring(0,2));
        Log.d("TEST", "AF desired hour: " + desiredHour);
        int desiredMinute = Integer.parseInt(endTime.substring(3,5));
        Log.d("TEST", "AF desired minute: " + desiredMinute);
        if (endTime.substring(5,7).toLowerCase().equals("pm")) {
            calendar.set(Calendar.HOUR_OF_DAY, desiredHour + 12);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, desiredHour);
        }
        calendar.set(Calendar.MINUTE, desiredMinute);
        calendar.set(Calendar.SECOND, 0);
        long diffTimestamp = calendar.getTimeInMillis() - currentTimestamp;
        Log.d("TEST", "AF current: " + currentTimestamp + " end: " + calendar.getTimeInMillis() + " Diff: " + diffTimestamp);
        Log.d("TEST", "AF myDelay: " + diffTimestamp);
        final Handler toasty = new Handler();
        toasty.postDelayed(toastTask, diffTimestamp);
        Log.d("TEST", "AF cancelled 1");


        //sets triggers for the two buttons on our screen that link to each individual fragment
        nowTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.constraintLayout, attendanceFragment);
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

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("BOBOB", "onFragmentInteraction: " + uri.toString());
    }

    public Runnable toastTask = new Runnable() {
        public void run() {
            Log.d("TEST", "toastTask");
            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherClassActivity.this);
            builder.setTitle("Section has ended!");
            builder.setMessage("Would you like to save your graph?");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Log.d("TEST", "selected no save: toast");
                    FirebaseUtils.removeAllUsers(mSectionRefKey);
                    //FirebaseUtils.removeSection(FirebaseUtils.getMySection());

                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //takeScreenshot();
                    Log.d("TEST", "selected save graph: toast");
                    FirebaseUtils.removeAllUsers(mSectionRefKey);
                    //FirebaseUtils.removeSection(FirebaseUtils.getMySection());
                }
            });
            builder.show();
        };
    };
}
