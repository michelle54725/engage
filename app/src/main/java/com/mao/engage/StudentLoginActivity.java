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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class StudentLoginActivity extends AppCompatActivity {

    Button joinClassBtn;
    ImageButton backBtn;
    EditText magicWordEditText;
    TextView helloText;

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

        helloText.setText(String.format("Hi, %s", getIntent().getStringExtra("name")));

        joinClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authenticateMagicWord()) {
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

    private boolean authenticateMagicWord() {
        return getMagicWord().length() == 3;
    }

    private String getMagicWord() {
        return magicWordEditText.getText().toString();
    }
}
