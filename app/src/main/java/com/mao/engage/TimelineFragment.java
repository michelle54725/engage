package com.mao.engage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import java.sql.Time;
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
    private TextView startTimeText;
    private TextView endTimeText;
    private String sectionRefKey;
    private double thresholdVal;
    TimerTask retrieveDataTask;
    private ArrayList<Integer> timelineData;

    private SeekBar threshBar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;

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
    // TODO: Rename and change types and number of parameters
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
        /**if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }**/
    }

    /**
     * TODO: Psuedocode for onCreateView:
     * Send threshold value from previous Now fragment via Intent.putExtra("threshold")
     * Need to grab threshold value from previous Now fragment via Intent.getExtra() and set to global variable
     * Create various additional variables for our graph (startTimeText, endTimeText, set these times to appropriate times from our db)
     * Implement a Timer here that calls the retrieveData function every 10s to get new updated values
     * make sure timer terminates after allotted time
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        chart = view.findViewById(R.id.chart);
        if (getArguments() != null) {
            sectionRefKey = getArguments().getString("sectionRefKey");
            timelineData = getArguments().getIntegerArrayList("timelinedata");
        }
        thresholdVal = FirebaseUtils.getThreshold(sectionRefKey) * 10.0;

        chart.bringToFront();
        mEngagedPieChart = view.findViewById(R.id.mEngagedPieChart);
        mDisengagedPieChart = view.findViewById(R.id.mDisengagedPieChart);
        //startTimeText = view.findViewById(R.id.startTimeText); endTimeText = view.findViewById(R.id.endTimeText);
        //startTimeText.setText("3:00PM"); endTimeText.setText("4:00PM");
        threshBar = view.findViewById(R.id.mVerticalSeekBar);
        threshBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                FirebaseUtils.changeThresholdVal(sectionRefKey, progress);
                Log.d("TEST", "threshold value changed!" + progress);
                retrieveData();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        retrieveDataTask = new TimerTask() {
            int test_val = 0; //for testing
            @Override
            public void run() {
                Log.d("TEST", "TIMER WORKING... timeline" + test_val++);
                Activity activity = getActivity();
                while (activity == null) {
                    activity = getActivity();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        retrieveData();
                    }
                });
            }
        };
        new Timer().scheduleAtFixedRate(retrieveDataTask, 0, 5000);


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

    /**
     * TODO: Psuedocode for retrieveData: (will be called every 10 seconds from onCreateView)
     * Grab threshold value from global variable mThreshold mentioned in onCreateView
     *  - extend entire value across the graph
     * Create classValues arrayList (MAKE SURE TO += our current values so we don't overwrite our previous ones
     * For now, set data being added from TimelineDataRetrieval class by calling createRandomStudentData
     * Later, replace the previous code and grab data from Firebase (UserSessions > all User Id's slider val with the associated magic key)
     * Average the current data via our TimeLineDataRetrieval class by passing in an arraylist into calculateAverageData
     * add this averaged data point to classValues
     * (BAD IMPLEMENTATION; ASK ABOUT FASTER METHOD) -- this is for the two numbers at the bottom
     *  - create new TreeMap every 10 seconds that stores all slider values
     *      - (compare slider values for order) K: sliderval V: repetitions
     *  - If there is a duplicate, add it to the value of the sliderVal
     *  - Find the closest sliderVal to the threshold in our Map
     *  - add all the values to the right of that to get the positive (blue) values
     *  - subtract classValues.size() by blue values to get the leftValues (redVal)
     *  - Implement pie chart from MPAndroidChart for both values and display at the bottom
     * Make sure that the last point of the graph has a little circle indicator.
     * Add the graphics for the threshold mark and insert threshold value as text on the graph.
     * TODO: Ask about realtime data ... can we maybe implement a different api since this one doesn't support real time?
     */
    private void retrieveData() {
        threshold = new ArrayList<>();
        classValues = new ArrayList<>();
        Log.d("TEST", "in  retrieve data function in TIMELINE");

        ArrayList<Integer> thresholdColor = new ArrayList<>();
        ArrayList<Integer> classColors = new ArrayList<>();
        TimelineDataRetrieval timeline = new TimelineDataRetrieval();
        final int count = 10;
        final int range = 100;

        Log.d("TEST", "calculateaveragedata timeline" + timeline.calculateAverageData());
        timelineData.add((int) timeline.calculateAverageData());

        ArrayList<Integer> individualEngagements = new ArrayList<>();
        for (String user : FirebaseUtils.sectionSliders.keySet()) {
            individualEngagements.add(FirebaseUtils.sectionSliders.get(user));
            Log.d("TEST", individualEngagements.size() + ") added: " + user + ": " + FirebaseUtils.sectionSliders.get(user));
        }

        //ArrayList<Float> mTimelineArray = TimelineDataRetrieval.getTimelineArray();
        for (int i = 0; i < timelineData.size(); i++) {
            threshold.add(new Entry(i, (float) thresholdVal));
            thresholdColor.add(Color.TRANSPARENT);
        }
        thresholdColor.remove(thresholdColor.size() - 1);
        thresholdColor.add(Color.WHITE);

        for (int i = 0; i < timelineData.size(); i++) {
            //float val = (float) (Math.random() * range);
            classValues.add(new Entry(i, (float) timelineData.get(i)));
            //Log.d("TEST", "values in averaged data" + classValues.get(i));
            classColors.add(Color.TRANSPARENT);
        }
        classColors.remove(classColors.size() - 1);
        classColors.add(getResources().getColor(R.color.colorAccentBlue));

        int[] countsArray = new int[10];
        for(int engagement : individualEngagements) {
            countsArray[engagement / 10] += 1;
        }

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

        mThreshold = new LineDataSet(threshold, "Threshold");
        classSet = new LineDataSet(classValues, "Class");

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
        //xAxis.setAxisMaximum(250);
        //xAxis.setAxisMinimum(0);

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
