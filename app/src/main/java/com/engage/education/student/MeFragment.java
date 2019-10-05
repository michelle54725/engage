/*
    Called in StudentClassActivity to allow students to send signals for attendance

    Triggered by:
    checkIsTakingAttendance in FirebaseUtils to start sending messages
 */

package com.engage.education.student;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.engage.education.FirebaseUtils;
import com.engage.education.R;

import java.util.Timer;
import java.util.TimerTask;

import static com.engage.education.student.MeFragment_findRefKey_writeSliderValKt.MeFragment_findRefKey_writeSliderVal;
import static java.lang.Math.abs;


public class MeFragment extends Fragment {

    SeekBar seekBar;
    int sliderValue;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String UID_STRING = "uID";
    private static final String PARAM2_STRING = "param2";

    private String uID;
    private String mParam2;

    private Boolean elastic;
    private static Message message;
    private static Context context;
    public MeFragment() {
        // Required empty public constructor
        context = getActivity();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString(UID_STRING, param1);
        args.putString(PARAM2_STRING, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uID = getArguments().getString(UID_STRING);
            mParam2 = getArguments().getString(PARAM2_STRING);
        }
        context = getActivity();
    }

    // Inflates the layout for this fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        seekBar = view.findViewById(R.id.seekBar);

        final Drawable original = getResources().getDrawable(R.drawable.ic_slider_thumb);
        seekBar.post(new Runnable() {
            @Override
            public void run() {

                ScaleDrawable test = new ScaleDrawable(original, 0, 0, 1);
                Drawable d = test;
                d.setLevel((seekBar.getHeight() - 1) * 10 / 3);
                seekBar.setThumb(d);
            }
        });
        elastic = false;

        setSlider();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (elastic) {
                    seekBar.setProgress(elasticFunction(seekBar.getProgress()));
                }
            }

        },0,10);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private int elasticFunction(int current) {
        double next = (current + 50) / 2;
        if(abs(next - 50) <= 1) {
            return 50;
        }
        return (int) next;
    }


    //Google Nearby high frequency attendance function

    //send high frequency messages from user's phone automatically when teacher calls attendance
    public static void startSendingMessages() {
        message = new Message(FirebaseUtils.getPsuedoUniqueID().getBytes());
        Nearby.getMessagesClient(context).publish(message);
    }
    //ends messages when teacther clicks their "Stop Taking Attendance" button
    public static void stopSendingMessages() {
        Nearby.getMessagesClient(context).unpublish(message);
    }

    void setSlider() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                elastic = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //FirebaseUtils.setSliderVal(uID, seekBar.getProgress());
                MeFragment_findRefKey_writeSliderVal(uID, seekBar.getProgress());
            }
        });
    }
}