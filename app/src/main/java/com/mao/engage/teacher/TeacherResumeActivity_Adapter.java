/*
    The adapter used in TeacherResumeActivity.java
    to display a list of the user's existing sections.
 */

package com.mao.engage.teacher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;
import com.mao.engage.TeacherClassActivity;

import java.util.HashMap;
import java.util.List;

public class TeacherResumeActivity_Adapter extends RecyclerView.Adapter<TeacherResumeActivity_Adapter.MyViewHolder> {
    private List<String> sectionSeshList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // section buttons reference button design from section_list_row.xml
        public Button section;

        MyViewHolder(View view) {
            super(view);
            section = (Button) view.findViewById(R.id.sectionBtn);

            section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open Section sesh with given magic word, section name, section ref key
                    HashMap<String, String> mySectionsHashMap = FirebaseUtils.getExistingSectionsHashMap();
                    String mSectionRefKey = mySectionsHashMap.get(section.getText().toString());

                    Intent intent = new Intent(section.getContext(), TeacherClassActivity.class);
                    intent.putExtra("sectionRefKey", mSectionRefKey);
                    intent.putExtra("section_name", section.getText().toString());
                    intent.putExtra("magic_word", FirebaseUtils.getMagicKey(mSectionRefKey) + "");
                    section.getContext().startActivity(intent);
                }
            });
        }
    }

    // constructs an adapter based on the section list passed in from TeacherResumeActivity
    // note: reads from DB through TeacherResumeActivity
    TeacherResumeActivity_Adapter(List<String> lst) {
        this.sectionSeshList = lst;
    }

    @NonNull @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    // sets button to have the section_id of the section
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String section_id = sectionSeshList.get(position);
        holder.section.setText(section_id);
    }

    @Override
    public int getItemCount() {
        return sectionSeshList.size();
    }
}
