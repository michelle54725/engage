package com.mao.engage.teacherclassactivity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.database.*
import com.mao.engage.FirebaseUtils
import com.mao.engage.callback.CallbackManager
import com.mao.engage.teacher.StartActivity
import com.mao.engage.teacher.TeacherCreateClassActivity
import com.mao.engage.teacher.TeacherOptionsActivity


internal fun checkIfTeacherInDB(nameEditText: String, context: Context) {
    Log.d("TEST", "reached setTeacherListener method")
    val mTeachersRef = FirebaseDatabase.getInstance().getReference("/Teachers")
    var isTeacherInDB = false
    //will uncomment this when UserSesh starts working
    //if (!UserSesh.getInstance().checkIsStudent()) {
    mTeachersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // sanity check snapshot is non-null (i.e. /Sections contains data)
            if (dataSnapshot.exists()) {
                var teacherID = ""

                /** Loop through all sections under /Sections */
                for (snapshot in dataSnapshot.children) {
                    val teacher : Map<String, String> = dataSnapshot.getValue() as Map<String, String>

                    for (key in teacher!!.keys) {
                        //Log.d("TEST", key)
                        //Log.d("TEST", FirebaseUtils.getPsuedoUniqueID())
                        if (key.equals(FirebaseUtils.getPsuedoUniqueID())) {
                            Log.d("TEST", "FOUND my teacher")
                            teacherID = key //set our callbackData once found
                            isTeacherInDB = true
                            break
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
                            val intent = Intent(context, TeacherOptionsActivity::class.java)
                            intent.putExtra("name", nameEditText)
                            context.startActivity(intent)
                        } else {
                            goToCreateClassActivity(context)
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

    // Send user's name to CreateClassActivity and start it

    //}
}

fun goToCreateClassActivity(context: Context) {
    if (StartActivity.isValidName()) {
        val intent = Intent(context, TeacherCreateClassActivity::class.java)
        intent.putExtra("name", StartActivity.getName())
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Please provide a name", Toast.LENGTH_SHORT).show()
    }
}

