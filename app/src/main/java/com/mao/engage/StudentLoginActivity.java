package com.mao.engage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StudentLoginActivity extends AppCompatActivity {

    Button joinClassBtn;
    EditText magicWordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_student_login);

        joinClassBtn = findViewById(R.id.joinClassBtn);
        magicWordEditText = findViewById(R.id.magicWordEditText);

        joinClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authenticateMagicWord()) {

                } else {
                    Toast.makeText(StudentLoginActivity.this, "Invalid code - check for typos?", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean authenticateMagicWord() {
        return true;
    }
}
