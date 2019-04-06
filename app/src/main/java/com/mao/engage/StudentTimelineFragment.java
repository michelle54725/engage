package com.mao.engage;

import android.content.Context;
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
public class StudentTimelineFragment extends Fragment {

    TimerTask retrieveDataTask;
    private TextView sectionNameText;
    private TextView magicWordText;

    private ArrayList<Entry> classValues;
    private ArrayList<Entry> myValues;
    private LineDataSet mySet;
    private LineDataSet classSet;

    private LineData lineData;
    private LineChart chart;
    private TextView startTimeText;
    private TextView endTimeText;

    //Required empty public constructor
    public StudentTimelineFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * TODO:
     * Send slider value from Class fragment via Intent.putExtra("class")
     * Create various additional variables for our graph (startTimeText, endTimeText, set these times to appropriate times from our db)
     * Implement a Timer here that calls the retrieveData function every 10s to get new updated values
     * make sure timer terminates after allotted time
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_timeline, container, false);
        chart = view.findViewById(R.id.chart);
        //startTimeText = view.findViewById(R.id.startTimeText); endTimeText = view.findViewById(R.id.endTimeText);
        //startTimeText.setText("3:00PM"); endTimeText.setText("4:00PM");
        retrieveData();

        return view;
    }

    /*
        Retrieve the data from the entire section using sectionID
        to average for the Average line
        While retrieving from section, if sectionID is of the user, add data to user's timeline
        Call from retrieveDataTask=new TimerTask() every 10 seconds

        Put values in a line graph
     */
    private void retrieveData() {
        myValues = new ArrayList<>();
        classValues = new ArrayList<>();

        ArrayList<Integer> meColors = new ArrayList<>();
        ArrayList<Integer> classColors = new ArrayList<>();

        final int count = 10;
        final int range = 100;

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range);
            myValues.add(new Entry(i, val));
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

        mySet = new LineDataSet(myValues, "Me");
        classSet = new LineDataSet(classValues, "Class");

        mySet.setLineWidth(2f);
        mySet.setColor(Color.WHITE);
        mySet.setCircleColors(meColors);
        mySet.setCircleRadius(3f);
        mySet.setDrawCircleHole(false);
        mySet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(entry == mySet.getEntryForIndex(9)) {
                    return "Me";
                } else {
                    return "";
                }
            }
        });
        mySet.setValueTextColor(Color.WHITE);
        mySet.setValueTextSize(12f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mySet.setValueTypeface(getResources().getFont(R.font.quicksand_bold));
        }
        mySet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

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


        lineData = new LineData(mySet, classSet);
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;

    private TimelineFragment.OnFragmentInteractionListener mListener;

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TimelineFragment.OnFragmentInteractionListener) {
            mListener = (TimelineFragment.OnFragmentInteractionListener) context;
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
}
