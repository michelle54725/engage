package com.mao.engage.teacherclassactivity

import android.util.Log
import com.google.firebase.database.*
import com.mao.engage.FirebaseUtils
import com.mao.engage.callback.CallbackManager


fun setTeacherListener() {
    Log.d("TEST", "reached setTeacherListener method")
    val mTeachersRef = FirebaseDatabase.getInstance().getReference("/Teachers")
    var isTeacherInDB = false
    //will uncomment this when UserSesh starts working!
    //if (!UserSesh.getInstance().checkIsStudent()) {
    mTeachersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // sanity check snapshot is non-null (i.e. /Sections contains data)
            if (dataSnapshot.exists()) {
                // sectionRefKeyFromFirebase will be our callbackData
                var teacherID = ""

                /** Loop through all sections under /Sections */
                for (snapshot in dataSnapshot.children) {
                    Log.d("TEST", "looked through datasnapshots teacher")
                    val teacher : Map<String, String> = dataSnapshot.getValue() as Map<String, String>

                    Log.d("TEST", teacher.toString())
                    Log.d("TEST", (teacher!!.keys).toString())

                    for (key in teacher!!.keys) {
                        Log.d("TEST", key)
                        Log.d("TEST", FirebaseUtils.getPsuedoUniqueID())
                        if (key.equals(FirebaseUtils.getPsuedoUniqueID())) {
                            Log.d("TEST", "FOUND my teacher")
                            teacherID = key //set our callbackData once found
                            isTeacherInDB = true
                        }
                    }
                    break
                    // check for matching teacher
                }

                /** At this point we can safely assume we've completed the for-loop
                and use the CallbackManager to proceed  */
                val firebaseCallbackManager = CallbackManager<String>()
                firebaseCallbackManager.onSuccess(teacherID) {
                    input: String ->
                    run {
                        Log.d("TEST", "reached run statement")

                        val teacherID : String = input
                        if (teacherID.isNotBlank() && isTeacherInDB) { //isNotBlank returns false for "" and "  "
                            Log.d("TEST", "reached correct statement in teacherinDB")
                            FirebaseUtils.isTeacherInDB = 1
                        } else {
                            Log.d("TEST", "reached wrong statement in teacherinDB")
                            FirebaseUtils.isTeacherInDB = -1
                        }
                    }
                }
            } else {
                Log.d("TEST-FAIL", "dataSnapshot DNE")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("TEST-FAIL", error.message)
        }
    })// marks end of addListenerForSingleValueEvent
    //}
}


