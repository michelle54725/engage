package com.mao.engage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AttendeeListActivity extends AppCompatActivity {

    private static AttendeeListActivity mActivity;
    private ImageButton backBtn;
    private RecyclerView recyclerView;
    private static AttendeeListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static HashMap<String, String> userNames; // k: user_id, v: name
    private static String mSectionRefKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = AttendeeListActivity.this;

        //UI: Recycler view with Attendee title on top
        setContentView(R.layout.activity_attendees);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //backbutton function
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //create Adapter that accesses userdata in specific section
        mSectionRefKey = getIntent().getStringExtra("sectionRefKey");
        userNames = FirebaseUtils.getUserNames(mSectionRefKey);

        Log.d("TEST[usernames]", "username size " + Integer.toString(userNames.size()));
        Log.d("TEST[usernames]", userNames.values().toString());
        mAdapter = new AttendeeListAdapter(new ArrayList<>(userNames.values()), mSectionRefKey); //List of String user_names
        recyclerView.setAdapter(mAdapter);
    }

    protected static void markPresent(String user_id) {
        Log.d("TEST[MARKING]", "Present " + user_id);
        if (userNames != null) {
            String userName = userNames.get(user_id);
            userNames.put(user_id, "P! " + userName); //TODO: make this green instead of change name
            Log.d("TEST[MARKING]", "updated userNames: " + userNames);
            refreshList();
        }
    }

    protected static void markAbsent(String user_id) {
        Log.d("TEST[MARKING]", "Absent " + user_id);
        if (userNames != null) {
            String userName = userNames.get(user_id);
            userNames.put(user_id, "A! " + userName); //TODO: make this green instead of change name
            refreshList();
        }
    }

    protected static void refreshList() {
        if (mActivity == null) return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userNames = FirebaseUtils.getUserNames(mSectionRefKey);
                mAdapter.refreshList(new ArrayList<>(userNames.values()));
            }
        });
    }
}