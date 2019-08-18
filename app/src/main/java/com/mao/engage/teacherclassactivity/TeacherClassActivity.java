/*
    Activity that manages and contains the fragments (timeline and attendance) associated with the teacher view
    Triggered by: intent call from section adapter.
 */

package com.mao.engage.teacherclassactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;

import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;
import com.mao.engage.UserConfig;
import com.mao.engage.teacher.TeacherCreateClassActivity;
import com.mao.engage.teacher.TeacherOptionsActivity;
import com.mao.engage.ui.LottieToast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import info.hoang8f.android.segmented.SegmentedGroup;

import static com.mao.engage.teacherclassactivity.TimelineFragment.getBitmapFromView;
import static com.mao.engage.teacherclassactivity.TimelineFragment.takeScreenshot;

public class TeacherClassActivity extends AppCompatActivity implements TimelineFragment.OnFragmentInteractionListener{

    // will store information passed on from SectionAdapter to this activity
    String mSectionRefKey;
    // items related to UI design
    SegmentedGroup segmentedBar;
    RadioButton nowTabBtn;
    RadioButton timelineTabBtn;
    Button endSectionBtn;

    FragmentManager fragmentManager;
    AttendanceFragment attendanceFragment;
    TimelineFragment timelineFragment;
    String endTime;
    String name;
    Handler toasty;
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
        endSectionBtn = findViewById(R.id.endSectionBtn);
        name = getIntent().getStringExtra("name");
        Log.d("TEST", "firstname: " + name);

        segmentedBar.setTintColor(getResources().getColor(R.color.colorPrimary));
        nowTabBtn.setTextColor(Color.WHITE);
        timelineTabBtn.setTextColor(Color.WHITE);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        me = this;
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
        int desiredMinute = Integer.parseInt(endTime.substring(3,5));
        if (endTime.substring(5,7).toLowerCase().equals("pm")) {
            calendar.set(Calendar.HOUR_OF_DAY, desiredHour + 12);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, desiredHour);
        }
        calendar.set(Calendar.MINUTE, desiredMinute);
        calendar.set(Calendar.SECOND, 0);
        long diffTimestamp = calendar.getTimeInMillis() - currentTimestamp;
        toasty = new Handler();

        //sets triggers for the two buttons on our screen that link to each individual fragment
        nowTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFragmentTransaction(attendanceFragment);
            }
        });
        timelineTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFragmentTransaction(timelineFragment);
            }
        });
    }

    private void handleFragmentTransaction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.constraintLayout, fragment, fragment.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("BOBOB", "onFragmentInteraction: " + uri.toString());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TeacherClassActivity.this, TeacherOptionsActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
        //finish();
    }


}
