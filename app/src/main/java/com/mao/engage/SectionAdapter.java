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
    private List<String> sectionSeshList;
    //private HashMap<String, String> sectionKeys;

    /*
    section buttons referenced button design from section_list_row
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button section;

        public MyViewHolder(View view) {
            super(view);
            section = (Button) view.findViewById(R.id.sectionBtn);

//            section.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // open Section sesh with given magic word, section name, section ref key
//                    /**TODO: add a method: getExistingSectionsHashmap to FireBaseUtils.java that returns a hashmap of
//                     * a key section id and val section.
//                     */
//                    HashMap<String, SectionSesh> mySectionsHashMap = FirebaseUtils.getExistingSectionsHashMap(FirebaseUtils.getPsuedoUniqueID());
//
//                    SectionSesh mSection = mySectionsHashMap.get(section.getText().toString());
//                    String mSectionRefKey = mSection.ref_key;
//                    int mMagicWord = mSection.magic_key;
//
//                    Intent intent = new Intent(section.getContext(), TeacherClassActivity.class);
//                    intent.putExtra("sectionRefKey", mSectionRefKey);
//                    Log.d("TEST-MAGIC", "" + mMagicWord);
//                    intent.putExtra("magic_word", "" + mMagicWord);
//                    intent.putExtra("section_name", section.getText().toString());
//                    section.getContext().startActivity(intent);
//
//                }
//            });
        }
    }

    /*
    constructs an adapter based on the section list passed in -- pass db
    through TeacherResumeActivity
     */
    public SectionAdapter(List<String> lst) {
        this.sectionSeshList = lst;
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
        String section_id = sectionSeshList.get(position);
        //SectionSesh section = sectionSeshList.get(position);
        //holder.section.setText(section.getSection_id());
        holder.section.setText(section_id);
    }

    @Override
    public int getItemCount() {
        return sectionSeshList.size();
    }
}
