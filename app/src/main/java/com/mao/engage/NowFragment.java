package com.mao.engage;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NowFragment extends Fragment {

    private BarChart barChart;
    private PieChart engagedPieChart;
    private PieChart disengagedPieChart;

    private SeekBar thresholdBar;

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
        barChart = view.findViewById(R.id.engagedBar);
        engagedPieChart = view.findViewById(R.id.engagedPieChart);
        disengagedPieChart = view.findViewById(R.id.disengagedPieChart);
        thresholdBar = view.findViewById(R.id.thresholdSeekBar);

        retrieveData();

        return view;
    }

    private void retrieveData() {

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 1));
        entries.add(new BarEntry(1f, 40));
        entries.add(new BarEntry(2f, 40));
        entries.add(new BarEntry(3f, 40));
        entries.add(new BarEntry(4f, 40));
        entries.add(new BarEntry(5f, 40));
        entries.add(new BarEntry(6f, 40));
        entries.add(new BarEntry(7f, 40));
        entries.add(new BarEntry(8f, 80));
        entries.add(new BarEntry(9f, 100));

        //TODO: make these values like real
        int totalStudents = new Random().nextInt(100);
        int engagedStudents = new Random().nextInt(totalStudents);
        int disengagedStudents = totalStudents - engagedStudents;

        List<PieEntry> engagementEntries = new ArrayList<>();
        engagementEntries.add(new PieEntry(disengagedStudents));
        engagementEntries.add(new PieEntry(engagedStudents));

        BarDataSet engagedBarSet = new BarDataSet(entries.subList(0, 5), "BarDataSet");
        engagedBarSet.setColor(getResources().getColor(R.color.colorAccentRed));

        BarDataSet disengagedBarSet = new BarDataSet(entries.subList(5, entries.size()), "BarDataSet");
        disengagedBarSet.setColor(getResources().getColor(R.color.colorAccentBlue));

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

        barChart.setDrawBorders(true);
        barChart.setViewPortOffsets(0f, 0f, 0f, 0);
        barChart.fitScreen();

        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barChart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

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
}
