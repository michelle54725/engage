/*
 * This fragment is contained under the TeacherClassActivity.
 * It displays the threshold graph, class average graph, and both the pie charts for those engaged and disengaged.
 */
package com.mao.engage.teacherclassactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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
import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimelineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimelineFragment extends Fragment {
    private ArrayList<Entry> classValues;
    private ArrayList<Entry> threshold;

    private LineDataSet mThreshold;
    private LineDataSet classSet;

    private PieChart mEngagedPieChart;
    private PieChart mDisengagedPieChart;

    private LineData lineData;
    private LineChart chart;
    //TODO: implement start time and end time into graphs.
    private TextView startTimeText;
    private TextView endTimeText;
    private String endTime;

    private String sectionRefKey;
    private double thresholdVal;

    TimerTask retrieveDataTask;
    private ArrayList<Integer> timelineData;

    private SeekBar threshBar;

    private OnFragmentInteractionListener mListener;

    public TimelineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimelineFragment.
     */
    //currently not used
    public static TimelineFragment newInstance(String param1, String param2) {
        TimelineFragment fragment = new TimelineFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
     * Sets a listener on the threshold Bar and creates a new timer to call retrieveData at a fixed rate of 5 seconds.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        chart = view.findViewById(R.id.chart);
        startTimeText = view.findViewById(R.id.startTimeText);
        endTimeText = view.findViewById(R.id.endTimeText);
        if (getArguments() != null) {
            sectionRefKey = getArguments().getString("sectionRefKey");
            startTimeText.setText(getArguments().getString("start_time"));
            endTimeText.setText(getArguments().getString("end_time"));
            endTime = getArguments().getString("end_time");
            Log.d("TEST", "sectionRefKey in Timeline: " + sectionRefKey);
            timelineData = getArguments().getIntegerArrayList("timelinedata");
        }
        thresholdVal = FirebaseUtils.getThreshold(sectionRefKey) * 10.0;

        chart.bringToFront();
        mEngagedPieChart = view.findViewById(R.id.mEngagedPieChart);
        mDisengagedPieChart = view.findViewById(R.id.mDisengagedPieChart);
        threshBar = view.findViewById(R.id.mVerticalSeekBar);

        /*
         * Listener on threshold bar. ChangeThresholdVal stores the new change into the database.
         * Need to recall retrieveData to reset pie charts and the graph.
         */
        threshBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                FirebaseUtils.changeThresholdVal(sectionRefKey, progress);
                Log.d("TEST", "threshold value changed!" + progress);
                if (FirebaseUtils.compareTime(endTime) == false) {
                    retrieveData();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Section has ended!");
                    builder.setMessage("Would you like to save your graph?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Log.d("TEST", "selected no save");
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Log.d("TEST", "selected save graph");
                        }
                    });
                    builder.show();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Runs the retrieveData method specified below at a fixed rate of five seconds
        retrieveDataTask = new TimerTask() {
            int test_val = 0; //for testing
            @Override
            public void run() {
                Log.d("TEST", "TIMER WORKING... timeline" + test_val++);
                Activity activity = getActivity();
                while (activity == null) {
                    activity = getActivity();
                }
                if (FirebaseUtils.compareTime(endTime)) {
                    Log.d("TEST", "compare: stop retrieve data upon reach time");
                    retrieveDataTask.cancel();
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        retrieveData();
                    }
                });
            }
        };
        Timer t = new Timer();
        t.scheduleAtFixedRate(retrieveDataTask, 0, 5000);
        if (FirebaseUtils.compareTime(getArguments().getString("end_time"))) {
            Log.d("TEST", "compare: stop retrieve data upon reach time");
            t.cancel();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Section has ended!");
            builder.setMessage("Would you like to save your graph?");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Log.d("TEST", "selected no save");
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Log.d("TEST", "selected save graph");
                }
            });
            builder.show();
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
     * This method updates the graphs and piecharts from firebase data displayed on the screen.
     * It is called in the onCreate method of this class at a fixed rate (every five seconds).
     */
    private void retrieveData() {
        //these variables will store an arraylist of all the values of this class session
        //has to be recreated at every call of retrieveData based on the way that the Android graph api works
        threshold = new ArrayList<>();
        classValues = new ArrayList<>();
        Log.d("TEST", "in  retrieve data function in TIMELINE");

        //stores color for every data point created in the set
        ArrayList<Integer> thresholdColor = new ArrayList<>();
        ArrayList<Integer> classColors = new ArrayList<>();
        TimelineDataRetrieval timeline = new TimelineDataRetrieval();
        final int count = 10;
        final int range = 100;

        Log.d("TEST", "calculateaveragedata timeline" + timeline.calculateAverageSectionData());
        //calculates the average of all the student's section sliders at an instance of time
        timelineData.add((int) timeline.calculateAverageSectionData());

        ArrayList<Integer> individualEngagements = new ArrayList<>();
        for (String user : FirebaseUtils.sectionSliders.keySet()) {
            individualEngagements.add(FirebaseUtils.sectionSliders.get(user));
            Log.d("TEST", individualEngagements.size() + ") added: " + user + ": " + FirebaseUtils.sectionSliders.get(user));
        }

        // need to add each data point as an entry to our array lists
        // threshold and thresholdColor will be used as the data set by the graph api
        for (int i = 0; i < timelineData.size(); i++) {
            threshold.add(new Entry(i, (float) thresholdVal));
            thresholdColor.add(Color.TRANSPARENT);
        }
        // need to remove and add a new last color to specify the last point on the graph
        thresholdColor.remove(thresholdColor.size() - 1);
        thresholdColor.add(Color.WHITE);

        // need to add each data point as an entry to our array lists
        // classValues and classColors will be used as the data set by the graph api
        for (int i = 0; i < timelineData.size(); i++) {
            classValues.add(new Entry(i, (float) timelineData.get(i)));
            classColors.add(Color.TRANSPARENT);
        }
        classColors.remove(classColors.size() - 1);
        classColors.add(getResources().getColor(R.color.colorAccentBlue));

        // countsArray scales down and stores the engagement levels of students on a scale of 0 to 10.
        // for example, if there are ten students with an engagement of 60,
        // countsArray will store 10 (amount of students) at index 6 (engagement level).
        int[] countsArray = new int[10];
        for(int engagement : individualEngagements) {
            countsArray[engagement / 10] += 1;
        }

        //the countsArray information is converted into type BarEntry to be used by the Pie Chart Graph API
        List<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < countsArray.length; i++) {
            entries.add(new BarEntry(i, countsArray[i]));
        }

        BarDataSet disengagedBarSet = new BarDataSet(entries.subList(0, (int) (thresholdVal / 10)), "BarDataSet");
        disengagedBarSet.setColor(getResources().getColor(R.color.colorAccentRed));
        BarDataSet engagedBarSet = new BarDataSet(entries.subList((int) (thresholdVal / 10), entries.size()), "BarDataSet");
        engagedBarSet.setColor(getResources().getColor(R.color.colorAccentBlue));

        int engagedStudents = 0;
        for(int i = (int) (thresholdVal / 10); i < countsArray.length; i++) {
            engagedStudents += countsArray[i];
        }
        int disengagedStudents = 0;
        for(int i = 0; i < (int) (thresholdVal / 10); i++) {
            disengagedStudents += countsArray[i];
        }

        List<PieEntry> engagementEntries = new ArrayList<>();
        engagementEntries.add(new PieEntry(disengagedStudents));
        engagementEntries.add(new PieEntry(engagedStudents));

        //line data sets are used by the graph API with the arraylists created earlier
        mThreshold = new LineDataSet(threshold, "Threshold");
        classSet = new LineDataSet(classValues, "Class");
        //below are settings of the graph for UI purposes
        mThreshold.setLineWidth(2f);
        mThreshold.setColor(Color.WHITE);
        mThreshold.setCircleColors(thresholdColor);
        mThreshold.setCircleRadius(3f);
        mThreshold.setDrawCircleHole(false);
        mThreshold.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(entry == mThreshold.getEntryForIndex(timelineData.size() - 1)) {
                    return "Threshold";
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
                if(entry == classSet.getEntryForIndex(timelineData.size() - 1)) {
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

        mThreshold.notifyDataSetChanged();
        classSet.notifyDataSetChanged();
        chart.getLineData().notifyDataChanged();
        chart.notifyDataSetChanged();
        Log.d("TEST", "data changed");

        PieDataSet engagedSet = new PieDataSet(engagementEntries, "EngagedPieSet");
        engagedSet.setColors(Color.TRANSPARENT, getResources().getColor(R.color.colorAccentBlue));
        PieData engagedData = new PieData(engagedSet);
        engagedData.setDrawValues(false);
        mEngagedPieChart.setData(engagedData);
        mEngagedPieChart.setTouchEnabled(false);
        mEngagedPieChart.setHoleColor(Color.TRANSPARENT);
        mEngagedPieChart.setHoleRadius(75);
        mEngagedPieChart.setTransparentCircleRadius(0);

        String engagedStudentsString = String.valueOf(engagedStudents);
        SpannableString engagedSpannable = new SpannableString(engagedStudentsString + "\nstudents");
        engagedSpannable.setSpan(new RelativeSizeSpan(2.5f), 0, engagedStudentsString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mEngagedPieChart.setCenterText(engagedSpannable);
        mEngagedPieChart.setCenterTextSize(18);
        mEngagedPieChart.setCenterTextColor(Color.WHITE);
        mEngagedPieChart.setCenterTextTypeface(ResourcesCompat.getFont(getContext(), R.font.quicksand_bold));
        mEngagedPieChart.getLegend().setEnabled(false);
        mEngagedPieChart.getDescription().setEnabled(false);
        mEngagedPieChart.invalidate();

        PieDataSet disengagedSet = new PieDataSet(engagementEntries, "DisengagedPieSet");
        disengagedSet.setColors(getResources().getColor(R.color.colorAccentRed), Color.TRANSPARENT);
        PieData disengagedData = new PieData(disengagedSet);
        disengagedData.setDrawValues(false);
        mDisengagedPieChart.setData(disengagedData);
        mDisengagedPieChart.setTouchEnabled(false);
        mDisengagedPieChart.setHoleColor(Color.TRANSPARENT);
        mDisengagedPieChart.setHoleRadius(75);
        mDisengagedPieChart.setTransparentCircleRadius(0);

        String disengagedStudentsString = String.valueOf(disengagedStudents);
        SpannableString disengagedSpannable = new SpannableString(disengagedStudentsString + "\nstudents");
        disengagedSpannable.setSpan(new RelativeSizeSpan(2.5f), 0, disengagedStudentsString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mDisengagedPieChart.setCenterText(disengagedSpannable);
        mDisengagedPieChart.setCenterTextSize(18);
        mDisengagedPieChart.setCenterTextColor(Color.WHITE);
        mDisengagedPieChart.setCenterTextTypeface(ResourcesCompat.getFont(getContext(), R.font.quicksand_bold));
        mDisengagedPieChart.getLegend().setEnabled(false);
        mDisengagedPieChart.getDescription().setEnabled(false);
        mDisengagedPieChart.invalidate();

        chart.invalidate();
        thresholdVal = FirebaseUtils.getThreshold(sectionRefKey) * 10.0;
        Log.d("TEST", "my current threshold " + thresholdVal);
        Log.d("TEST", "my actual threshold " + threshBar.getProgress());
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
