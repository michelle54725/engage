package com.mao.engage;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;
import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.MyViewHolder> {
    private List<SectionSesh> sectionSeshList;
    private HashMap<String, SectionSesh> sectionKeys;

    /*
    section buttons referenced button design from section_list_row
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button section;

        public MyViewHolder(View view) {
            super(view);
            section = (Button) view.findViewById(R.id.sectionBtn);

            section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open Section sesh with given magic word, section name, section ref key
                    //Todo: find better way to find the current magic key instead of iterating over every value in the list
                    SectionSesh mySection = sectionKeys.get(section.getText().toString());
                    int mMagicWord = mySection.magic_key;
                    String mSectionRefKey = mySection.ref_key;
                    //TODO: get section ref key from database; currently is just fake data.



                    Intent intent = new Intent(section.getContext(), TeacherClassActivity.class);
                    intent.putExtra("sectionRefKey", mSectionRefKey);
                    Log.d("TEST-MAGIC", "" + mMagicWord);
                    intent.putExtra("magic_word", "" + mMagicWord);
                    intent.putExtra("section_name", section.getText().toString());
                    section.getContext().startActivity(intent);

                }
            });
        }
    }

    /*
    constructs an adapter based on the section list passed in -- pass db
    through TeacherResumeActivity
     */
    public SectionAdapter(List<SectionSesh> sectionSeshList, HashMap<String, SectionSesh> h) {
        this.sectionSeshList = sectionSeshList;
        this.sectionKeys = h;
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
