package com.mao.engage.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mao.engage.FirebaseUtils;
import com.mao.engage.models.SectionSesh;
import com.mao.engage.utils.eventlisteners.SectionChangeEventListener;
import com.mao.engage.utils.eventlisteners.SectionInitEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class deals with handling some basic operations concerning
 * a section for both students and teachers.
 * @author paulshao
 */
public class SectionUtils {
    private static DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
    private static DatabaseReference mTeachersRef = FirebaseDatabase.getInstance().getReference("/Teachers");


    // Returns "p" or "a" corresponding to a student in the Sections child section_ref_key.user_ids.user_id child
    public static String getAttendanceFromSectionMap(String user_id, String section_ref_key) {
        Map<String, Map<String, String>> hashyMap = FirebaseUtils.sectionMap.get(section_ref_key);
        Map<String, String> user_ids = hashyMap.get("user_ids");
        Log.d("TEST[ATTENDANCE]", "users: " + user_ids.values().toString());

        String username = user_ids.get(user_id);
        Log.d("TEST[ATTENDANCE]", "username: " + username);
        if (username != null) {
            int index = username.indexOf(",");
            Log.d("TEST[ATTENDANCE]", "getAttendanceFromSectionMap of " + user_id + ": " + username.substring(index + 1).replaceAll("\\s", ""));
            return username.substring(index + 1).replaceAll("\\s", "");
        } else {
            //username DNE
            return "DNE";
        }
    }

    public static void setSectionListener() {
        mSectionRef.addChildEventListener(new SectionInitEventListener());
    }

    /*
        Set user as present in Firebase
        Assumes user exists in section
        Called in AttendeeListAdapter when button is clicked
     */
    public static void markPresent(String user_id, String section_ref_key) {
        Log.d("TEST", "marking student present");
        DatabaseReference userIdRef = mSectionRef.child(section_ref_key).child("user_ids");
        userIdRef.child(user_id).setValue(getNameFromSectionMap(user_id, section_ref_key) + ",p");
    }



    /*
        Returns the name of a user in sectionMap
     */
    public static String getNameFromSectionMap(String userID, String section_ref_key) {
        Map<String, Map<String, String>> hashyMap = FirebaseUtils.sectionMap.get(section_ref_key);
        String value = hashyMap.get("user_ids").get(userID);
        return getNameFromValue(value);
    }

    /*
       Returns the name of a value
    */
    public static String getNameFromValue(@NonNull String value) {
        int index = value.indexOf(",");
        Log.d("TEST", "getNameFromValue " + value.substring(0, index));
        return value.substring(0, index);
    }

    /*
        Returns the present status of a user given the value
     */
    public static boolean isPresent(String value) {
        int index = value.indexOf(",");
        String status = value.substring(index + 1);
        Log.d("TEST", "isPresent " + value.substring(index));
        return status.equals("p");
    }

    /*
        Returns ArrayList of existing sections for a teacher
        Called in TeacherResumeActivity_Adapter
     */
    public static ArrayList<String> getExistingSections() {
        ArrayList<String> existingList = new ArrayList<>();
        for (String key : FirebaseUtils.existingSections.keySet()) {
            existingList.add(key);
        }
        return existingList;
    }

    /*
        Returns a hashmap of the existing sections of a teacher
        K: section_name; V: section_ref;
        Called in TeacherResumeActivity_Adapter
     */
    public static HashMap<String, String> getExistingSectionsHashMap() {
        return FirebaseUtils.existingSections;
    }

    /*
    gets Threshhold set my teacher to be used in TimelineFragment
     */
    public static double getThreshold(String refKey) {
        if (FirebaseUtils.sectionMap.get(refKey).get("threshold") == null) {
            FirebaseUtils.sectionMap.get(refKey).put("threshold", 0);
        }
        Log.d("TEST: ", "getThreshold called" + Double.parseDouble(FirebaseUtils.sectionMap.get(refKey).get("threshold").toString()));
        return Double.parseDouble(FirebaseUtils.sectionMap.get(refKey).get("threshold").toString());
    }

    /*
    changes threshold value as set by teacher in TimelineFragment
     */
    public static void changeThresholdVal(String refKey, double threshold) {
        //TODO:doesn't update the firebase, just updates local values -- ask if that is that ok?
        Log.d("TEST: ", "changeThresholdVal called");
        FirebaseUtils.sectionMap.get(refKey).put("threshold", threshold);
    }

    /*
    Gets magicKey of a section
    Called in TeacherResumeActivity_Adapter
     */
    public static long getMagicKey(String refKey) {
        String magicKey = FirebaseUtils.sectionMap.get(refKey).get("magic_key").toString();
        Log.d("TEST", magicKey);
        //Long.parseLong(sectionMap.get(refKey).get("magic_key").toString())
        return Long.parseLong(magicKey);
    }

    /*
        Currently when called, has a HashMap get null object reference error even when called on magic_key
     */
    public static String getStartTime(String refKey) {
        String s = FirebaseUtils.sectionMap.get(refKey).get("a_start").toString();
        Log.d("TEST", s);
        return s.substring(s.length() - 7);
    }

    public static String getEndTime(String refKey) {
        String s = FirebaseUtils.sectionMap.get(refKey).get("b_end").toString();
        Log.d("TEST", s);
        return s.substring(s.length() - 7);
    }

    public static String getUsernameFromSectionMap(String section_ref_key, String user_id) {
        Map<String, Map<String, String>> hashyMap = FirebaseUtils.sectionMap.get(section_ref_key);
        Map<String, String> user_ids = hashyMap.get("user_ids");
        Log.d("TEST[GETUSERNAME]", "users: " + user_ids.values().toString());

        String username = user_ids.get(user_id);
        Log.d("TEST[GETUSERNAME]", "username: " + username);
        if (username != null) {
            int index = username.indexOf(",");
            Log.d("TEST[GETUSERNAME]", "getAttendanceFromSectionMap of " + user_id + ": " + username.substring(0, index).replaceAll("\\s", ""));
            return username.substring(0, index).replaceAll("\\s", "");
        } else {
            //username DNE
            return "";
        }
    }


    //adds existing section information to hashmap
    public static void setExistingSectionsListener(String userID) {
        Log.d("TEST: ", "setExistingSections Called");
        mTeachersRef.child(userID).child("existingSections").addChildEventListener(new SectionChangeEventListener());
    }

    // Add a section child in SectionSesh
    public static void createSection(SectionSesh section) {
        Log.d("TEST", "in FirebaseUtils.createSection...");
        mSectionRef.child(section.ref_key).setValue(section);
        FirebaseDatabase.getInstance().getReference("/MagicKeys").child("" + section.getMagic_key()).setValue(section.getRef_key());
    }

}
