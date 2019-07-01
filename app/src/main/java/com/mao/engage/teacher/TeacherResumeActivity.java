/*
    Displays a list of the user's existing sections that when clicked opens the corresponding
    TeacherClassActivity (implemented in TeacherResumeActivity_Adapter).

    Triggered by: "Resume Section" button from TeacherOptionsActivity.
 */

package com.mao.engage.teacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;
import com.mao.engage.utils.SectionUtils;

import java.util.ArrayList;

public class TeacherResumeActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private RecyclerView recyclerView;
    private TeacherResumeActivity_Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI: Recycler view with "Choose from your existing sections:" on top
        setContentView(R.layout.activity_teacher_resume);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // feeds a list of the user's existing sections to the adapter which handles
        // onClick functionality and reviving the SectionSesh
        ArrayList<String> existingSectionsList = SectionUtils.getExistingSections(); //list of section_name
        mAdapter = new TeacherResumeActivity_Adapter(existingSectionsList);
        recyclerView.setAdapter(mAdapter);

        // back button
        backBtn = findViewById(R.id.teacherBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
