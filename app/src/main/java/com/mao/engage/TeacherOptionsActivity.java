package com.mao.engage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TeacherOptionsActivity extends AppCompatActivity {

    Button createButton;
    Button resumeButton;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_options);

        name = getIntent().getStringExtra("name");
        createButton = findViewById(R.id.createNewBtn);
        resumeButton = findViewById(R.id.resumeBtn);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherOptionsActivity.this, TeacherCreateClassActivity.class);
                            intent.putExtra("name", name);
                            startActivity(intent);
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show list of existing sections to choose from
            }
        });
    }
}
