/**
 * StartActivity: the home screen
 *
 * User inputs name and selects "Student" or "Teacher".
 */
package com.mao.engage;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    Button joinStudentBtn;
    Button joinTeacherBtn;
    EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI: set to portrait, notification bar hidden
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        joinStudentBtn = findViewById(R.id.joinStudentBtn);
        joinTeacherBtn = findViewById(R.id.joinTeacherBtn);
        nameEditText = findViewById(R.id.nameEditText);

        joinStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidName()) {
                    Intent intent = new Intent(StartActivity.this, StudentLoginActivity.class);
                    intent.putExtra("name", nameEditText.getText().toString());
                    startActivity(intent);
                } else {
                    // TODO: replace Toasts with something cleaner
                    Toast.makeText(StartActivity.this, "Choose a real name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        joinTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseUtils.teacherIsInDB()) {
                    // Teacher already in DB
                    Intent intent = new Intent(StartActivity.this, TeacherOptionsActivity.class);
                    intent.putExtra("name", nameEditText.getText().toString());
                    startActivity(intent);
                } else {
                    // Teacher not in DB yet (i.e. first time user)
                    goToCreateClassActivity();
                }
            }
        });

        FirebaseUtils.setUserListener();
        FirebaseUtils.setTeacherListener();
    }

    // Send user's name to CreateClassActivity and start it
    void goToCreateClassActivity(){
        if (isValidName()) {
            Intent intent = new Intent(StartActivity.this, TeacherCreateClassActivity.class);
            intent.putExtra("name", getName());
            startActivity(intent);
        } else {
            Toast.makeText(StartActivity.this, "Please provide a name", Toast.LENGTH_SHORT).show();
        }
    }

    // Removes spaces
    private String getName() {
        return nameEditText.getText().toString().replaceAll("\\s","");
    }

    // Name validity check
    private boolean isValidName() {
        //TODO: make more rigorous check
        return !getName().isEmpty();
    }
}
