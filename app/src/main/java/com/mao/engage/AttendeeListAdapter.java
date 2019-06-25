package com.mao.engage;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

public class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.MyViewHolder> {
    private List<String> attendeeList;
    private String section_ref_key;
    private Map<String, String> userMap;

    /*
    section buttons referenced button design from activity_teacher_resume_item.xml
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button user;
        public View view1;

        public MyViewHolder(View view) {
            super(view);
            view1 = view;
            user = (Button) view.findViewById(R.id.sectionBtn);

            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (String id : userMap.keySet()) {
                        String name = FirebaseUtils.getNameFromValue(userMap.get(id));
                        Log.d("TEST", "name from userMap: " + name);
                        if (name.equals(user.getText())) {
                            Log.d("TEST", "change attendance status in Firebase");
                            FirebaseUtils.updateUserAttendance(section_ref_key, id);

                            Log.d("TEST", "change button color to presen: green on click");
                            user.setBackgroundColor(Color.parseColor("#20B537"));
                        }
                    }
                }
            });
        }
    }

    /*
    constructs an adapter based on the attendee list passed in -- pass db
    through AttendeeListActivity
     */
    public AttendeeListAdapter(Map<String, String> userNames, String section_ref_key) {
        this.attendeeList = new ArrayList<>(userNames.values());
        this.userMap = userNames;
        this.section_ref_key = section_ref_key;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_teacher_resume_item, parent, false);
        Log.d("TEST", "AttendeeListAdapter");
        return new MyViewHolder(itemView);
    }

    /*
    sets button to have the section_id of the section
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String userName = attendeeList.get(position);
        //String userName = (attendeeList.get(position));
        Log.d("TEST", "userName: " + userName);
        holder.user.setText(FirebaseUtils.getNameFromValue(userName));

        holder.user = (Button) holder.view1.findViewById(R.id.sectionBtn);
        Log.d("TEST", "status: " + FirebaseUtils.isPresent(userName));
        if (FirebaseUtils.isPresent(userName)) {
            Log.d("TEST", "changed button color to present: green");
            holder.user.setBackgroundColor(Color.parseColor("#20B537"));
        }
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    public void refreshList(List<String> newAttendeeList) {
        this.attendeeList.clear();
        this.attendeeList.addAll(newAttendeeList);
        notifyDataSetChanged();
    }
}
