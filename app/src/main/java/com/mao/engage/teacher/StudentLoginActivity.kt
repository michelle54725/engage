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

import java.util.ArrayList

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
                            UserSesh.getInstance().section_ref_key = mUser.section_ref_key
                            UserSesh.getInstance().setIsStudent(true)

                            // verify the current UserSession has a section_ref_key
                            if (findSection(mUser)) {
                                Log.d("TEST", "set User's ref key to: " + mUser.section_ref_key)
                                Toast.makeText(this@StudentLoginActivity, "SUCCESS! Entering Section...", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@StudentLoginActivity, StudentClassActivity::class.java)
                                intent.putExtra("uID", mUID)
                                intent.putExtra("magic_key", magicWord)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this@StudentLoginActivity, "Error! Check for typos?", Toast.LENGTH_SHORT).show()
                            }
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

    private fun findSection(user: UserSesh): Boolean {
        FirebaseUtils.createUser(user)
        //TODO: problem: this code runs faster than EventListeners do their work. Use runnable? or use local magic key/section ref key
        //        user.setSection_ref_key(FirebaseUtils.allUsers.get(user.getUser_id()));
        //        Log.d("TEST", "User's Ref Key is now: " + user.getSection_ref_key());
        //        return user.getSection_ref_key() != null;
        return true // forced true for now
    }
}
