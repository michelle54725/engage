/*
 * Displays all the users in the class (who have entered the magic key).
 * Changes students names from red to green (absent to present) if Google Nearby authenticates them.
 * Can manually change students from absent to present by clicking on their names.
 */
package com.mao.engage.teacherclassactivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;

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

        //back button function
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //create Adapter that accesses userdata in specific section
        mSectionRefKey = getIntent().getStringExtra("sectionRefKey");
        //userNames stores all the usernames in this section
        userNames = FirebaseUtils.getUserNames(mSectionRefKey);

        Log.d("TEST[usernames]", "username size " + Integer.toString(userNames.size()));
        Log.d("TEST[usernames]", userNames.values().toString());
        //mAdapter = new AttendeeListAdapter(new ArrayList<>(userNames.values()), mSectionRefKey); //List of String user_names
        mAdapter = new AttendeeListAdapter(userNames, mSectionRefKey); //List of String user_names
        recyclerView.setAdapter(mAdapter);
    }

    // marks a user present based on database information (intended call in firebase)
    protected static void markPresent(String user_id) {
        Log.d("TEST[MARKING]", "Present " + user_id);
        if (userNames != null) {
            String userName = userNames.get(user_id);
            userNames.put(user_id, "P! " + userName); //TODO: make this green instead of change name
            Log.d("TEST[MARKING]", "updated userNames: " + userNames);
            refreshList();
        }
    }

    // marks a user absent based on database information (called in firebase)
    public static void markAbsent(String user_id) {
        Log.d("TEST[MARKING]", "Absent " + user_id);
        if (userNames != null) {
            String userName = userNames.get(user_id);
            userNames.put(user_id, "A! " + userName); //TODO: make this green instead of change name
            refreshList();
        }
    }

    // list of users needs to be refreshed based on Firebase's onDataChange.
    // new list of users is restored in userNames and the page is refreshed
    public static void refreshList() {
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