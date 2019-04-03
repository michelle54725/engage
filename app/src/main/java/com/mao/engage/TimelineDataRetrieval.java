package com.mao.engage;

import java.util.ArrayList;

public class TimelineDataRetrieval {
    //constructor
    private ArrayList<Integer> averageSliderVal; //each index is one time step (10s)
    public TimelineDataRetrieval() {

        averageSliderVal = new ArrayList<>();

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

    public ArrayList<Integer> calculateAverageData(ArrayList<Integer> myList) {
        int total = 0;
        for (int i : myList) {
            total += i;
        }
        averageSliderVal.add(total/myList.size());
        return averageSliderVal;
    }

    //create fixed data
    public ArrayList<Integer> createFixedData(int students) {
        ArrayList<Integer> studentSliderVals = new ArrayList<>();
        for (int i = 0; i < students; i++) {
            studentSliderVals.add(i%100);
        }
        return studentSliderVals;
    }
}

