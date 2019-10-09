/*
    Activity that manages and contains the fragments (timeline and attendance) associated with the teacher view
    Triggered by: intent call from section adapter.
 */

package com.engage.education.teacherclassactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;

import com.engage.education.FirebaseUtils;
import com.engage.education.R;
import com.engage.education.teacher.TeacherOptionsActivity;

import java.util.ArrayList;
import java.util.Calendar;

import info.hoang8f.android.segmented.SegmentedGroup;

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
    String mMagicKey;
    Handler toasty;

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
        mMagicKey = getIntent().getStringExtra("magic_word");
        ArrayList<Integer> timelineData = new ArrayList();
        bundle.putString("section_name", getIntent().getStringExtra("section_name"));
        mSectionRefKey = getIntent().getStringExtra("sectionRefKey");
        bundle.putString("sectionRefKey", getIntent().getStringExtra("sectionRefKey"));
        bundle.putIntegerArrayList("timelinedata", timelineData);
        bundle.putString("start_time", getIntent().getStringExtra("start_time"));
        bundle.putString("end_time", getIntent().getStringExtra("end_time"));
        attendanceFragment.setArguments(bundle);
        timelineFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.constraintLayout, timelineFragment);
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
        toasty.postDelayed(toastTask, diffTimestamp);


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

        endSectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toasty.post(toastTask);
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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TeacherClassActivity.this, TeacherOptionsActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
        //finish();
    }

    public Runnable toastTask = new Runnable() {
        public void run() {
            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherClassActivity.this);
            builder.setTitle("Section has ended!");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    FirebaseUtils.removeAllUsers(mSectionRefKey);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.constraintLayout, timelineFragment);
                    fragmentTransaction.commit();

                    Intent intent = new Intent(TeacherClassActivity.this, TeacherOptionsActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                    FirebaseUtils.removeSection(mSectionRefKey, FirebaseUtils.getPsuedoUniqueID());
                    FirebaseUtils.removeMagicKey(mMagicKey);

                }
            });
            builder.show();
        };
    };
}
