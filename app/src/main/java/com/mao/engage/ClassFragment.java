package com.mao.engage;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class ClassFragment extends Fragment {

    private ArrayList<Entry> meValues;
    private ArrayList<Entry> classValues;

    private LineDataSet meSet;
    private LineDataSet classSet;

    private LineData lineData;

    private LineChart chart;
    private TextView startTimeText;
    private TextView endTimeText;
    private ConstraintLayout circleWrapper;
    private TextView engagedCountText;

    public ClassFragment() {
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

        retrieveData();

        startTimeText.setText("3:00PM");
        endTimeText.setText("4:00PM");

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
        meValues = new ArrayList<>();
        classValues = new ArrayList<>();

        ArrayList<Integer> meColors = new ArrayList<>();
        ArrayList<Integer> classColors = new ArrayList<>();

        final int count = 10;
        final int range = 100;

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range);
            meValues.add(new Entry(i, val));
            meColors.add(Color.TRANSPARENT);
        }
        meColors.remove(meColors.size() - 1);
        meColors.add(Color.WHITE);

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range);
            classValues.add(new Entry(i, val));
            classColors.add(Color.TRANSPARENT);
        }
        classColors.remove(classColors.size() - 1);
        classColors.add(getResources().getColor(R.color.colorAccentBlue));

        meSet = new LineDataSet(meValues, "Me");
        classSet = new LineDataSet(classValues, "Class");

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
