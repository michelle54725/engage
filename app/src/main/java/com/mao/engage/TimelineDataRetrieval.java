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

    //create fixed data, one datapoint
    public int dataFixed(){
        return 10;
    }

    public int dataRandom() {
        return (int) (Math.random() * 101);
    }
}

