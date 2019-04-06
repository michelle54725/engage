package com.mao.engage;

import android.graphics.Color;
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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
public class StudentTimelineActivitiy extends Fragment {

    TimerTask retrieveDataTask;
    private TextView sectionNameText;
    private TextView magicWordText;
    private LineChart lineChartAverage;
    private LineChart lineChartUser;


    //public void onCreate(Bundle savedInstanceState)
    //public View onCreateView()

    /*
        Retrieve the data from the entire section to average for the Average line
        While retrieving from section, if sectionID is of the user, add data to user's timeline
        Call from retrieveDataTask=new TimerTask() every 10 seconds

        Put values in a line graph
     */
    private void retrieveData() {}
    private double averageData() {}


}
