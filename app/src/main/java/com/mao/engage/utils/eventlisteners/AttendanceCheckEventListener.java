package com.mao.engage.utils.eventlisteners;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mao.engage.student.MeFragment;

public class AttendanceCheckEventListener implements ValueEventListener {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            boolean attendanceClicked = Boolean.parseBoolean(dataSnapshot.getValue().toString()); //true if teacher is taking attendance!
            if(attendanceClicked) {
                MeFragment.startSendingMessages();
            } else {
                try {
                    MeFragment.stopSendingMessages();
                } catch(Exception e) {
                    Log.d("TEST: ", "context is not available");

                }
            }

        } else {
            Log.d("TEST: ", "Is taking attendance NOT FOUND in checkIsTakingAttendance for students!");
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
