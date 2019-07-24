/*
    StudentLoginActivity: where students input the magic word to key into a section
    - If magic word matches existing section, create a new UserSession in the DB

    Triggered by: "JOIN AS STUDENT" button from StartActivity

    Transitions to: StudentClassActivity
 */

package com.mao.engage.teacher

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mao.engage.FirebaseUtils
import com.mao.engage.R
import com.mao.engage.callback.CallbackManager
import com.mao.engage.student.StudentClassActivity
import com.mao.engage.UserSesh
import com.mao.engage.models.SectionSesh

import java.util.ArrayList
import java.util.HashMap

class StudentLoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var joinClassBtn: Button
    lateinit var backBtn: ImageButton
    lateinit var magicWordEditText: EditText
    lateinit var helloText: TextView

    lateinit var mUsername: String
    lateinit var mUID: String
    lateinit var mUser: UserSesh

    lateinit var existentMagicKeys: ArrayList<String>

    private val magicWord: String
        get() = magicWordEditText.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // UI: set to portrait, notification bar hidden
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_student_login)

        // bind views (UX components)
        joinClassBtn = findViewById(R.id.joinClassBtn)
        backBtn = findViewById(R.id.backBtn)
        magicWordEditText = findViewById(R.id.magicWordEditText)
        helloText = findViewById(R.id.helloText)
        mUsername = intent.getStringExtra("name")
        helloText.text = String.format("Hi, %s", mUsername)

        // set button listeners
        joinClassBtn.setOnClickListener(this)
        backBtn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.joinClassBtn -> authenticateMagicWord()
            R.id.backBtn -> finish()
            else -> Log.d("TEST:", "Button not accounted for")
        }
    }

    // TODO: Firebase verify (make sure MagicWord exists); further upgrade: check MagicWord corresponds to a section CURRENTLY in session
    private fun authenticateMagicWord() {
        if (magicWord.length != 2 && magicWord.length != 3) {
            Toast.makeText(this, "Typo? A Magic Keyword Should Contain 2 or 3 digits", Toast.LENGTH_LONG).show()
            Log.d("P-TEST:", "Invalidate Magic Key: magic key must be of length 3")
        }
        // not correctly implemented so students can key into non-existent sections
        val magicKeysReference = FirebaseDatabase.getInstance().getReference("/MagicKeys")
        magicKeysReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val magicKeysFromFirebase = ArrayList<String>()
                for (keySnapshot in snapshot.children) {
                    var magicKeyInString: String? = keySnapshot.key ?: continue
                    magicKeyInString = magicKeyInString!!.trim { it <= ' ' }
                    Log.d("P-TEST, Current Key: ", magicKeyInString)
                    magicKeysFromFirebase.add(magicKeyInString)
                    // here you can access to name property like university.name
                }
                /**
                 * Please see below for a concrete usage of the CallbackManagerClass
                 */
                val firebaseCallbackManager = CallbackManager<ArrayList<String>>()
                firebaseCallbackManager.onSuccess(magicKeysFromFirebase) {
                    input: ArrayList<String> ->
                    run {
                        existentMagicKeys = input
                        Log.d("P-TEST:", existentMagicKeys.toString())
                        if (existentMagicKeys.contains(magicWord.trim { it <= ' ' })) {
                            // create new UserSesh & store in DB
                            mUID = FirebaseUtils.getPsuedoUniqueID()
                            mUser = UserSesh(mUID, mUsername,
                                    Integer.valueOf(magicWord), null)
                            UserSesh.getInstance().user_id = mUID
                            UserSesh.getInstance().username = mUsername
                            UserSesh.getInstance().magic_key = Integer.valueOf(magicWord)
                            UserSesh.getInstance().section_ref_key = null; //haven't matched a section yet
                            UserSesh.getInstance().setIsStudent(true)

                            findSection(mUser)
                        } else {
                            Toast.makeText(this@StudentLoginActivity, "Invalid code - check for typos?", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("P-TEST:", "Encountered Database Error")
            }
        })
    }

    // Find SectionSesh corresponding to User's MagicWord
    // - updates User with section_ref_key then pushes it to Firebase under /Users
    // - adds UserId to corresponding Section's user_ids list
    // replaces: FirebaseUtils.createUser(user)
    private fun findSection(user: UserSesh) {
        val mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections")
        val mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions")

        mSectionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    var sectionRefKeyFromFirebase = ""
                    for (snapshot in dataSnapshot.children) {
                        val section = snapshot.getValue(SectionSesh::class.java)
                        if (section!!.getMagic_key() == user.magic_key) {
                            sectionRefKeyFromFirebase = section.getRef_key()

                            // reflect change in section_ref_key in both DB and UserSesh object
                            user.section_ref_key = section.getRef_key()
                            mUsersRef.child(user.user_id).setValue(user)

                            // add this user to user_ids of section
                            val userIdRef = mSectionRef.child(section.getRef_key()).child("user_ids")
                            val userUpdates = HashMap<String, Any>()
                            userUpdates[user.user_id] = user.username + ",a"
                            userIdRef.updateChildren(userUpdates)

                            //TODO: eliminate sectionMap code once eliminated sectionMap
                            val user_id_map = FirebaseUtils.sectionMap.get(section.getRef_key())!!.get("user_ids") as HashMap<String, String>
                            user_id_map[user.user_id] = user.username + ",a"
                            Log.d("M-Test", FirebaseUtils.sectionMap.get(section.getRef_key())!!.get("user_ids").toString())
                        }
                    }
                    val firebaseCallbackManager = CallbackManager<String>()
                    firebaseCallbackManager.onSuccess(sectionRefKeyFromFirebase) {
                        input: String ->
                        run {
                            val sectionRefKey : String = input
                            Log.d("M-TEST:", sectionRefKey)
                            if (sectionRefKey != "") {
                                Log.d("M-TEST", "set User's ref key to: " + mUser.section_ref_key)
                                Toast.makeText(this@StudentLoginActivity, "SUCCESS! Entering Section...", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@StudentLoginActivity, StudentClassActivity::class.java)
                                intent.putExtra("uID", mUID)
                                intent.putExtra("magic_key", magicWord)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@StudentLoginActivity, "Error! Check for typos?", Toast.LENGTH_SHORT).show()
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
        })
    }
}
