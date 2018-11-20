package com.mao.engage;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
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
                    intent.putExtra("name", getName());
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
                if (isValidName()) {
                    Intent intent = new Intent(StartActivity.this, TeacherCreateClassActivity.class);
                    intent.putExtra("name", getName());
                    startActivity(intent);
                } else {
                    Toast.makeText(StartActivity.this, "Please provide a name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        FirebaseUtils.setUserListener();
    }

    private String getName() {
        return nameEditText.getText().toString().replaceAll("\\s","");
    }

    private boolean isValidName() {
        return !getName().isEmpty();
    }
}
