/*
 * Used by AttendeeListActivity's recycler view functionality to display names of students in a current section.
 */
package com.engage.education.teacherclassactivity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

import com.engage.education.FirebaseUtils;
import com.engage.education.R;

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
            //allows teacher to manually add students (set student to present) if google nearby doesn't function properly
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (String id : userMap.keySet()) {
                        String name = FirebaseUtils.getNameFromValue(userMap.get(id));
                        if (name.equals(user.getText())) {
                            FirebaseUtils.updateUserAttendance(section_ref_key, id);
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
        return new MyViewHolder(itemView);
    }

    /*
    sets button to have the section_id of the section
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String userName = attendeeList.get(position);
        //String userName = (attendeeList.get(position));
        holder.user.setText(FirebaseUtils.getNameFromValue(userName));

        holder.user = (Button) holder.view1.findViewById(R.id.sectionBtn);
        if (FirebaseUtils.isPresent(userName)) {
            holder.user.setBackgroundColor(Color.parseColor("#20B537"));
        }
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    // called by AttendeeListActivity whenever changes are made to firebase. Allows the user list to refresh.
    public void refreshList(List<String> newAttendeeList) {
        this.attendeeList.clear();
        this.attendeeList.addAll(newAttendeeList);
        notifyDataSetChanged();
    }
}
