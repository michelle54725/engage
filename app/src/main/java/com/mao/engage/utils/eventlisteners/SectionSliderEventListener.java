package com.mao.engage.utils.eventlisteners;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.mao.engage.FirebaseUtils.sectionSliders;

public class SectionSliderEventListener implements ValueEventListener {

    private String mUserId;
    private static DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions");

    public SectionSliderEventListener(String userId) {
        mUserId = userId;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            if (sectionSliders.containsKey(mUserId)) {
                Log.d("TEST", "\nreading '" + mUserId + "'; \n with slider val: " + dataSnapshot.getValue());
                sectionSliders.put(mUserId, Integer.valueOf(dataSnapshot.getValue().toString()));

                // For testing purposes
                Log.d("PRINT_TEST", "\n Printing contents of sectionSliders Hashmap...");
                for (String user : sectionSliders.keySet()) {
                    Integer value = sectionSliders.get(user);
                    Log.d("PRINT_TEST", user + ": " + value.toString());
                }

            } else {
                Log.d("TEST", "ERROR: user_id not found in sectionSlider HashMap");
            }
        } else {
            Log.d("TEST", "deleting Listener for " + mUserId);
            mUsersRef.child(mUserId).child("slider_val").removeEventListener(this);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
