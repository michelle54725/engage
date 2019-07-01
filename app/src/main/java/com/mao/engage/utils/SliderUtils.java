package com.mao.engage.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mao.engage.utils.eventlisteners.SectionSliderEventListener;
import com.mao.engage.utils.eventlisteners.UserSectionIDChangeEventListener;

import static com.mao.engage.FirebaseUtils.allUsers;
import static com.mao.engage.FirebaseUtils.sectionSliders;

public class SliderUtils {
    private static DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
    private static DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions");

    public static void setUserIdinSectionListener(final String ref_key) {
        Log.d("TEST[user_ids]", "in setUserIdinSectionListener");
        UserSectionIDChangeEventListener userSectionIDChangeEventListener
                = new UserSectionIDChangeEventListener(ref_key);

        DatabaseReference useridsRef = mSectionRef.child(ref_key).child("user_ids");
        if (useridsRef != null) {
            useridsRef.addChildEventListener(userSectionIDChangeEventListener);
        } else {
            Log.d("TEST[user_ids]", "USERID REF IS NULL");
        }
    }

    public static int getSliderVal(String user_id) {
        if (!sectionSliders.containsKey(user_id)) {
            sectionSliders.put(user_id, 50);
        }
        Log.d("TEST", "mySliderValue: " + sectionSliders.get(user_id));
        return sectionSliders.get(user_id);
    }

    public static void setSliderVal(final String user_id, final int value) {
        String key = allUsers.get(user_id);
        Log.d("TEST", "Attempting to write " + value + " to " + user_id + "...");
        if (key != null) {
            mUsersRef.child(user_id).child("slider_val").setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("TEST", "New slider wrote to DB: " + value);
                    sectionSliders.put(user_id, value);
                    Log.d("TEST", "new slider val in section sliders" + sectionSliders.get(user_id));
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TEST", "New slider wrote to DB: " + "FAILED");
                        }
                    });
        }
    }

    // creates Listener for UserSessions's slider_val to update sectionSliders HashMap
    public static void setSliderListener(final String user_id) {
        SectionSliderEventListener sectionSliderEventListener =
                new SectionSliderEventListener(user_id);
        mUsersRef.child(user_id).child("slider_val").addValueEventListener(sectionSliderEventListener);
    }
}
