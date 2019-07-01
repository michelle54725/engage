package com.mao.engage.utils.eventlisteners;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mao.engage.teacherclassactivity.AttendanceFragment;
import com.mao.engage.teacherclassactivity.AttendeeListActivity;
import com.mao.engage.utils.SectionUtils;

import java.util.Map;
import java.util.Objects;

import static com.mao.engage.FirebaseUtils.sectionAttendance;
import static com.mao.engage.FirebaseUtils.sectionMap;

public class AttendanceChangeEventListener implements ValueEventListener {

    private String mUserId;
    private String mSectionRefKey;

    public AttendanceChangeEventListener(String userId, String sectionRefKey) {
        mUserId = userId;
        mSectionRefKey = sectionRefKey;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Log.d("TEST[ATTENDANCE]", "in onDataChange for " + mSectionRefKey);
        if (dataSnapshot.exists()) {
            Log.d("TEST[ATTENDANCE]", "snapshot: " + dataSnapshot);
            Log.d("TEST[ATTENDANCE]", "status: " + SectionUtils.getAttendanceFromSectionMap(mSectionRefKey, mUserId));
            if (SectionUtils.isPresent(Objects.requireNonNull(dataSnapshot.getValue()).toString())) {
                // update sectionAttendance AND set student's name to green if "Michelle, p"
                Log.d("TEST[ATTENDANCE]", mSectionRefKey + ": Present");
                sectionAttendance.put(mSectionRefKey, true);

                //update sectionMap
                Map<String, Map<String, String>> hashyMap = sectionMap.get(mUserId);
                hashyMap.get("user_ids").put(mSectionRefKey, dataSnapshot.getValue().toString());

                AttendeeListActivity.refreshList();
                AttendanceFragment.refreshCount();
            } else if (!SectionUtils.isPresent(dataSnapshot.getValue().toString())) {
                // update sectionAttendance AND set student's name to green if "Michelle, a"
                Log.d("TEST[ATTENDANCE]", mSectionRefKey + ": Absent");
                sectionAttendance.put(mSectionRefKey, false);
                AttendeeListActivity.markAbsent(mSectionRefKey);

                //update sectionMap
                Map<String, Map<String, String>> hashyMap = sectionMap.get(mUserId);
                hashyMap.get("user_ids").put(mSectionRefKey, dataSnapshot.getValue().toString());

                AttendeeListActivity.refreshList();
                AttendanceFragment.refreshCount();
            } else {
                Log.d("TEST[ATTENDANCE]", mSectionRefKey + ": Not P or A");
            }
        } else {
            Log.d("TEST[ATTENDANCE]", "snapshot does not exist for " + mSectionRefKey);
            //Log.d("TEST[ATTENDANCE]", "deleting attendance Listener for " + user_id);
            //mSectionRef.child(section_ref_key).child("user_ids").child(user_id).removeEventListener(this);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
