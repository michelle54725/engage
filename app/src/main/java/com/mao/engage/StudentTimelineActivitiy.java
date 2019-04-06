package com.mao.engage;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
public class StudentTimelineActivitiy extends Fragment {

    TimerTask retrieveDataTask;
    private TextView sectionNameText;
    private TextView magicWordText;
    private ArrayList<Entry> classValues;
    private ArrayList<Entry> threshold;

    private LineDataSet mThreshold;
    private LineDataSet classSet;

    private LineData lineData;

    private LineChart chart;
    private TextView startTimeText;
    private TextView endTimeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        chart = view.findViewById(R.id.chart);

        //make sure time retrieved is from the section data
        startTimeText = view.findViewById(R.id.startTimeText);
        endTimeText = view.findViewById(R.id.endTimeText);

        retrieveData();

        startTimeText.setText("3:00PM");
        endTimeText.setText("4:00PM");

        retrieveDataTask = new TimerTask() {
            int test_val = 0; //for testing
            @Override
            public void run() {
                Log.d("TEST", "STUDENT TIMELINE TIMER WORKING..." + test_val++);
                retrieveData();
            }
        };
        new Timer().scheduleAtFixedRate(retrieveDataTask, 0, 5000);

        return view;
    }

    //public View onCreateView()

    /*
        Retrieve the data from the entire section using sectionID or magic key
        to average for the Average line
        While retrieving from section, if sectionID is of the user, add data to user's timeline
        Call from retrieveDataTask=new TimerTask() every 10 seconds

        Put values in a line graph
     */
    private void retrieveData() {
        threshold = new ArrayList<>();
        classValues = new ArrayList<>();

        ArrayList<Integer> meColors = new ArrayList<>();
        ArrayList<Integer> classColors = new ArrayList<>();

        final int count = 10;
        final int range = 100;

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range);
            threshold.add(new Entry(i, val));
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

        mThreshold = new LineDataSet(threshold, "Me");
        classSet = new LineDataSet(classValues, "Class");

        mThreshold.setLineWidth(2f);
        mThreshold.setColor(Color.WHITE);
        mThreshold.setCircleColors(meColors);
        mThreshold.setCircleRadius(3f);
        mThreshold.setDrawCircleHole(false);
        mThreshold.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(entry == mThreshold.getEntryForIndex(9)) {
                    return "Me";
                } else {
                    return "";
                }
            }
        });
        mThreshold.setValueTextColor(Color.WHITE);
        mThreshold.setValueTextSize(12f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mThreshold.setValueTypeface(getResources().getFont(R.font.quicksand_bold));
        }
        mThreshold.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

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


        lineData = new LineData(mThreshold, classSet);
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
    private double averageData() {
        return 0.0;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
