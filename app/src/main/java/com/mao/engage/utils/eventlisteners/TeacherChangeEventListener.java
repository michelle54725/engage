package com.mao.engage.utils.eventlisteners;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import static com.mao.engage.FirebaseUtils.allTeachers;

public class TeacherChangeEventListener implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
        String id = dataSnapshot.getKey();
        Log.d("TEST", "[new Teacher Child] \n" + id);
        allTeachers.add(id);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String id = dataSnapshot.getKey();
        Log.d("TEST", "[deleting Teacher Child] \n" + id);
        allTeachers.remove(id);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
