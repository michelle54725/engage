package com.mao.engage;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class StudentTimelineFragment extends Fragment {

    private ArrayList<Entry> meValues;
    private ArrayList<Entry> classValues;
    private ArrayList<Integer> meColors;
    private ArrayList<Integer> classColors;
    private StudentClassActivity activity;
    private int index;

    private LineDataSet meSet;
    private LineDataSet classSet;
    private String startTime;
    private String endTime;

    private LineData lineData;
    private LineChart chart;

    private TextView startTimeText;
    private TextView endTimeText;

    private ConstraintLayout circleWrapper;
    private TextView engagedCountText;

    TimerTask retrieveDataTask;
    private TimelineDataRetrieval dataRetrieval;

    public StudentTimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);
        chart = view.findViewById(R.id.chart);
        startTimeText = view.findViewById(R.id.startTimeText);
        endTimeText = view.findViewById(R.id.endTimeText);
        circleWrapper = view.findViewById(R.id.circleWrapper);
        engagedCountText = view.findViewById(R.id.engagedCount);
        activity = (StudentClassActivity) getActivity();

        //initiating lists of data
        meValues = activity.getMeValues();
        classValues = activity.getClassValues();

        //initiating lines for graph
        meColors = new ArrayList<>();
        classColors = new ArrayList<>();

        retrieveDataTask = new TimerTask() {
            int test_val = 0; //for testing
            @Override
            public void run() {
                Log.d("TEST", "TIMER WORKING..." + test_val++);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        retrieveData();
                        index++;
                        Log.d("TEST", "INDEX: " + index);
                    }
                });
            }
        };
        new Timer().scheduleAtFixedRate(retrieveDataTask, 0, 5000);

        startTime = activity.getStartTime();
        endTime = activity.getEndTime();

        startTimeText.setText(startTime);
        endTimeText.setText(endTime);

        setEngagedCount();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void retrieveData() {
        //initiating lines for graph
        meColors = new ArrayList<>();
        classColors = new ArrayList<>();

        dataRetrieval = new TimelineDataRetrieval();

        int d = dataRetrieval.dataRandom();
        meValues.add(new Entry(index, d));
        Log.d("TEST", "Me value: " + d);
        Log.d("TEST", "Me Value size: " + meValues.size());


        ArrayList<Integer> list = dataRetrieval.createRandomStudentData(150);
        int avg = Math.round(dataRetrieval.calculateAverageData(list));
        classValues.add(new Entry(index, avg));
        Log.d("TEST", "Class avg: " + avg);
        Log.d("TEST", "ClassValues size:" + classValues.size());

        for (int i = 0; i < meValues.size(); i++) {
            meColors.add(Color.TRANSPARENT);
            classColors.add(Color.TRANSPARENT);
        }

        Log.d("TEST", "meColor size: " + meColors.size());
        meColors.remove(meColors.size() - 1);
        meColors.add(Color.WHITE);

        Log.d("TEST", "classColors size: " + classColors.size());
        classColors.remove(classColors.size() - 1);
        classColors.add(getResources().getColor(R.color.colorAccentBlue));

        meSet = new LineDataSet(meValues, "Me");
        classSet = new LineDataSet(classValues, "Class");
        Log.d("TEST", "initialized meSet and classSet");
        Log.d("TEST", "meSet:" + meSet.getEntryCount());
        Log.d("TEST", "classSet: " + classSet.getEntryCount());

        meSet.setLineWidth(2f);
        meSet.setColor(Color.WHITE);
        meSet.setCircleColors(meColors);
        meSet.setCircleRadius(3f);
        meSet.setDrawCircleHole(false);
        meSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (entry == meSet.getEntryForIndex(meSet.getEntryCount() - 1)) {
                    Log.d("TEST", "get last label");
                    return String.valueOf(entry.getY());
                }  else {
                    return "";
                }
            }
        });
        meSet.setValueTextColor(Color.WHITE);
        meSet.setValueTextSize(12f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            meSet.setValueTypeface(getResources().getFont(R.font.quicksand_bold));
        }
        meSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        classSet.setLineWidth(2f);
        classSet.setColor(getResources().getColor(R.color.colorAccentBlue));
        classSet.setCircleColors(classColors);
        classSet.setCircleRadius(3f);
        classSet.setDrawCircleHole(false);
        classSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (entry == classSet.getEntryForIndex(classSet.getEntryCount() - 1)) {
                    return String.valueOf(entry.getY());
                }  else {
                    return "";
                }
            }
        });
        classSet.setValueTextColor(getResources().getColor(R.color.colorAccentBlue));
        classSet.setValueTextSize(12f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            classSet.setValueTypeface(getResources().getFont(R.font.quicksand_bold));
        }
        classSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        lineData = new LineData(meSet, classSet);
        chart.setData(lineData);
        meSet.notifyDataSetChanged();
        classSet.notifyDataSetChanged();
        chart.getLineData().notifyDataChanged();
        chart.notifyDataSetChanged();
        Log.d("TEST", "data changed");

        chart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);
        yAxis.setAxisMinimum(-10);
        yAxis.setAxisMaximum(110);
        yAxis.setAxisLineColor(Color.WHITE);
        yAxis.setAxisLineWidth(1f);

        LimitLine neutralLine = new LimitLine(50);
        neutralLine.setLineColor(Color.argb(63, 255, 255, 255));
        neutralLine.setLineWidth(1f);
        neutralLine.enableDashedLine(25, 25, 0);

        yAxis.addLimitLine(neutralLine);

        LimitLine bottomLine = new LimitLine(-10);
        bottomLine.setLineColor(Color.WHITE);
        bottomLine.setLineWidth(1f);

        yAxis.addLimitLine(bottomLine);


        YAxis yAxis2 = chart.getAxisRight();
        yAxis2.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(index);
        //xAxis.setAxisMaximum(360);
        xAxis.setAxisMinimum(0);
        Log.d("TEST", "end of graphData");
        chart.invalidate();

    }

    private void graphData() {

//        //initiating lists of data
//        meValues = new ArrayList<>();
//        classValues = new ArrayList<>();

        //initiating lines for graph
//        meColors = new ArrayList<>();
//        classColors = new ArrayList<>();



        //dataRetrieval = new TimelineDataRetrieval();



//        final int count = 200;
//        final int range = 100;
//        int rand = -1;
//
//        for (int i = 0; i < count; i++) {
//            meValues.add(new Entry(i, i%100 * rand));
//            rand *= -1;
//            meColors.add(Color.TRANSPARENT);
//        }





//        for (int i = 0; i < count; i++) {
//            float val = (float) (Math.random() * range);
//            meValues.add(new Entry(i, val));
//            meColors.add(Color.TRANSPARENT);
//        }

        for (int i = 0; i < meValues.size(); i++) {
            meColors.add(Color.TRANSPARENT);
            classColors.add(Color.TRANSPARENT);
        }

        Log.d("TEST", "meColor size: " + meColors.size());
        meColors.remove(meColors.size() - 1);
        meColors.add(Color.WHITE);

//        for (int i = 0; i < count; i++) {
//            ArrayList<Integer> list = dataRetrieval.average(101);
//            float avg = dataRetrieval.calculateAverageData(list);
//            classValues.add(new Entry(i, avg));
//            classColors.add(Color.TRANSPARENT);
//        }

//        for (int i = 0; i < count; i++) {
//            float val = (float) (Math.random() * range);
//            classValues.add(new Entry(i, val));
//            classColors.add(Color.TRANSPARENT);
//        }

        Log.d("TEST", "classColors size: " + classColors.size());
        classColors.remove(classColors.size() - 1);
        classColors.add(getResources().getColor(R.color.colorAccentBlue));


        meSet = new LineDataSet(meValues, "Me");
        classSet = new LineDataSet(classValues, "Class");
        Log.d("TEST", "initialized meSet and classSet");

        meSet.setLineWidth(2f);
        meSet.setColor(Color.WHITE);
        meSet.setCircleColors(meColors);
        meSet.setCircleRadius(3f);
        meSet.setDrawCircleHole(false);
        meSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(entry == meSet.getEntryForIndex(9)) {
                    return "Me";
                } else {
                    return "";
                }
            }
        });
        meSet.setValueTextColor(Color.WHITE);
        meSet.setValueTextSize(12f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            meSet.setValueTypeface(getResources().getFont(R.font.quicksand_bold));
        }
        meSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        classSet.setLineWidth(2f);
        classSet.setColor(getResources().getColor(R.color.colorAccentBlue));
        classSet.setCircleColors(classColors);
        classSet.setCircleRadius(3f);
        classSet.setDrawCircleHole(false);
        classSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(entry == classSet.getEntryForIndex(9)) {
                    return "Class";
                } else {
                    return "";
                }
            }
        });
        classSet.setValueTextColor(getResources().getColor(R.color.colorAccentBlue));
        classSet.setValueTextSize(12f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            classSet.setValueTypeface(getResources().getFont(R.font.quicksand_bold));
        }
        classSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);


        lineData = new LineData(meSet, classSet);
        chart.setData(lineData);

        chart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setDrawLabels(false);
        yAxis.setAxisMinimum(-10);
        yAxis.setAxisMaximum(110);
        yAxis.setAxisLineColor(Color.WHITE);
        yAxis.setAxisLineWidth(1f);

        LimitLine neutralLine = new LimitLine(50);
        neutralLine.setLineColor(Color.argb(63, 255, 255, 255));
        neutralLine.setLineWidth(1f);
        neutralLine.enableDashedLine(25, 25, 0);

        yAxis.addLimitLine(neutralLine);

        LimitLine bottomLine = new LimitLine(-10);
        bottomLine.setLineColor(Color.WHITE);
        bottomLine.setLineWidth(1f);

        yAxis.addLimitLine(bottomLine);


        YAxis yAxis2 = chart.getAxisRight();
        yAxis2.setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(10);
        xAxis.setAxisMinimum(0);
        Log.d("TEST", "end of graphData");
    }

    private void setEngagedCount() {
        int engagedCount = new Random().nextInt(100);
        engagedCountText.setText(String.format(Locale.US, "%d", engagedCount));
        if(engagedCount < 10) {
            circleWrapper.setVisibility(View.GONE);
        } else {
            circleWrapper.setVisibility(View.VISIBLE);
        }
    }

}
