package com.mao.engage;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentLoginActivity extends AppCompatActivity {

    Button joinClassBtn;
    ImageButton backBtn;
    EditText magicWordEditText;
    TextView helloText;

    String mUsername;
    String mUID;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
    DatabaseReference mUsersRef = FirebaseDatabase.getInstance().getReference("/UserSessions");
    UserSesh mUser;

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
                if (authenticateMagicWord()) {
                    // Create new UserSesh & store in DB
                    mUID = FirebaseUtils.getPsuedoUniqueID();
                    mUser = new UserSesh(mUID, mUsername,
                            Integer.valueOf(getMagicWord()), null);
                    findSection(mUser);
                    // Now mUser has the corresponding SectionSesh ref key
                    mUsersRef.child(mUID).setValue(mUser);

                    updateSectionUsersList();

                    Intent intent = new Intent(StudentLoginActivity.this, StudentClassActivity.class);
                    intent.putExtra("magicWord", getMagicWord());
                    startActivity(intent);
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

    // TODO: Firebase verify (make sure MagicWord not in use)
    private boolean authenticateMagicWord() {
        return getMagicWord().length() == 3;
    }

    private String getMagicWord() {
        return magicWordEditText.getText().toString();
    }

    // Returns null if ref key not set yet
    private String getRefKey() {
        return mUser.getSection_ref_key();
    }

    // takes in a UserSesh and sets .section_ref_key based on .magic_key
    void findSection(final UserSesh user) {
        Log.d("TEST", "in findSection");
        String refKey = FirebaseUtils.findSectionWithUser(user);
    }

    // Add current user to SectionSesh's user_ids list
    void updateSectionUsersList() {
        Log.d("TEST", "in updateSectionUsersList");
        mSectionRef.child(getRefKey()).child("user_ids").child(mUID);
//        mSectionRef.child(getRefKey()).child("user_ids").setValue(mListUsers);
    }
}
