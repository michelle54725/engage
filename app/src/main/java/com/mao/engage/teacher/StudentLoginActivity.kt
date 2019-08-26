/*
    StudentLoginActivity: where students input the magic word to key into a section
    - If magic word matches existing section, create a new UserSession in the DB

    Triggered by: "JOIN AS STUDENT" button from StartActivity

    Transitions to: StudentClassActivity (via findSection)
 */

package com.mao.engage.teacher

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
import com.mao.engage.UserConfig
import com.mao.engage.callback.CallbackManager
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

    private fun authenticateMagicWord() {
        if (magicWord.length != 2 && magicWord.length != 3) {
            Toast.makeText(this, "Typo? A Magic Keyword Should Contain 2 or 3 digits", Toast.LENGTH_LONG).show()
            Log.d("P-TEST:", "Invalidate Magic Key: magic key must be of length 3")
        }

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
                firebaseCallbackManager.onSuccess(magicKeysFromFirebase) { input: ArrayList<String> ->
                    run {
                        existentMagicKeys = input
                        Log.d("P-TEST:", existentMagicKeys.toString())
                        if (existentMagicKeys.contains(magicWord.trim { it <= ' ' })) {
                            // create new UserSesh & store in DB
                            mUID = FirebaseUtils.getPsuedoUniqueID()
                            mUser = UserSesh(mUID, mUsername,
                                    Integer.valueOf(magicWord), null)
                            // setting the UserConfig variables for global access
                            UserConfig.username = mUsername
                            UserConfig.userID = mUID
                            UserConfig.userType = UserConfig.UserType.STUDENT
                            // call helper function to search in DB for section matching user's magic_key
                            // and start StudentClassActivity if found
                            findSection(mUser, this@StudentLoginActivity)
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
}
