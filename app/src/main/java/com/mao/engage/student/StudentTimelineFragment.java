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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Environment;
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
import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;
import com.mao.engage.teacher.TeacherCreateClassActivity;
import com.mao.engage.teacherclassactivity.TeacherClassActivity;
import com.mao.engage.teacherclassactivity.TimelineDataRetrieval;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
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
                myActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() { retrieveData();}
                });
            }
        };
        //retrieves data every 5000 ms (5s)
        Timer t = new Timer();
        t.scheduleAtFixedRate(retrieveDataTask, 0, 5000);
        if (FirebaseUtils.compareTime(activity.getEndTime())) {
            Log.d("TEST", "compare: stop retrieve data upon reach time");
            t.cancel();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                    //takeScreenshot();
                    Log.d("TEST", "selected save graph");
                }
            });
            builder.show();
        }

        startTime = activity.getStartTime();
        endTime = activity.getEndTime();
        startTimeText.setText(startTime);
        endTimeText.setText(endTime);

        setEngagedCount();

        return view;
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            Log.d("TEST", "takeScreenshot");
            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        Log.d("TEST", "openScreenshot");
        startActivity(intent);
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
        //initializes lines for graph
        meColors = new ArrayList<>();
        classColors = new ArrayList<>();

        //initializes TimelineDataRetrieval class to get data
        dataRetrieval = new TimelineDataRetrieval();

        //adds user slider value to ArrayList of entries
        int mySliderValue = dataRetrieval.getMySliderValue(FirebaseUtils.getPsuedoUniqueID());
        meValues.add(new Entry(index, mySliderValue));
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
        chart.invalidate();
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