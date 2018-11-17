package com.mao.engage;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NowFragment extends Fragment {

    private BarChart barChart;
    private PieChart engagedPieChart;
    private PieChart disengagedPieChart;

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
        barChart = view.findViewById(R.id.barChart);
        engagedPieChart = view.findViewById(R.id.engagedPieChart);
        disengagedPieChart = view.findViewById(R.id.disengagedPieChart);

        retrieveData();

        return view;
    }

    private void retrieveData() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 30f));
        entries.add(new BarEntry(1f, 80f));
        entries.add(new BarEntry(2f, 60f));
        entries.add(new BarEntry(3f, 50f));
        // gap of 2f
        entries.add(new BarEntry(5f, 70f));
        entries.add(new BarEntry(6f, 60f));

        //TODO: make these values like real
        int totalStudents = new Random().nextInt(100);
        int engagedStudents = new Random().nextInt(totalStudents);
        int disengagedStudents = totalStudents - engagedStudents;

        List<PieEntry> engagementEntries = new ArrayList<>();
        engagementEntries.add(new PieEntry(disengagedStudents));
        engagementEntries.add(new PieEntry(engagedStudents));

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
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
