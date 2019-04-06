package com.mao.engage;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

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

    private LineData lineData;

    private LineChart chart;
    private TextView startTimeText;
    private TextView endTimeText;

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
        //startTimeText = view.findViewById(R.id.startTimeText); endTimeText = view.findViewById(R.id.endTimeText);
        //startTimeText.setText("3:00PM"); endTimeText.setText("4:00PM");
        retrieveData();

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
