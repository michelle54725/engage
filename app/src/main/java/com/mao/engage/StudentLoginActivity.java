/**
 * StudentLoginActivity: where students input the magic word to key into a section
 *  - If magic word matches existing section, create a new UserSession in the DB
 *
 * Triggered by:
 *  "JOIN AS STUDENT" button in StartActivity
 *
 * Transitions to:
 *  StudentClassActivity
 */

package com.mao.engage;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentLoginActivity extends AppCompatActivity {

    Button joinClassBtn;
    ImageButton backBtn;
    EditText magicWordEditText;
    TextView helloText;

    String mUsername;
    String mUID;
    UserSesh mUser;

    //for ease of access to different data
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
    DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_student_login);

        joinClassBtn = findViewById(R.id.joinClassBtn);
        backBtn = findViewById(R.id.backBtn);
        magicWordEditText = findViewById(R.id.magicWordEditText);
        helloText = findViewById(R.id.helloText);
        mUsername = getIntent().getStringExtra("name");
        helloText.setText(String.format("Hi, %s", mUsername));

        joinClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verify the magic word exists
                if (authenticateMagicWord()) {
                    // Create new UserSesh & store in DB
                    mUID = FirebaseUtils.getPsuedoUniqueID();
                    mUser = new UserSesh(mUID, mUsername,
                            Integer.valueOf(getMagicWord()), null);

                    // Verify the current UserSession has a section_ref_key
                    if (findSection(mUser)) {
                        Log.d("TEST", "set User's ref key to: " + mUser.getSection_ref_key());
                        Toast.makeText(StudentLoginActivity.this, "SUCCESS! Entering Section...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(StudentLoginActivity.this, StudentClassActivity.class);
                        intent.putExtra("uID", mUID);

                        Log.d("TEST", "put intent: " + mUID);
                        startActivity(intent);
                    } else {
                        Toast.makeText(StudentLoginActivity.this, "Error! Check for typos?", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StudentLoginActivity.this, "Invalid code - check for typos?", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // TODO: Firebase verify (make sure MagicWord exists)
    // TODO: further upgrade: check MagicWord corresponds to a section CURRENTLY in session
    private boolean authenticateMagicWord() {
        //not correctly implemented so students can key into non-existent sections
        return getMagicWord().length() == 3;
    }

    private String getMagicWord() {
        return magicWordEditText.getText().toString();
    }

    // Returns null if ref key not set yet
    private String getRefKey() {
        return mUser.getSection_ref_key();
    }

    boolean findSection(final UserSesh user) {
        FirebaseUtils.createUser(user);
        //TODO: problem: this code runs faster than EventListeners do their work. Use runnable?
        //TODO: or use local magic key/section ref key
//        user.setSection_ref_key(FirebaseUtils.allUsers.get(user.getUser_id()));
//        Log.d("TEST", "User's Ref Key is now: " + user.getSection_ref_key());
//        return user.getSection_ref_key() != null;
        return true; // forced true for now
    }
}
