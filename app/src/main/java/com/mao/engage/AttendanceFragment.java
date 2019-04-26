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


public class AttendanceFragment extends Fragment implements View.OnClickListener {

    private TextView sectionNameText;
    private TextView magicWordText;
    private TextView studentCount;
    private TextView students;
    private String mSectionRefKey;
    private Button attendanceButton;
    private Button whosHereButton;
    boolean attendancePressed = false;

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

        if (getArguments() != null) {
            sectionNameText.setText(getArguments().getString("section_name"));
            magicWordText.setText(String.format("Magic word: %s", getArguments().getString("magic_word")));
            mSectionRefKey = getArguments().getString("sectionRefKey");
        }

        attendanceButton = view.findViewById(R.id.attendanceButton);
        whosHereButton = view.findViewById(R.id.see_whos_here);
        attendanceButton.setOnClickListener(this);
        whosHereButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
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
                    studentCount.setVisibility(View.VISIBLE);
                    students.setVisibility(View.VISIBLE);
                }
                attendancePressed = !attendancePressed;
                break;
            case R.id.see_whos_here:
                Intent intent = new Intent(getActivity(), AttendeeListActivity.class);
                startActivity(intent);
               break;
            default:
                Log.d("TEST:","Button not accounted for");
                break;
        }

    }
}
