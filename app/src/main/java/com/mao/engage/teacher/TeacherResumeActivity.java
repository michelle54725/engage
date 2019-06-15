package com.mao.engage.teacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;

import java.util.ArrayList;

public class TeacherResumeActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private RecyclerView recyclerView;
    private SectionAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //UI: Recycler view with Choose Your Section on top
        setContentView(R.layout.activity_teacher_resume);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //backbutton function
        backBtn = findViewById(R.id.teacherBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //create Adapter that accesses firebase section data based teacher and display as buttons
        ArrayList<String> existingSectionsList = FirebaseUtils.getExistingSections();
        mAdapter = new SectionAdapter(existingSectionsList); //existingSectionList of String section_ids
        recyclerView.setAdapter(mAdapter);
    }
}
