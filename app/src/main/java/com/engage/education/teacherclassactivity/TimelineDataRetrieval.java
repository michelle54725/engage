/*
    How data is retrieved from Firebase for the timeline graphs
    Called in StudentTimelineFragment
 */
package com.engage.education.teacherclassactivity;

import com.engage.education.FirebaseUtils;

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
        }
        return total / FirebaseUtils.sectionSliders.size();
    }

    //get individual slider val
    public int getMySliderValue(String user_id) { return FirebaseUtils.getSliderVal(user_id); }

    //not used
    public static void addData(float dataPoint) {
        timelineArray.add(dataPoint);
    }
    //not used
    public static ArrayList<Float> getTimelineArray() {
        return timelineArray;
    }
}

