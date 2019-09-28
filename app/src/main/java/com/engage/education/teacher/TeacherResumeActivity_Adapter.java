/*
    The adapter used in TeacherResumeActivity.java
    to display a list of the user's existing sections.
 */

package com.engage.education.teacher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.engage.education.FirebaseUtils;
import com.engage.education.R;
import com.engage.education.UserConfig;
import com.engage.education.teacherclassactivity.TeacherClassActivity;

import java.util.HashMap;
import java.util.List;

public class TeacherResumeActivity_Adapter extends RecyclerView.Adapter<TeacherResumeActivity_Adapter.MyViewHolder> {
    private List<String> sectionSeshList;

    private static DatabaseReference mTeachersRef = FirebaseDatabase.getInstance().getReference("/Teachers");
    private static DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
    private static DatabaseReference mMagicKeysRef = FirebaseDatabase.getInstance().getReference("/MagicKeys");

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // section buttons reference button design from activity_teacher_resume_item.xml
        public Button section;
        public ImageButton delete;

        MyViewHolder(View view) {
            super(view);
            section = (Button) view.findViewById(R.id.sectionBtn);
            delete = (ImageButton) view.findViewById(R.id.deleteBtn);

            section.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // open Section sesh with given magic word, section name, section ref key
                    HashMap<String, String> mySectionsHashMap = FirebaseUtils.getExistingSectionsHashMap();
                    String mSectionRefKey = mySectionsHashMap.get(section.getText().toString());
                    UserConfig.Companion.setSectionReferenceKey(mSectionRefKey);
                    Intent intent = new Intent(section.getContext(), TeacherClassActivity.class);
                    intent.putExtra("sectionRefKey", mSectionRefKey);
                    intent.putExtra("section_name", section.getText().toString());
                    intent.putExtra("magic_word", FirebaseUtils.getMagicKey(mSectionRefKey) + "");
                    intent.putExtra("end_time", FirebaseUtils.getEndTime(mSectionRefKey));
                    intent.putExtra("start_time", FirebaseUtils.getStartTime(mSectionRefKey));
                    if (FirebaseUtils.compareTime(FirebaseUtils.getEndTime(mSectionRefKey))) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setMessage(section.getText().toString() + " is expired! It will now be removed.");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        HashMap<String, String> mySectionsHashMap = FirebaseUtils.getExistingSectionsHashMap();
                                        String mSectionRefKey = mySectionsHashMap.get(section.getText().toString());
                                        if (mSectionRefKey != null) {
                                            String mKey = Long.toString(FirebaseUtils.getMagicKey(mSectionRefKey));

                                            // remove section from /Teachers(existingSection), /Sections, /MagicKeys
                                            mTeachersRef.child(FirebaseUtils.getPsuedoUniqueID())
                                                    .child("existingSections").child(mSectionRefKey).removeValue();
                                            mSectionRef.child(mSectionRefKey).removeValue();
                                            mMagicKeysRef.child(mKey).removeValue();
                                        }
                                        // UI update
                                        removeAt(getAdapterPosition());
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                    } else {
                        section.getContext().startActivity(intent);
                    }
                }
            });

            // TODO: move to FirebaseUtils when quality checked by teammates OR keep here for @Paul's idea of separating code by user logic
            // Remove all instances of this section in the DB
            // -note: this does not remove users from the section (i.e. a User's section_ref_key may still be this one)
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> mySectionsHashMap = FirebaseUtils.getExistingSectionsHashMap();
                    String mSectionRefKey = mySectionsHashMap.get(section.getText().toString());
                    if (mSectionRefKey != null) {
                        String mKey = Long.toString(FirebaseUtils.getMagicKey(mSectionRefKey));

                        // remove section from /Teachers(existingSection), /Sections, /MagicKeys
                        mTeachersRef.child(FirebaseUtils.getPsuedoUniqueID())
                                .child("existingSections").child(mSectionRefKey).removeValue();
                        mSectionRef.child(mSectionRefKey).removeValue();
                        mMagicKeysRef.child(mKey).removeValue();
                    }
                    // UI update
                    removeAt(getAdapterPosition());
                }
            });
        }
        public void removeAt(int position) {
            sectionSeshList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, sectionSeshList.size());
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
                .inflate(R.layout.activity_teacher_resume_item, parent, false);
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
