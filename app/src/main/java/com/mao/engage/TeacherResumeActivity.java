package com.mao.engage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class TeacherResumeActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private RecyclerView recyclerView;
    private SectionAdapter mAdapter;
    private List<SectionSesh> sectionSeshList = new ArrayList<>();
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

        //create Adapter that access firebase section data based teacher and display as buttons
        mAdapter = new SectionAdapter(sectionSeshList);
        recyclerView.setAdapter(mAdapter);

//        for (int i = 0; i < 10; i++) {
//            SectionSesh movie = new SectionSesh("1", "2",
//                    String ta_key, String section_id,
//                    String ref_key, int magic_key,
//            ArrayList<String> user_ids);
//            movieList.add(movie);
//            System.out.println(i);
//        }
    }
}
