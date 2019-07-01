package com.mao.engage.utils.eventlisteners;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mao.engage.FirebaseUtils;
import com.mao.engage.UserSesh;
import com.mao.engage.models.SectionSesh;

import java.util.HashMap;
import java.util.Map;

public class UserCreateEventListener implements ValueEventListener {

    private UserSesh mUserSesh;
    private static DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions");
    private static DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");

    public UserCreateEventListener(UserSesh userSesh) {
        mUserSesh = userSesh;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            Log.d("TEST", "in OnDataChange w MW: " + String.valueOf(mUserSesh.getMagic_key()));
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                SectionSesh section = snapshot.getValue(SectionSesh.class);
                if (section.getMagic_key() == mUserSesh.getMagic_key()) {
                    Log.d("TEST", "\n[FOUND MATCH] " + "\nmagic key: " + section.getMagic_key() + "; \n" + "ref key: " + section.getRef_key());
                    // Reflect change in section_ref_key in both DB and UserSesh object
                    mUserSesh.setSection_ref_key(section.getRef_key());
                    mUsersRef.child(mUserSesh.getUser_id()).setValue(mUserSesh);
                    DatabaseReference userIDref = mSectionRef.child(section.getRef_key()).child("user_ids");
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put(mUserSesh.getUser_id(), mUserSesh.getUsername()+ ",a");
                    userIDref.updateChildren(userUpdates);

                    HashMap<String,String> user_id_map = (HashMap<String,String>) FirebaseUtils.sectionMap.get(section.getRef_key()).get("user_ids");
                    user_id_map.put(mUserSesh.getUser_id(), mUserSesh.getUsername() + ",a");
                }
            }
        } else {
            Log.d("TEST-FAIL", "dataSnapshot DNE");
        }
    }

    @Override
    public void onCancelled(DatabaseError error) {
        Log.d("TEST-FAIL", "failed to read value");
    }
}
