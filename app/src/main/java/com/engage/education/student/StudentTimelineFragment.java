/*
    How the student's version of the timeline graph is rendered.

    Graph contains:
        Blue line: class average slider values
        White line: student slider values
        A count of number of students in the section

    Used by StudentClassActivity
    Etymology: A "Section" refers to a course discussion section.
 */
package com.engage.education.student;

import android.app.Activity;
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
import com.engage.education.FirebaseUtils;
import com.engage.education.R;
import com.engage.education.teacherclassactivity.TimelineDataRetrieval;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class StudentTimelineFragment extends Fragment {

    //ArrayList of Entry type values for student timeline and line of class averages
    private ArrayList<Entry> meValues;
    private ArrayList<Entry> classValues;

    //Arraylist of student slider values (y-axis of the graph)
    private ArrayList<Integer> meColors;
    private ArrayList<Integer> classColors;
    private StudentClassActivity activity;

    //x-value of graph, increments each time retrieveData is called
    private int index;

    //LineDataSets that take integer values from above ArrayLists to graph
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
    Timer timer;

    private boolean isEndOfSection = false;
    private String exportDialogTitle = "";

    public StudentTimelineFragment() {
        // required empty public constructor
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

        //initializes lists of slider values from activity
        meValues = activity.getMeValues();
        classValues = activity.getClassValues();

        //initializes lines for graph
        meColors = new ArrayList<>();
        classColors = new ArrayList<>();

        //retrieves updates slider values
        retrieveDataTask = new TimerTask() {
            int test_val = 0; //for testing
            @Override
            public void run() {
                Activity myActivity = getActivity();
                while (myActivity == null) { //to prevent null object reference for runOnUiThread
                    myActivity = getActivity();
                }
                if (FirebaseUtils.compareTime(activity.getEndTime())) {
                    retrieveDataTask.cancel();
                    return;
                }
                //run on separate Ui thread to no conflict other threads
                myActivity.runOnUiThread(() -> retrieveData());
            }
        };
        //retrieves data every 5000 ms (5s)
        timer = new Timer();
        timer.scheduleAtFixedRate(retrieveDataTask, 0, 5000);
        if (FirebaseUtils.compareTime(activity.getEndTime())) {
            timer.cancel();
            isEndOfSection = true;
        }

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

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        } else {
            Log.e("TEST: ", "timer doesnt work");
        }

    }

    private void retrieveData() {
        //initializes lines for graph
        meColors = new ArrayList<>();
        classColors = new ArrayList<>();

        //initializes TimelineDataRetrieval class to get data
        dataRetrieval = new TimelineDataRetrieval();

        //adds user slider value to ArrayList of entries
        int mySliderValue = dataRetrieval.getMySliderValue(FirebaseUtils.getPsuedoUniqueID());
        meValues.add(new Entry(index, mySliderValue));

        //adding class average slider value to ArrayList of entries
        int sectionAvg = Math.round(dataRetrieval.calculateAverageSectionData());
        classValues.add(new Entry(index, sectionAvg));
        index++;

        //colors for rendering data points on the timeline graph: all transparent except for latest point
        for (int i = 0; i < meValues.size(); i++) {
            meColors.add(Color.TRANSPARENT);
            classColors.add(Color.TRANSPARENT);
        }
        meColors.remove(meColors.size() - 1);
        meColors.add(R.color.colorWhite);
        classColors.remove(classColors.size() - 1);
        classColors.add(getResources().getColor(R.color.colorAccentBlue));

        //initializes LineDataSets necessary for timeline graph
        meSet = new LineDataSet(meValues, "Me");
        classSet = new LineDataSet(classValues, "Class");

        //sets line colors and weights
        meSet.setLineWidth(2f);
        meSet.setColor(R.color.colorWhite);
        meSet.setCircleColors(meColors);
        meSet.setCircleRadius(3f);
        meSet.setDrawCircleHole(false);
        meSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        classSet.setLineWidth(2f);
        classSet.setColor(R.color.colorAccentBlue);
        classSet.setCircleColors(classColors);
        classSet.setCircleRadius(3f);
        classSet.setDrawCircleHole(false);
        classSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        //setting graph text and colors
        meSet.setValueTextColor(R.color.colorWhite);
        meSet.setValueTextSize(12f);
        classSet.setValueTextColor(R.color.colorAccentBlue);
        classSet.setValueTextSize(12f);

        //labels the latest point its numerical value
        meSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (entry == meSet.getEntryForIndex(meSet.getEntryCount() - 1)) {
                    return String.valueOf(entry.getY());
                }  else {
                    return "";
                }
            }
        });
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

        //sets font type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            meSet.setValueTypeface(getResources().getFont(R.font.quicksand_bold));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            classSet.setValueTypeface(getResources().getFont(R.font.quicksand_bold));
        }

        //inputs sets into data form that can be graphed
        lineData = new LineData(meSet, classSet);
        chart.setData(lineData);


        //notifies that data has changed
        //tells the graph to update
        meSet.notifyDataSetChanged();
        classSet.notifyDataSetChanged();
        chart.getLineData().notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.invalidate();

        //turn off unnecessary elements of the graph package such as a legend
        chart.setTouchEnabled(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        //setting the axes of the graph
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

        //maximum length of axis set at index but within the layout size
        //means that latest point will always be at the rightmost edge
        xAxis.setAxisMaximum(index);
        xAxis.setAxisMinimum(0);


    }

    //All adjustments for engaged count (number of students in a section)
    private void setEngagedCount() {
        int countEngaged = 0;

        //counts number of students in a section
        for (String user : FirebaseUtils.sectionSliders.keySet()) {
            countEngaged += 1;
        }

        //sets text of engaged count
        engagedCountText.setText(String.format(Locale.US, "%d", countEngaged));

        //displays engaged counts
        //TODO: Make layout adaptable beause 3-digit numbers currently do no display well
        if(countEngaged > 100) {
            circleWrapper.setVisibility(View.GONE);
        } else {
            circleWrapper.setVisibility(View.VISIBLE);
        }
    }
}
