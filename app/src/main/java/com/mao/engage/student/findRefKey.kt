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

internal fun findRefKey(user_id: String, value: Int) {
    val mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions")
    mUsersRef.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            Log.d("L-TEST", "findRefKey onCancelled")
        }

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                var sectionRefKeyFromFirebase = ""
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(UserSesh::class.java)
                    Log.d("L-TEST", "a snapshot: " + user!!.getUser_id())
                    if (user!!.getUser_id().equals(user_id)) {
                        Log.d("L-TEST", "user found. Section: " + user.section_ref_key)
                        sectionRefKeyFromFirebase = user.section_ref_key
                    }
                }

                val firebaseCallBackManager = CallbackManager<String>()
                firebaseCallBackManager.onSuccess(sectionRefKeyFromFirebase) {
                    input: String ->
                    run {
                        val sectionRefKey : String = input
                        Log.d("L-TEST", sectionRefKey)
                        if (sectionRefKey != "") {
                            mUsersRef.child(user_id).child("slider_val").setValue(value).addOnSuccessListener(OnSuccessListener<Void> {
                                Log.d("L-TEST", "New slider wrote to DB: $value")
                                sectionSliders.put(user_id, value)
                                Log.d("L-TEST", "new slider val in section sliders" + sectionSliders.get(user_id)!!)
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