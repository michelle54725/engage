package com.mao.engage.utils.eventlisteners;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.mao.engage.FirebaseUtils;

import java.util.Map;

import static com.mao.engage.FirebaseUtils.sectionAttendance;
import static com.mao.engage.utils.AttendanceUtils.setAttendanceListener;
import static com.mao.engage.utils.SliderUtils.setSliderListener;

public class UserSectionIDChangeEventListener implements ChildEventListener {

    private String mRefKey;

    public UserSectionIDChangeEventListener(String refKey) {
        mRefKey = refKey;
    }
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        //copied and pasted here
        Log.d("TEST[user_ids]", "putting... " + dataSnapshot.getKey() + ": " + dataSnapshot);
        Map<String, String> user_ids = (Map<String, String>) FirebaseUtils.sectionMap.get(mRefKey).get("user_ids");
        Log.d("TEST[user_ids]", "user_ids is currently: " + user_ids);
        if (user_ids != null) {
            if (((String)dataSnapshot.getValue()).contains(",")) {
                // LOCAL SYNC: put user in sectionMap
                user_ids.put(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
            }
        }

        Log.d("TEST[user_id]", "LISTENER SAYS copying user to local sectionSliders: " + dataSnapshot.getKey());
        String user_id = dataSnapshot.getKey();
        // LOCAL SYNC: put user in sectionSliders
        FirebaseUtils.sectionSliders.put(user_id, 50); // default slider = 50
        setSliderListener(user_id);

        // LOCAL SYNC: put user in sectionAttendance
        sectionAttendance.put(user_id, false);
        setAttendanceListener(mRefKey, user_id); //this is what above TO-DO is referring to, delete this comment once resolved
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        // Someone changed their name

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Log.d("TEST", "removing user from local hashmaps: " + dataSnapshot.getKey());
        String user_id = dataSnapshot.getKey();
        FirebaseUtils.sectionSliders.remove(user_id);
        Map<String, String> userMap = (Map) FirebaseUtils.sectionMap.get(mRefKey).get("user_ids");
        userMap.remove(user_id);
        boolean removed = userMap.containsKey(user_id);
        Log.d("TEST", "proper local removal: " + !removed);
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
