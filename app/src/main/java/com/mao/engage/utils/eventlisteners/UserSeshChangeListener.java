package com.mao.engage.utils.eventlisteners;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.mao.engage.UserSesh;

import static com.mao.engage.FirebaseUtils.allUsers;

public class UserSeshChangeListener implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
        UserSesh newUser = dataSnapshot.getValue(UserSesh.class);
        Log.d("TEST", "[new User Child] \n" + newUser.getUser_id() + "\n" + newUser.getSection_ref_key());
        allUsers.put(newUser.getUser_id(), newUser.getSection_ref_key());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
        //if a student leaves a class and joins a different one, we need to update allUsers locally.
        UserSesh changedUser = dataSnapshot.getValue(UserSesh.class);
        Log.d("TEST", "[changed User Child] \n" + changedUser.getUser_id() + "\n" + changedUser.getSection_ref_key());
        allUsers.put(changedUser.getUser_id(), changedUser.getSection_ref_key());
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        UserSesh newUser = dataSnapshot.getValue(UserSesh.class);
        Log.d("TEST", "[deleting User Child] \n" + newUser.getUser_id() + "\n" + newUser.getSection_ref_key());
        allUsers.remove(newUser.getUser_id());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
