package com.mao.engage.teacher;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;

public class TeacherOptionsActivity extends AppCompatActivity {

    ImageButton backBtn;
    Button createButton;
    Button resumeButton;
    TextView helloText;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_options);

        name = getIntent().getStringExtra("name");
        backBtn = findViewById(R.id.backBtn);
        createButton = findViewById(R.id.createNewBtn);
        resumeButton = findViewById(R.id.resumeBtn);
        helloText = findViewById(R.id.helloText3);
        helloText.setText(String.format("Hi, %s", name));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                Intent intent = new Intent(TeacherOptionsActivity.this, TeacherResumeActivity.class);
                startActivity(intent);
                // show list of existing sections to choose from
            }
        });
        FirebaseUtils.setExistingSectionsListener(FirebaseUtils.getPsuedoUniqueID());
    }
}
