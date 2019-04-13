package com.mao.engage;

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

    public float calculateAverageData(ArrayList<Integer> myList) {
        float total = 0;
        for (int i : myList) {
            total += i;
        }
        return total/myList.size();
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
}

