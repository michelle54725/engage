package com.mao.engage.student

import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mao.engage.FirebaseUtils.sectionSliders
import com.mao.engage.UserSesh
import com.mao.engage.callback.CallbackManager

internal fun MeFragment_findRefKey_writeSliderVal(user_id: String, value: Int) {
    val mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions")
    mUsersRef.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.d("L-TEST", "findRefKey onCancelled")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                var sectionRefKeyFromFirebase = ""
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(UserSesh::class.java)
                    if (user!!.getUser_id().equals(user_id)) {
                        sectionRefKeyFromFirebase = user.section_ref_key
                    }
                }

                val firebaseCallBackManager = CallbackManager<String>()
                firebaseCallBackManager.onSuccess(sectionRefKeyFromFirebase) {
                    input: String ->
                    run {
                        val sectionRefKey : String = input
                        if (sectionRefKey.isNotBlank()) {
                            mUsersRef.child(user_id).child("slider_val").setValue(value).addOnSuccessListener(OnSuccessListener<Void> {
                                sectionSliders.put(user_id, value)
                            })
                                    .addOnFailureListener(OnFailureListener { Log.d("TEST", "New slider wrote to DB: " + "FAILED") })
                        }
                    }
                }
            } else {
                Log.d("L-TEST-FAIL", "datasnapshot DNE")
            }

        }
    })
}
