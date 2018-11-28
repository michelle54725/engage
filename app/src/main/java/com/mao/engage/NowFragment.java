package com.mao.engage;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.Time;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class NowFragment extends Fragment {

    private TextView sectionNameText;
    private TextView magicWordText;

    private BarChart barChart;
    private PieChart engagedPieChart;
    private PieChart disengagedPieChart;

    private SeekBar thresholdBar;

    TimerTask retrieveDataTask;

    public NowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now, container, false);

        sectionNameText = view.findViewById(R.id.sectionNameText);
        magicWordText = view.findViewById(R.id.magicWordText);
        if (getArguments() != null) {
            sectionNameText.setText(getArguments().getString("section_name"));
            Log.d("TEST-MAGIC", "in NowFragment: " + getArguments().getString("magic_word"));
            magicWordText.setText(String.format("Magic word: %s", getArguments().getString("magic_word")));

        }

        barChart = view.findViewById(R.id.engagedBar);
        engagedPieChart = view.findViewById(R.id.engagedPieChart);
        disengagedPieChart = view.findViewById(R.id.disengagedPieChart);
        thresholdBar = view.findViewById(R.id.thresholdSeekBar);

        thresholdBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                retrieveData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        barChart.setViewPortOffsets(0f, 0f, 0f, 0);
        retrieveDataTask = new TimerTask() {
            @Override
            public void run() {
                Log.d("TEST", "TIMER WORKING...");
                retrieveData();
            }
        };
        new Timer().scheduleAtFixedRate(retrieveDataTask, 0, 3000);

        return view;
    }

    private void retrieveData() {


        // get all Slider values
        ArrayList<Integer> individualEngagements = new ArrayList<>();
        for (String user : FirebaseUtils.sectionSliders.keySet()) {
            individualEngagements.add(FirebaseUtils.sectionSliders.get(user));
        }

//                {
//                5, 5,
//                15, 15, 15, 15,
//                25, 25, 25, 25, 25, 25, 25, 25,
//                35, 35, 35, 35, 35, 35, 35, 35, 35, 35, 35,
//                45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45, 45,
//                55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55,
//                65, 65, 65, 65, 65, 65, 65, 65, 65, 65,
//                75, 75, 75, 75, 75, 75, 75, 75, 75,
//                85, 85, 85, 85,
//                95
//                };

        int[] countsArray = new int[10];
        for(int engagement : individualEngagements) {
            countsArray[engagement / 10] += 1;
        }

        List<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < countsArray.length; i++) {
            entries.add(new BarEntry(i, countsArray[i]));
        }

        BarDataSet disengagedBarSet = new BarDataSet(entries.subList(0, thresholdBar.getProgress()), "BarDataSet");
        disengagedBarSet.setColor(getResources().getColor(R.color.colorAccentRed));
        BarDataSet engagedBarSet = new BarDataSet(entries.subList(thresholdBar.getProgress(), entries.size()), "BarDataSet");
        engagedBarSet.setColor(getResources().getColor(R.color.colorAccentBlue));

        int engagedStudents = 0;
        for(int i = thresholdBar.getProgress(); i < countsArray.length; i++) {
            engagedStudents += countsArray[i];
        }
        int disengagedStudents = 0;
        for(int i = 0; i < thresholdBar.getProgress(); i++) {
            disengagedStudents += countsArray[i];
        }

        List<PieEntry> engagementEntries = new ArrayList<>();
        engagementEntries.add(new PieEntry(disengagedStudents));
        engagementEntries.add(new PieEntry(engagedStudents));

        Log.d("SEEKBAR", "retrieveData: " + thresholdBar.getProgress());

        BarData barData = new BarData();
        barData.addDataSet(engagedBarSet);
        barData.addDataSet(disengagedBarSet);
        barData.setDrawValues(false);

        barChart.setData(barData);
        barChart.setTouchEnabled(false);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setDrawLabels(false);
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(false);

        barChart.getAxisRight().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.fitScreen();

        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        barChart.invalidate(); // refresh

        PieDataSet engagedSet = new PieDataSet(engagementEntries, "EngagedPieSet");
        engagedSet.setColors(Color.TRANSPARENT, getResources().getColor(R.color.colorAccentBlue));
        PieData engagedData = new PieData(engagedSet);
        engagedData.setDrawValues(false);
        engagedPieChart.setData(engagedData);
        engagedPieChart.setTouchEnabled(false);
        engagedPieChart.setHoleColor(Color.TRANSPARENT);
        engagedPieChart.setHoleRadius(75);
        engagedPieChart.setTransparentCircleRadius(0);

        String engagedStudentsString = String.valueOf(engagedStudents);
        SpannableString engagedSpannable = new SpannableString(engagedStudentsString + "\nstudents");
        engagedSpannable.setSpan(new RelativeSizeSpan(2.5f), 0, engagedStudentsString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        engagedPieChart.setCenterText(engagedSpannable);
        engagedPieChart.setCenterTextSize(18);
        engagedPieChart.setCenterTextColor(Color.WHITE);
        engagedPieChart.setCenterTextTypeface(ResourcesCompat.getFont(getContext(), R.font.quicksand_bold));
        engagedPieChart.getLegend().setEnabled(false);
        engagedPieChart.getDescription().setEnabled(false);
        engagedPieChart.invalidate();

        PieDataSet disengagedSet = new PieDataSet(engagementEntries, "DisengagedPieSet");
        disengagedSet.setColors(getResources().getColor(R.color.colorAccentRed), Color.TRANSPARENT);
        PieData disengagedData = new PieData(disengagedSet);
        disengagedData.setDrawValues(false);
        disengagedPieChart.setData(disengagedData);
        disengagedPieChart.setTouchEnabled(false);
        disengagedPieChart.setHoleColor(Color.TRANSPARENT);
        disengagedPieChart.setHoleRadius(75);
        disengagedPieChart.setTransparentCircleRadius(0);

        String disengagedStudentsString = String.valueOf(disengagedStudents);
        SpannableString disengagedSpannable = new SpannableString(disengagedStudentsString + "\nstudents");
        disengagedSpannable.setSpan(new RelativeSizeSpan(2.5f), 0, disengagedStudentsString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        disengagedPieChart.setCenterText(disengagedSpannable);
        disengagedPieChart.setCenterTextSize(18);
        disengagedPieChart.setCenterTextColor(Color.WHITE);
        disengagedPieChart.setCenterTextTypeface(ResourcesCompat.getFont(getContext(), R.font.quicksand_bold));
        disengagedPieChart.getLegend().setEnabled(false);
        disengagedPieChart.getDescription().setEnabled(false);
        disengagedPieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("TEST", "onDestroyView: Destroying View & TimerTask");
        retrieveDataTask.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TEST", "onStop: Stopping & Destroying TimerTask");
        retrieveDataTask.cancel();
    }
}
