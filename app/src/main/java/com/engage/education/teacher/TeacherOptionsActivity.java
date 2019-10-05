/*
    TeacherOptionsActivity: where a returning Teacher selects to either:
        a) Resume or b) Create a new section to start.

    Triggered by: "JOIN AS TEACHER" button from StartActivity IFF the Teacher is returning
        - "returning" = user_id exists under /Teachers in FB

    Transitions to:
        TeacherResumeActivity if "RESUME SECTION" clicked, OR
        TeacherClassActivity if "CREATE NEW SECTION"
 */

package com.engage.education.teacher;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.engage.education.FirebaseUtils;
import com.engage.education.R;
import com.engage.education.UserConfig;

public class TeacherOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton backBtn;
    Button createButton;
    Button resumeButton;
    TextView helloText;

    String name;
    String uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_options);

        // bind views (UX components)
        backBtn = findViewById(R.id.backBtn);
        createButton = findViewById(R.id.createNewBtn);
        resumeButton = findViewById(R.id.resumeBtn);
        helloText = findViewById(R.id.helloText);

        // personalize UI by displaying username
        name = getIntent().getStringExtra("name");
        helloText.setText(String.format("Hi, %s", name));

        // set button listeners
        backBtn.setOnClickListener(this);
        createButton.setOnClickListener(this);
        resumeButton.setOnClickListener(this);

        // set DB listeners
        uID = FirebaseUtils.getPsuedoUniqueID();
        FirebaseUtils.setExistingSectionsListener(uID);

        UserConfig.Companion.setUsername(name);
        UserConfig.Companion.setUserType(UserConfig.UserType.TEACHER);
        UserConfig.Companion.setUserID(uID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                Intent intent3 = new Intent(TeacherOptionsActivity.this, StartActivity.class);
                intent3.putExtra("name", name);
                startActivity(intent3);
                break;
            case R.id.createNewBtn:
                Intent intent = new Intent(TeacherOptionsActivity.this, TeacherCreateClassActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
                break;
            case R.id.resumeBtn:
                Intent intent2 = new Intent(TeacherOptionsActivity.this, TeacherResumeActivity.class);
                intent2.putExtra("name", name);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TeacherOptionsActivity.this, StartActivity.class);
        startActivity(intent);
    }
}