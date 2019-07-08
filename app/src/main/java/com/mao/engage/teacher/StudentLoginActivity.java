/*
    StudentLoginActivity: where students input the magic word to key into a section
    - If magic word matches existing section, create a new UserSession in the DB

    Triggered by: "JOIN AS STUDENT" button from StartActivity

    Transitions to: StudentClassActivity
 */

package com.mao.engage.teacher;

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
import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;
import com.mao.engage.student.StudentClassActivity;
import com.mao.engage.UserSesh;

public class StudentLoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button joinClassBtn;
    ImageButton backBtn;
    EditText magicWordEditText;
    TextView helloText;

    String mUsername;
    String mUID;
    UserSesh mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI: set to portrait, notification bar hidden
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_student_login);

        // bind views (UX components)
        joinClassBtn = findViewById(R.id.joinClassBtn);
        backBtn = findViewById(R.id.backBtn);
        magicWordEditText = findViewById(R.id.magicWordEditText);
        helloText = findViewById(R.id.helloText);
        mUsername = getIntent().getStringExtra("name");
        helloText.setText(String.format("Hi, %s", mUsername));

        // set button listeners
        joinClassBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.joinClassBtn:
                // verify the magic word exists
                if (authenticateMagicWord()) {
                    // create new UserSesh & store in DB
                    mUID = FirebaseUtils.getPsuedoUniqueID();
                    mUser = new UserSesh(mUID, mUsername,
                            Integer.valueOf(getMagicWord()), null);
                    UserSesh.getInstance().setUser_id(mUID);
                    UserSesh.getInstance().setUsername(mUsername);
                    UserSesh.getInstance().setMagic_key(Integer.valueOf(getMagicWord()));
                    UserSesh.getInstance().setSection_ref_key(mUser.getSection_ref_key());
                    UserSesh.getInstance().setIsStudent(true);

                    // verify the current UserSession has a section_ref_key
                    if (findSection(mUser)) {
                        Log.d("TEST", "set User's ref key to: " + mUser.getSection_ref_key());
                        Toast.makeText(StudentLoginActivity.this, "SUCCESS! Entering Section...", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(StudentLoginActivity.this, StudentClassActivity.class);
                        intent.putExtra("uID", mUID);
                        intent.putExtra("magic_key", getMagicWord());
                        startActivity(intent);
                    } else {
                        Toast.makeText(StudentLoginActivity.this, "Error! Check for typos?", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StudentLoginActivity.this, "Invalid code - check for typos?", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.backBtn:
                finish();
                break;
            default:
                Log.d("TEST:","Button not accounted for");
                break;
        }
    }

    // TODO: Firebase verify (make sure MagicWord exists); further upgrade: check MagicWord corresponds to a section CURRENTLY in session
    private boolean authenticateMagicWord() {
        // not correctly implemented so students can key into non-existent sections
        return getMagicWord().length() == 3;
    }

    boolean findSection(final UserSesh user) {
        FirebaseUtils.createUser(user);
        //TODO: problem: this code runs faster than EventListeners do their work. Use runnable? or use local magic key/section ref key
//        user.setSection_ref_key(FirebaseUtils.allUsers.get(user.getUser_id()));
//        Log.d("TEST", "User's Ref Key is now: " + user.getSection_ref_key());
//        return user.getSection_ref_key() != null;
        return true; // forced true for now
    }

    private String getMagicWord() {
        return magicWordEditText.getText().toString();
    }
}
