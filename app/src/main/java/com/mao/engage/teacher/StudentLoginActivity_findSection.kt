/*
    findSection is a synchronous task that attempts to find a section in the DB that has
    a magic_key equal to the passed in UserSesh's magic_key.

    If successfully matched, it makes the following changes to the DB:
        1)    /Users: updates UserSesh with section_ref_key
        2) /Sections: adds UserSesh's id to corresponding Section's user_ids list

    This function replaces FirebaseUtils.createUser(user) that was used previously.
*/

package com.mao.engage.teacher

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mao.engage.FirebaseUtils
import com.mao.engage.UserSesh
import com.mao.engage.callback.CallbackManager
import com.mao.engage.models.SectionSesh
import com.mao.engage.student.StudentClassActivity
import java.util.HashMap

internal fun findSection(user: UserSesh, context: Context) {
    val mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections")
    val mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions")

    // use SingleValueEvent Listener to read Firebase (only reads it once)
    mSectionRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // sanity check snapshot is non-null (i.e. /Sections contains data)
            if (dataSnapshot.exists()) {
                // sectionRefKeyFromFirebase will be our callbackData
                var sectionRefKeyFromFirebase = ""

                /** Loop through all sections under /Sections */
                for (snapshot in dataSnapshot.children) {

                    // we stored sections as SectionSesh classes so we retrieve them as such
                    val section = snapshot.getValue(SectionSesh::class.java)

                    // check for matching magic_key
                    if (section!!.getMagic_key() == user.magic_key) {

                        sectionRefKeyFromFirebase = section.getRef_key() //set our callbackData once found

                        user.section_ref_key = section.getRef_key() //reflect in section_ref_key in UserSesh object
                        mUsersRef.child(user.user_id).setValue(user) //push updates to user (in DB)

                        // push this user to user_ids of section (in DB)
                        val userIdRef = mSectionRef.child(section.getRef_key()).child("user_ids")
                        val userUpdates = HashMap<String, Any>()
                        userUpdates[user.user_id] = user.username + ",a" //"a" for absent
                        userIdRef.updateChildren(userUpdates)

                        //TODO: eliminate the following code once eliminated sectionMap
                        val user_id_map = FirebaseUtils.sectionMap.get(section.getRef_key())!!.get("user_ids") as HashMap<String, String>
                        user_id_map[user.user_id] = user.username + ",a"
                        Log.d("M-Test", FirebaseUtils.sectionMap.get(section.getRef_key())!!.get("user_ids").toString())
                    }
                }

                /** At this point we can safely assume we've completed the for-loop
                    and use the CallbackManager to proceed */
                val firebaseCallbackManager = CallbackManager<String>()
                firebaseCallbackManager.onSuccess(sectionRefKeyFromFirebase) {
                    input: String ->
                    run {
                        val sectionRefKey : String = input
                        if (sectionRefKey.isNotBlank()) { //isNotBlank returns false for "" and "  "
                            Toast.makeText(context, "SUCCESS! Entering Section...", Toast.LENGTH_SHORT).show()

                            // make Intent to StudentClassActivity via context (passed in from StudentLoginActivity)
                            val intent = Intent(context, StudentClassActivity::class.java)
                            intent.putExtra("name", user.username)
                            intent.putExtra("uID", user.user_id)
                            intent.putExtra("magic_key", user.magic_key)
                            context.startActivity(intent)
                        } else {
                            // did not find match
                            Toast.makeText(context, "Error! Check for typos?", Toast.LENGTH_SHORT).show()
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
    
    /** Note on why we need to do this:
     *  mSectionRef.addListenerForSingleValueEvent(...) contains code that is run asynchronously, i.e.
     *  any code here will likely execute before the for-loop in OnDataChange finishes, thus falling
     *  to the trap of asynchronous tasks. e.g. if we tried to match a magic_key here, we might
     *  end with "no match" even though there was a match because this code is executing at a time
     *  before we finished scanning the DB in OnDataChange */

}