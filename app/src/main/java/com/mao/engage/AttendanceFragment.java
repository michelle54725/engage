package com.mao.engage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class AttendanceFragment extends Fragment implements View.OnClickListener {

    private TextView sectionNameText;
    private TextView magicWordText;
    private TextView studentCount;
    private TextView students;
    private String mSectionRefKey;
    private static Context context;
    private Button attendanceButton;
    private Button whosHereButton;
    boolean attendancePressed = false;
    MessageListener mMessageListener;
    Message mMessage;
    ArrayList<String> mMessages;

//    private OnFragmentInteractionListener mListener;

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
        mMessages = new ArrayList<>();
        context = getActivity();

        if (getArguments() != null) {
            sectionNameText.setText(getArguments().getString("section_name"));
            magicWordText.setText(String.format("Magic word: %s", getArguments().getString("magic_word")));
            mSectionRefKey = getArguments().getString("sectionRefKey");
        }

        attendanceButton = view.findViewById(R.id.attendanceButton);
        whosHereButton = view.findViewById(R.id.see_whos_here);
        attendanceButton.setOnClickListener(this);
        whosHereButton.setOnClickListener(this);
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

    @Override
    public void onStop() {
        Nearby.getMessagesClient(this.getActivity()).unpublish(mMessage);
        Nearby.getMessagesClient(this.getActivity()).unsubscribe(mMessageListener);
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.attendanceButton:
                if (attendancePressed) {
                    attendanceButton.setBackground(getResources().getDrawable(R.drawable.attendance_button));
                    attendanceButton.setText(R.string.take_attendance);
                } else {
                    attendanceButton.setBackground(getResources().getDrawable(R.drawable.attendance_button2));
                    attendanceButton.setText(R.string.stop_attendance);
                    Log.d("TEST", "studentcount");
                    studentCount.setText(Integer.toString(FirebaseUtils.getUserNames(mSectionRefKey).size()));
                    studentCount.setVisibility(View.VISIBLE);
                    students.setVisibility(View.VISIBLE);
                }
                attendancePressed = !attendancePressed;
                FirebaseUtils.setIsTakingAttendance(mSectionRefKey, attendancePressed);
                Nearby.getMessagesClient(this.getActivity()).publish(mMessage);
                Nearby.getMessagesClient(this.getActivity()).subscribe(mMessageListener);
                break;
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

//    protected static void refreshCount() {
//        if (context != null) {
//            studentCount = (TextView) findViewById(R.id.studentCount);
//            studentCount.setText(FirebaseUtils.getUserNames(mSectionRefKey).size());
//        }
//    }
}
