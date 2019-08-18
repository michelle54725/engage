/*
    How the student's version of the timeline graph is rendered.

    Graph contains:
        Blue line: class average slider values
        White line: student slider values
        A count of number of students in the section

    Used by StudentClassActivity
    Etymology: A "Section" refers to a course discussion section.
 */
package com.mao.engage.student;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;
import com.mao.engage.teacherclassactivity.TimelineDataRetrieval;
import com.rey.material.widget.FloatingActionButton;

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

    private FloatingActionButton exportImageButton;

    TimerTask retrieveDataTask;
    private TimelineDataRetrieval dataRetrieval;

    private boolean isEndOfSection = false;
    private String exportDialogTitle = "";
    private String name;

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
        exportImageButton = view.findViewById(R.id.exportActionButton);
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
                Log.d("TEST", "TIMER WORKING..." + test_val++);
                Activity myActivity = getActivity();
                while (myActivity == null) { //to prevent null object reference for runOnUiThread
                    myActivity = getActivity();
                }
                if (FirebaseUtils.compareTime(activity.getEndTime())) {
                    Log.d("TEST", "compare: stop retrieve data upon reach time");
                    retrieveDataTask.cancel();
                    return;
                }
                //run on separate Ui thread to no conflict other threads
                myActivity.runOnUiThread(() -> useDummyData()); // FIXME: Replace with retrieve Data; currently on dummy data for DEMO purposes
            }
        };
        //retrieves data every 5000 ms (5s)
        Timer t = new Timer();
        t.scheduleAtFixedRate(retrieveDataTask, 0, 5000);
        if (FirebaseUtils.compareTime(activity.getEndTime())) {
            Log.d("TEST", "compare: stop retrieve data upon reach time");
            t.cancel();
            isEndOfSection = true;
            exportDialogTitle = "Time is up! Export Your Image as an PNG to Save to Gallery";
//            lottieToastStudent(activity, name)
            showExportImageDialog();
        }

        exportImageButton.setOnClickListener(floatingButton -> {
            isEndOfSection = false;
            exportDialogTitle = "Export Your Image as an PNG to Save to Gallery";
            Log.d("P-TEST", "Export Button Clicked");
            showExportImageDialog();
        });

        startTime = activity.getStartTime();
        endTime = activity.getEndTime();
        startTimeText.setText(startTime);
        endTimeText.setText(endTime);

        setEngagedCount();

        return view;
    }


    private boolean checkIfAlreadyhavePermission() {
        int writeResult = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readResut = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return writeResult == PackageManager.PERMISSION_GRANTED && readResut == PackageManager.PERMISSION_GRANTED;
    }

    private void showExportImageDialog() {
        if (checkIfAlreadyhavePermission()) {
            // If request is cancelled, the result arrays are empty.
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(exportDialogTitle);
            builder.setMessage("To save your graph, please enter a name below for the graph:");
            final EditText imageNameInput = new EditText(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            layoutParams.leftMargin = 8;
            layoutParams.rightMargin = 8;
            imageNameInput.setHint("Please enter a non-empty name for your file");
            imageNameInput.setLayoutParams(layoutParams);
            builder.setView(imageNameInput);
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
                Log.d("TEST", "selected no save");
            });
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                dialog.dismiss();
                String imageName = imageNameInput.getText().toString();
                if (imageName.trim().length() == 0) {
                    Toast.makeText(getContext(), "Please enter a non-empty name", Toast.LENGTH_LONG).show();
                } else {
                    boolean success = chart.saveToGallery(imageName);
                    if (success) {
                        Toast.makeText(getContext(), "Image has been saved successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Export failed :( please try again later", Toast.LENGTH_LONG).show();
                    }
                    Log.d("TEST", "selected save graph");
                    if (isEndOfSection) {
                        // FIXME: Ideally remove section, but parameters have changed; update needed
                    }
                }
            });
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this.activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void useDummyData() {
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 4));
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 4));

        LineDataSet dataSet = new LineDataSet(entries, "You");
        dataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccentBlue));
        dataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.colorAccentBlue));

        //****
        // Controlling X axis
        XAxis xAxis = chart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        final String[] months = new String[]{"7:00", "7:15", "7:30", "7:45"};

        IAxisValueFormatter formatter = (value, axis) -> months[(int) value];
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        //***
        // Controlling right side of y axis
        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        //***
        // Controlling left side of y axis
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        // Setting Data
        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.notifyDataSetChanged();
        //refresh
        chart.invalidate();
        //
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
        // dummy data
        int[][] dummyData = {{1, 10}, {2, 50}, {3, 70}, {4, 50}, {5, 90}};
        for (int[] data : dummyData) {
            meValues.add(new Entry(data[0], data[1]));
        }
        Log.d("TEST", "Me value: " + mySliderValue);

        //adding class average slider value to ArrayList of entries
        int sectionAvg = Math.round(dataRetrieval.calculateAverageSectionData());
        classValues.add(new Entry(index, sectionAvg));
        Log.d("TEST", "Class avg: " + sectionAvg);

        //colors for rendering data points on the timeline graph: all transparent except for latest point
        for (int i = 0; i < meValues.size(); i++) {
            meColors.add(Color.TRANSPARENT);
            classColors.add(Color.TRANSPARENT);
        }
        meColors.remove(meColors.size() - 1);
        meColors.add(Color.WHITE);
        classColors.remove(classColors.size() - 1);
        classColors.add(getResources().getColor(R.color.colorAccentBlue));

        //initializes LineDataSets necessary for timeline graph
        meSet = new LineDataSet(meValues, "Me");
        classSet = new LineDataSet(classValues, "Class");
        Log.d("TEST", "initialized meSet and classSet");
        Log.d("TEST", "meSet:" + meSet.getEntryCount());
        Log.d("TEST", "classSet: " + classSet.getEntryCount());

        //sets line colors and weights
        meSet.setLineWidth(2f);
        meSet.setColor(Color.WHITE);
        meSet.setCircleColors(meColors);
        meSet.setCircleRadius(3f);
        meSet.setDrawCircleHole(false);
        meSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        classSet.setLineWidth(2f);
        classSet.setColor(getResources().getColor(R.color.colorAccentBlue));
        classSet.setCircleColors(classColors);
        classSet.setCircleRadius(3f);
        classSet.setDrawCircleHole(false);
        classSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        //setting graph text and colors
        meSet.setValueTextColor(Color.WHITE);
        meSet.setValueTextSize(12f);
        classSet.setValueTextColor(getResources().getColor(R.color.colorAccentBlue));
        classSet.setValueTextSize(12f);

        //labels the latest point its numerical value
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
        Log.d("TEST", "data changed");

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
        Log.d("TEST", "end of graphData");

        //redraws
//        chart.invalidate();
    }

    //All adjustments for engaged count (number of students in a section)
    private void setEngagedCount() {
        int countEngaged = 0;

        //counts number of students in a section
        for (String user : FirebaseUtils.sectionSliders.keySet()) {
            countEngaged += 1;
            Log.d("TEST","added: " + user + ": " + FirebaseUtils.sectionSliders.get(user));
        }

        //sets text of engaged count
        engagedCountText.setText(String.format(Locale.US, "%d", countEngaged));

        //displays engaged counts
        //TODO: Make layout adaptable beause 3-digit numbers currently do no display well
        if(countEngaged > 100) {
            circleWrapper.setVisibility(View.GONE);
        } else {
            Log.d("TEST", "displaying engaged");
            circleWrapper.setVisibility(View.VISIBLE);
            Log.d("TEST", "displaying engaged");
        }
    }
}
