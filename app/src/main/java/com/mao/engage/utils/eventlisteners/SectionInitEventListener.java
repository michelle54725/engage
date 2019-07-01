package com.mao.engage.utils.eventlisteners;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.mao.engage.FirebaseUtils;

import java.util.HashMap;

public class SectionInitEventListener implements ChildEventListener {

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        String sectionRefKey = dataSnapshot.getKey();
        Log.d("TEST", "[new Section Child]: " + sectionRefKey);
        // TODO: rely on callback from the server side (Firebase) instead of local hash maps
        HashMap<String, Object> hashyMap = new HashMap<>();
        hashyMap.put("user_ids", new HashMap<String, String>());


        for(DataSnapshot child : dataSnapshot.getChildren()) {
            if (!(child.getKey().equals("user_ids"))) {
                hashyMap.put(child.getKey(), child.getValue().toString());
                Log.d("TEST[new Section Child]", child.getKey() + " " + child.getValue());
            }
        }
        Log.d("TEST: ", "SECTION ITEMS added");
        FirebaseUtils.sectionMap.put(sectionRefKey, hashyMap);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
