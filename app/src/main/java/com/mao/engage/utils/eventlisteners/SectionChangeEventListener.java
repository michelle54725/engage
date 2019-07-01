package com.mao.engage.utils.eventlisteners;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.mao.engage.FirebaseUtils;

public class SectionChangeEventListener implements ChildEventListener {
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        onChildChanged(dataSnapshot, s);
        Log.d("TEST: ", "EXISTING SECTIONS added");
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        onChildStateModified(dataSnapshot.getValue(String.class), dataSnapshot.getKey());
        Log.d("TEST: ", "EXISTING SECTIONS changed");
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        FirebaseUtils.existingSections.remove(dataSnapshot.getValue(String.class));
        Log.d("TEST: ", "EXISTING SECTIONS removed");

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    private void onChildStateModified(String sectionID, String sectionRef) {
        FirebaseUtils.existingSections.put(sectionRef, sectionID);
    }
}
