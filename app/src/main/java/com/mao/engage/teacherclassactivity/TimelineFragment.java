/*
 * This fragment is contained under the TeacherClassActivity.
 * It displays the threshold graph, class average graph, and both the pie charts for those engaged and disengaged.
 */
package com.mao.engage.teacherclassactivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimelineFragment extends Fragment {
    private TextView magicWordText;

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
    private String endTime;

    private String sectionRefKey;
    private int thresholdVal;

    TimerTask retrieveDataTask;
    private ArrayList<Integer> timelineData;

    private SeekBar threshBar;

    private OnFragmentInteractionListener mListener;

    private Calendar calendar;
    static Activity me;

    public TimelineFragment() {
        // Required empty public constructor
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
        magicWordText = view.findViewById(R.id.magicWordText);
        chart = view.findViewById(R.id.chart);
        startTimeText = view.findViewById(R.id.startTimeText);
        endTimeText = view.findViewById(R.id.endTimeText);
        if (this.getArguments() != null) {
            magicWordText.setText(String.format("Magic word: %s", getArguments().getString("magic_word")));
            sectionRefKey = getArguments().getString("sectionRefKey");
            startTimeText.setText(getArguments().getString("start_time"));
            endTimeText.setText(getArguments().getString("start_time"));
            endTime = getArguments().getString("end_time");
            Log.d("TEST", "sectionRefKey in Timeline: " + sectionRefKey);
            timelineData = getArguments().getIntegerArrayList("timelinedata");
        }
        thresholdVal = 50; //default to 50

        chart.bringToFront();
        mEngagedPieChart = view.findViewById(R.id.mEngagedPieChart);
        mDisengagedPieChart = view.findViewById(R.id.mDisengagedPieChart);
        threshBar = view.findViewById(R.id.mVerticalSeekBar);

        /*
         * Listener on threshold bar.
         * Need to re-call retrieveData to reset pie charts and the graph.
         */
        threshBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // don't call retrieveData() here so spam-y behavior doesn't affect anything
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                thresholdVal = seekBar.getProgress();
                Log.d("TEST-M", "threshold value changed: " + seekBar.getProgress());
                if (!FirebaseUtils.compareTime(endTime)) {
                    retrieveData(true);
                }
            }
        });

        final Timer t = new Timer();
        me = getActivity();

        //Runs the retrieveData method specified below at a fixed rate of five seconds
        retrieveDataTask = new TimerTask() {
            @Override
            public void run() {
                Activity activity = getActivity();
                while (activity == null) {
                    activity = getActivity();
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (FirebaseUtils.compareTime(endTime)) {
                            threshBar.setVisibility(View.GONE);
                            Log.d("TEST", "compare: stop retrieve data upon reach time 1");
                            t.cancel();
                            t.purge();
                            return;
                        } else {
                            retrieveData(false);
                        }
                    }
                });
            }
        };

        t.scheduleAtFixedRate(retrieveDataTask, 0, 5000);

        return view;
    }

    public static void takeScreenshot() {
        Log.d("TEST", "reached takeScreenshot");
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        // image naming and path  to include sd card  appending name you choose for file
        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
        // create bitmap screen capture
        View v1 = me.getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        File imageFile = new File(mPath);

        if (imageFile.exists()) {
            imageFile.delete();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            Log.d("TEST", "takeScreenshot2");
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            Log.d("TEST", "takeScreenshot3");
            outputStream.flush();
            Log.d("TEST", "takeScreenshot4");
            outputStream.close();
            Log.d("TEST", "takeScreenshot5");
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            Log.d("TEST", "Error: " + e.getStackTrace());
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        Log.d("TEST", "bitmap created");
        return returnedBitmap;
    }

//    public static void openScreenshot(File imageFile) {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        Uri uri = Uri.fromFile(imageFile);
//        intent.setDataAndType(uri, "image/*");
//        Log.d("TEST", "openScreenshot");
//        startActivity(intent);
//    }

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
    private void retrieveData(boolean isThreshold) {
        //isThreshold checks if we need to only change the threshold graph and not update the entire graph.
        //update endTimeText to current time every time retrieveData is called
        Date date = new Date();
        String strDateFormat = "hh:mm a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);
        endTimeText.setText(formattedDate);
        
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
        if(!isThreshold) {
            timelineData.add((int) timeline.calculateAverageSectionData());
        }

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
            Log.d("L-TEST", "class value: " + timelineData.get(i));
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

        BarDataSet disengagedBarSet = new BarDataSet(entries.subList(0, (thresholdVal / 10)), "BarDataSet");
        disengagedBarSet.setColor(getResources().getColor(R.color.colorAccentRed));
        BarDataSet engagedBarSet = new BarDataSet(entries.subList((thresholdVal / 10), entries.size()), "BarDataSet");
        engagedBarSet.setColor(getResources().getColor(R.color.colorAccentBlue));

        int engagedStudents = 0;
        for(int i = (thresholdVal / 10); i < countsArray.length; i++) {
            engagedStudents += countsArray[i];
        }
        int disengagedStudents = 0;
        for(int i = 0; i < (thresholdVal / 10); i++) {
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

    //[WIP: Deep] when student leaves this fragment, upload current timelineData to firebase.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //[WIP: Deep] when the view is destroyed, save values to firebase, so they can be used again.
        //FirebaseUtils.setSavedSliderVals(sectionRefKey, timelineData);
    }
}
