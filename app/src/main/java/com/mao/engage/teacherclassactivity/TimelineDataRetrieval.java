/*
    How data is retrieved from Firebase for the timeline graphs
    Called in StudentTimelineFragment
 */
package com.mao.engage.teacherclassactivity;

import android.util.Log;

import com.mao.engage.FirebaseUtils;
import com.mao.engage.utils.SliderUtils;

import java.util.ArrayList;

public class TimelineDataRetrieval {

    private static ArrayList<Float> timelineArray;

    //Constructor
    public TimelineDataRetrieval() {
        timelineArray = new ArrayList<>();
    }

    //Calculates the section average given the slider values of all the students in a section retrieved from Firebase
    public float calculateAverageSectionData() {
        float total = 0;
        for (int i : FirebaseUtils.sectionSliders.values()) {
            total += i;
            Log.d("TEST", "Firbase sectionslider called in Calculate Average Data" + i);
        }
        Log.d("TEST", "calculate average : " + total/FirebaseUtils.sectionSliders.size());
        return total / FirebaseUtils.sectionSliders.size();
    }

    //get individual slider val
    public int getMySliderValue(String user_id) { return SliderUtils.getSliderVal(user_id); }

    //not used
    public static void addData(float dataPoint) {
        timelineArray.add(dataPoint);
    }
    //not used
    public static ArrayList<Float> getTimelineArray() {
        return timelineArray;
    }
}

