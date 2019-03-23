package com.mao.engage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeacherResumeActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private RecyclerView recyclerView;
    private SectionAdapter mAdapter;
    private List<SectionSesh> sectionSeshList = new ArrayList<>();
    private FirebaseDatabase db;
    private DatabaseReference dbr;
    private RecyclerView.LayoutManager layoutManager;
    private HashMap<String, SectionSesh> sectionSeshKeys = new HashMap<>();


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
        db = FirebaseDatabase.getInstance();
        getFirebaseData();
        mAdapter = new SectionAdapter(sectionSeshList, sectionSeshKeys);
    }

    public void getFirebaseData() {
        //String userID = user.getUid();
        //dbr = db.getReference("Teachers").child(userID);
        //dbr = db.getReference("Teachers/" + userID + "/existingSections");
        dbr = db.getReference("Sections"); //only path that has worked
        dbr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SectionSesh section = dataSnapshot.getValue(SectionSesh.class);
                sectionSeshList.add(section);
                sectionSeshKeys.put(section.section_id, section);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
