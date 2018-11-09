package com.mao.engage;

import android.content.Intent;
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
                    startActivity(new Intent(StartActivity.this, StudentLoginActivity.class));
                } else {
                    Toast.makeText(StartActivity.this, "Choose a real name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidName() {
        return !nameEditText.getText().toString().replaceAll("\\s","").isEmpty();
    }
}
