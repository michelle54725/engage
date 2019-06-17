package com.mao.engage.student;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.abs;


public class MeFragment extends Fragment {

    SeekBar seekBar;
    int sliderValue;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "uID";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uID = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        seekBar = view.findViewById(R.id.seekBar);

        final Drawable original = getResources().getDrawable(R.drawable.ic_slider_thumb);
        seekBar.post(new Runnable() {
            @Override
            public void run() {

                ScaleDrawable test = new ScaleDrawable(original, 0, 0, 1);
                Drawable d = test;
                d.setLevel((seekBar.getHeight() - 1) * 10 / 3);
                Log.d("BOBOOBO3BO", "RUNN: " + seekBar.getHeight() + " " );
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
        Log.d("BOBOOBO2BO", "onCreateView: " + seekBar.getHeight());
    }

    private int elasticFunction(int current) {
        double next = (current + 50) / 2;
        if(abs(next - 50) <= 1) {
            return 50;
        }
        return (int) next;
    }


    public static void startSendingMessages() {
        Log.d("TEST: ", "start sending messages! here is my message " + FirebaseUtils.getPsuedoUniqueID());
        message = new Message(FirebaseUtils.getPsuedoUniqueID().getBytes());
        Nearby.getMessagesClient(context).publish(message);
    }

    public static void stopSendingMessages() {
        Log.d("TEST: ", "stop sending messages! " + context);
        Nearby.getMessagesClient(context).unpublish(message);
    }

    void setSlider() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("ADAS", "onProgressChanged: " + sliderValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                elastic = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                FirebaseUtils.setSliderVal(uID, seekBar.getProgress());
            }
        });
    }
}
