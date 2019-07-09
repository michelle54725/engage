/*
    Fragment associated with attendance page that teacher views.
    This page is opened from TeacherClassActivity and is called by FirebaseUtils to update students in the class.
 */

package com.mao.engage.teacherclassactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class AttendanceFragment extends Fragment implements View.OnClickListener {

    private static AttendanceFragment mFragment;
    private static String mSectionRefKey;
    private static Context context;
    boolean attendancePressed = false;
    MessageListener mMessageListener;
    Message mMessage;
    ArrayList<String> mMessages;
    String endTime;
    Activity me;

    //ui design
    private TextView sectionNameText;
    private TextView magicWordText;
    private static TextView studentCount;
    private TextView students;
    private static int count;
    private Button whosHereButton;
    private Button attendanceButton;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        sectionNameText = view.findViewById(R.id.sectionNameText3);
        magicWordText = view.findViewById(R.id.magicWordText3);
        studentCount = view.findViewById(R.id.studentCount);
        students = view.findViewById(R.id.students_text);
        attendanceButton = view.findViewById(R.id.attendanceButton);
        whosHereButton = view.findViewById(R.id.see_whos_here);

        mFragment = AttendanceFragment.this;
        mMessages = new ArrayList<>();
        context = getActivity();

        //need to check if arguments are null to avoid errors when spamming the app
        if (getArguments() != null) {
            sectionNameText.setText(getArguments().getString("section_name"));
            magicWordText.setText(String.format("Magic word: %s", getArguments().getString("magic_word")));
            mSectionRefKey = getArguments().getString("sectionRefKey");
        }

        attendanceButton.setOnClickListener(this);
        whosHereButton.setOnClickListener(this);
        count = FirebaseUtils.getUserNames(mSectionRefKey).size();
        studentCount.setText(Integer.toString(count));

        endTime = FirebaseUtils.getEndTime(mSectionRefKey);

        /*
        The teacher listens to messages sent by students. The message contains the user id of the student.
        Each user id is appended to a list called mMessages.
        */
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                String user_id = new String(message.getContent());
                Log.d("TEST: ", "Found message: " + user_id);
                if(!mMessages.contains(user_id)) {
                    FirebaseUtils.updateUserAttendance(mSectionRefKey, user_id);
                }
                mMessages.add(user_id);
            }

            @Override
            public void onLost(Message message) {
                Log.d("TEST: ", "Lost sight of message: " + new String(message.getContent()));
            }
        };
        mMessage = new Message("Hello World".getBytes());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /*
        It is necessary to unpublish and unsubscribe messages once you exit this current activity to reduce battery loss
     */
    @Override
    public void onStop() {
        Nearby.getMessagesClient(this.getActivity()).unpublish(mMessage);
        Nearby.getMessagesClient(this.getActivity()).unsubscribe(mMessageListener);
        super.onStop();
    }

    /*
        onClick is triggered when either the attendance button or see_whos_here button is clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //when attendance button is pressed, the student count is displayed
            //and we start taking attendance by publishing and subscribing messages via google nearby
            case R.id.attendanceButton:
                if (attendancePressed) {
                    attendanceButton.setBackground(getResources().getDrawable(R.drawable.attendance_button));
                    attendanceButton.setText(R.string.take_attendance);
                    count = FirebaseUtils.getUserNames(mSectionRefKey).size();
                } else {
                    attendanceButton.setBackground(getResources().getDrawable(R.drawable.attendance_button2));
                    attendanceButton.setText(R.string.stop_attendance);
                    Log.d("TEST", "studentcount: " + Integer.toString(FirebaseUtils.getUserNames(mSectionRefKey).size()));
                    //studentCount.setText(Integer.toString(FirebaseUtils.getUserNames(mSectionRefKey).size()));
                    studentCount.setVisibility(View.VISIBLE);
                    students.setVisibility(View.VISIBLE);
                }
                attendancePressed = !attendancePressed;
                FirebaseUtils.setIsTakingAttendance(mSectionRefKey, attendancePressed);
                Nearby.getMessagesClient(this.getActivity()).publish(mMessage);
                Nearby.getMessagesClient(this.getActivity()).subscribe(mMessageListener);
                break;
            //the see_whos_here button takes the user to a new page, AttendeeListActivity.
            case R.id.see_whos_here:
                Log.d("TEST", "SEE WHO IS HERE BUTTON GOOGLE NEARBY MESSAGES");
                Intent intent = new Intent(getActivity(), AttendeeListActivity.class);
                intent.putExtra("sectionRefKey", mSectionRefKey);
                startActivity(intent);
               break;
            default:
                Log.d("TEST:","Button not accounted for");
                break;
        }

    }


    /*
        Purpose of this method is to refresh the number of students in the class.
        This does not currently work as intended, but is called in FirebaseUtils whenever a student is added to the class.
     */
    public static void refreshCount() {
        if (context != null) {
            Log.d("TEST", "before refreshCount: " + count);
            count = FirebaseUtils.getUserNames(mSectionRefKey).size();
            studentCount.setText(Integer.toString(count));
            Log.d("TEST", " after refreshCount: " + count);
        }
    }

}
