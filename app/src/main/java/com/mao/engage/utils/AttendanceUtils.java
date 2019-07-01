package com.mao.engage.utils;

import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mao.engage.utils.eventlisteners.AttendanceChangeEventListener;
import com.mao.engage.utils.eventlisteners.AttendanceCheckEventListener;

import static com.mao.engage.FirebaseUtils.sectionMap;

public class AttendanceUtils {
    private static DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");

    public static void checkIsTakingAttendance(String section_ref_key) {
        Log.d("TEST", "calling checkIsTakingAttendance");
        FirebaseDatabase.getInstance().getReference("/Sections")
                .child(section_ref_key).child("isTakingAttendance")
                .addValueEventListener(new AttendanceCheckEventListener());
    }

    public static void setIsTakingAttendance(String section_ref_key, boolean isAttendance) {
        try {
            mSectionRef.child(section_ref_key).child("isTakingAttendance").setValue(isAttendance);
            sectionMap.get(section_ref_key).put("isTakingAttendance", isAttendance);
        } catch(Exception e) {
            Log.d("TEST: ", "isTakingAttendance DOES NOT EXIST in setIsTakingAttendance");
        }
    }

    public static void updateUserAttendance(String section_ref_key, String user_id) {
        String val = SectionUtils.getUsernameFromSectionMap(section_ref_key, user_id);
        if (val != null) {
            String[] parts = val.split("\\,");
            String name = parts[0];
            Log.d("TEST: ", "updated user attendance" + name);
            mSectionRef.child(section_ref_key).child("user_ids").child(user_id).setValue(name + ",p");
        }
        Log.d("TEST: ", "did NOT update user attendance, name = null");

    }

    // creates Listener for Section.user_ids.userid value (e.g. "Michelle, a") to update TA's attendance list
    public static void setAttendanceListener (final String section_ref_key, final String user_id) {
        Log.d("TEST[ATTENDANCE]", "LISTENER SET FOR: " + user_id);
        AttendanceChangeEventListener attendanceChangeEventListener
                = new AttendanceChangeEventListener(user_id, section_ref_key);
        mSectionRef.child(section_ref_key).child("user_ids").child(user_id).addValueEventListener(attendanceChangeEventListener);
    }
}
