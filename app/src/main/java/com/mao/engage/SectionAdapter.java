package com.mao.engage;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.MyViewHolder> {
    private List<SectionSesh> sectionSeshList;

    /*
    section buttons referenced button design from section_list_row
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button section;

        public MyViewHolder(View view) {
            super(view);
            section = (Button) view.findViewById(R.id.sectionBtn);
        }
    }

    /*
    constructs an adapter based on the section list passed in -- pass db
    through TeacherResumeActivity
     */
    public SectionAdapter(List<SectionSesh> sectionSeshList) {
        this.sectionSeshList = sectionSeshList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.section_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    /*
    sets button to have the section_id of the section
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SectionSesh section = sectionSeshList.get(position);
        //SectionSesh section = new SectionSesh();
        holder.section.setText(section.getSection_id());
    }

    @Override
    public int getItemCount() {
        return sectionSeshList.size();
    }
}
