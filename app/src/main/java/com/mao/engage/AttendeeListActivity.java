package com.mao.engage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AttendeeListActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private RecyclerView recyclerView;
    private AttendeeListAdapter attendeeAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public AttendeeListActivity() {
        //Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        attendeeAdapter = new AttendeeListAdapter(existingSectionsList); //existingSectionList of String section_ids
        recyclerView.setAdapter(attendeeAdapter);
    }

}
