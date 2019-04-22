package com.mao.engage;

import android.util.Log;

import java.util.ArrayList;

public class TimelineDataRetrieval {
    //constructor
    private ArrayList<Integer> averageSliderVal; //each index is one time step (10s)
    private static ArrayList<Float> timelineArray;
    public TimelineDataRetrieval() {
        averageSliderVal = new ArrayList<>();
        timelineArray = new ArrayList<>();
    }
    //create random data
    public ArrayList<Integer> createRandomStudentData(int students) {
        ArrayList<Integer> studentSliderVals = new ArrayList<>();
        for (int i = 0; i < students; i++) {
            int r = (int) (101 * Math.random());
            studentSliderVals.add(r);
        }
        return studentSliderVals;
    }

    public float calculateAverageData() {
        float total = 0;
        for (int i : FirebaseUtils.sectionSliders.values()) {
            total += i;
            Log.d("TEST", "Firbase sectionslider called in Calculate Average Data" + i);
        }
        Log.d("TEST", "calculate average : " + total/FirebaseUtils.sectionSliders.size());
        return total/FirebaseUtils.sectionSliders.size();
    }

    //get individual slider val
    public int mySliderValue(String user_id) {
        return FirebaseUtils.getSliderVal(user_id);
    }

    //create fixed data
    public ArrayList<Integer> createFixedData(int students) {
        ArrayList<Integer> studentSliderVals = new ArrayList<>();
        for (int i = 0; i < students; i++) {
            studentSliderVals.add(i%100);
        }
        return studentSliderVals;
    }

    //create fixed data average
    public ArrayList<Integer> average(int count) {
        ArrayList<Integer> vals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            vals.add(i);
        }
        return vals;
    }

    public static void addData(float dataPoint) {
        timelineArray.add(dataPoint);
    }

    public static ArrayList<Float> getTimelineArray() {
        return timelineArray;
    }

    //create fixed data, one datapoint
    public int dataFixed(){
        return 10;
    }

    public int dataRandom() {
        return (int) (Math.random() * 101);
    }
}

