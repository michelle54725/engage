package com.mao.engage;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AttendeeListAdapter extends RecyclerView.Adapter<AttendeeListAdapter.MyViewHolder> {
    private List<String> attendeeList;
    private String section_ref_key;

    /*
    section buttons referenced button design from section_list_row
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
                    Log.d("TEST", "changed button color to light blue on click");
                    user.setBackgroundColor(Color.parseColor("#2FA6D8"));
                    FirebaseUtils.updateUserAttendance(section_ref_key, FirebaseUtils.getPsuedoUniqueID());
                }
            });
        }
    }

    /*
    constructs an adapter based on the attendee list passed in -- pass db
    through AttendeeListActivity
     */
    public AttendeeListAdapter(List<String> lst, String section_ref_key) {
        this.attendeeList = lst;
        this.section_ref_key = section_ref_key;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_list_row, parent, false);
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
        holder.user.setText(FirebaseUtils.getNameFromValue(userName));

        holder.user = (Button) holder.view1.findViewById(R.id.sectionBtn);
        Log.d("TEST", "before button check");
        Log.d("TEST", "text: " + holder.user.getText().toString());
        if (FirebaseUtils.isPresent(userName)) {
            Log.d("TEST", "changed button color to light blue");
            holder.user.setBackgroundColor(Color.parseColor("#2FA6D8"));
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
