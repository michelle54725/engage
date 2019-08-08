/*
    TeacherCreateClassActivity: where a Teacher sets up a class by entering metadata.
        (metadata: Class Name, Data, Start time, End time)

    Triggered by: "Create New Section" from TeacherOptionsActivity OR
                   "Join As Teacher" from StartActivity if user has no existing sections

    Transitions to: TeacherClassActivity
 */

package com.mao.engage.teacher;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.mao.engage.FirebaseUtils;
import com.mao.engage.R;
import com.mao.engage.models.SectionSesh;
import com.mao.engage.teacherclassactivity.TeacherClassActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class TeacherCreateClassActivity extends AppCompatActivity implements View.OnClickListener {

    private String START;
    private String END;
    private String TA_KEY;
    private String SECTION_ID;

    ImageButton backBtn;
    EditText classNameEditText;
    EditText dateEditText;
    EditText startTimeEditText;
    EditText endTimeEditText;
    Button createClassBtn;
    String name;

    HashMap<Integer, String> activeMagicKeys; // populated with getActiveMagicKeys()
    private int magicKey; // set in generateMagicKey()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActiveMagicKeys();

        // UI: set to portrait, notification bar hidden
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_teacher_create_class);

        // bind views (UX components)
        backBtn = findViewById(R.id.backBtn);
        classNameEditText = findViewById(R.id.classNameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        endTimeEditText = findViewById(R.id.endTimeEditText);
        createClassBtn = findViewById(R.id.createClassBtn);
        name = getIntent().getStringExtra("name");
        Log.d("TEST", "createname: " + name);

        // disable focus on calendar fields
        dateEditText.setFocusable(false); // Keyboard input is entered into the field with focus
        startTimeEditText.setFocusable(false);
        endTimeEditText.setFocusable(false);

        // set button listeners
        dateEditText.setOnClickListener(this);
        startTimeEditText.setOnClickListener(this);
        endTimeEditText.setOnClickListener(this);
        createClassBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Creating a SectionSesh and pushing to DB
            case R.id.createClassBtn:
                if (fieldsValid()) {
                    setFields();

                    // create empty node to get key of it -> section's ref_key
                    final String mSectionRefKey = FirebaseDatabase.getInstance().getReference("/Sections")
                            .push().getKey();

                    // create SectionSesh
                    final SectionSesh mSectionSesh = new SectionSesh(
                            START, END, TA_KEY, SECTION_ID, mSectionRefKey, magicKey, new ArrayList<String>());
                    final String mMagicWord = String.format(Locale.US, "%03d", magicKey);

                    // add section to /Sections in DB
                    FirebaseUtils.createSection(mSectionSesh);
                    // add section to /Teachers/{user_id}/existingSections in DB
                    FirebaseUtils.updateTeacher(getIntent().getStringExtra("name"), mSectionRefKey, mSectionSesh.getSection_name());

                    // UX: "Success!" pop-up
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeacherCreateClassActivity.this);
                    builder.setTitle("Success!");
                    builder.setMessage("Magic word: " + mMagicWord + "\nShare this with the class");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("Start Class", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(TeacherCreateClassActivity.this, TeacherClassActivity.class);
                            intent.putExtra("sectionRefKey", mSectionRefKey);
                            intent.putExtra("magic_word", mMagicWord);
                            intent.putExtra("section_name", classNameEditText.getText().toString());
                            intent.putExtra("start_time", startTimeEditText.getText().toString());
                            intent.putExtra("end_time", endTimeEditText.getText().toString());
                            intent.putExtra("name", name);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(TeacherCreateClassActivity.this, "Invalid Fields", Toast.LENGTH_SHORT).show();
                }
                break;

            // Setting date and time metadata
            case R.id.dateEditText:
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TeacherCreateClassActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateEditText.setText(String.format(Locale.US, "%02d/%02d/%02d", month + 1, dayOfMonth, year));
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
                break;
            case R.id.startTimeEditText:
                final Calendar c2 = Calendar.getInstance();
                int hour = c2.get(Calendar.HOUR_OF_DAY);
                int minute = c2.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(TeacherCreateClassActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        if (hourOfDay == 0) { hourOfDay = 12; amPm = "AM"; }
                        else if (hourOfDay == 12) { amPm = "PM"; }
                        else if (hourOfDay > 12) { hourOfDay -= 12;amPm = "PM";
                        } else { amPm = "AM"; }
                        startTimeEditText.setText(String.format(Locale.US, "%02d:%02d%s", hourOfDay % 13, minute, amPm));
                    }
                }, hour, minute, false);
                timePickerDialog.show();
                break;
            case R.id.endTimeEditText:
                final Calendar c3 = Calendar.getInstance();
                int hour2 = c3.get(Calendar.HOUR_OF_DAY) + 1;
                int minute2 = c3.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog2 = new TimePickerDialog(TeacherCreateClassActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        if (hourOfDay == 0) { hourOfDay = 12; amPm = "AM"; }
                        else if (hourOfDay == 12) { amPm = "PM"; }
                        else if (hourOfDay > 12) { hourOfDay -= 12; amPm = "PM"; }
                        else { amPm = "AM"; }
                        endTimeEditText.setText(String.format(Locale.US, "%02d:%02d%s", hourOfDay % 13, minute, amPm));
                    }
                }, hour2, minute2, false);
                timePickerDialog2.show();
                break;

            case R.id.backBtn:
                finish();
                break;
            default:
                Log.d("TEST:","Button not accounted for");
                break;
        }
    }

    // set magicKey to a random int between 0 and 1000. If it already exists in the DB, try again.
    private void generateMagicKey() {
        magicKey = new Random().nextInt(1000);
        if (activeMagicKeys.containsKey(magicKey)) {
            generateMagicKey();
//Hide section of comments below for cleaner view
// -------Jaiveer's attempt to delete a session that is over to recycle it's magicKey:----------
//            final String conflictingSectionId = activeMagicKeys.get(magicKey);
//            Log.d("BOBOBactive", "CONFLICT Time to resolve" + conflictingSectionId);
//            final DatabaseReference conflictingSectionRef = FirebaseDatabase.getInstance().getReference("/Sections/").child(activeMagicKeys.get(magicKey));
//            conflictingSectionRef.child("b_end").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy-HH:mma", Locale.US);
//                    try {
//                        Date endTime = dateFormat.parse((String) dataSnapshot.getValue());
//                        Date now = Calendar.getInstance().getTime();
//                        if (now.after(endTime)) {
//                            //late enough to remove
//
//                            Log.d("BOBOBB", "onDataChange: " + "CAN DELETE");
//                            conflictingSectionRef.child("ta_key").addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    String ta_key = (String) dataSnapshot.getValue();
//                                    DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("/Teachers/" + ta_key + "/existingSections");
//                                    Log.d("BOBOBOBBOB", "onDataChange: BOBOBOBOBOBOBO TEACHER DELETE" + tempRef.getPath().toString());
//                                    tempRef.child(conflictingSectionId).removeValue();
//                                    conflictingSectionRef.child("user_ids").addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                                                String user_key = (String) userSnapshot.getValue();
//                                                DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("/UserSessions/" + user_key);
//                                                Log.d("BOBOBOBBOB", "onDataChange: BOBOBOBOBOBOBO USERS DELETE" + tempRef.getPath().toString());
//                                                //TODO:  ask michelle abt purpose of the following code?  currently crashes!
//                                                //TODO: This was an attempt at removing a session from FB once it's END_TIME passed. Commenting out for now -michelle
//                                                 if (FirebaseUtils.allUsers.get(user_key).equals(conflictingSectionId)) {
//                                                    tempRef.removeValue();
//                                                } else {
//                                                Log.d("BOBOB", "onChildAdded: REEEE FIREBASE" + FirebaseUtils.allUsers.get(user_key));
//                                                }
//                                            }
//                                            conflictingSectionRef.removeValue();
//                                            FirebaseDatabase.getInstance().getReference("/MagicKeys/" + magicKey).removeValue();
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                            //TODO: REMOVE FROM TEACHERS' REFERENCE TOO
//
//                        } else {
//                            generateMagicKey();
//                        }
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            });
        }
    }

    /** Helper methods **/
    private void setFields() {
        START = getField(dateEditText).replace("/", "-") +"-"+ getField(startTimeEditText);
        END = getField(dateEditText).replace("/", "-") +"-"+ getField(endTimeEditText);
        TA_KEY = FirebaseUtils.getPsuedoUniqueID();
        SECTION_ID = getField(classNameEditText);
    }

    private boolean fieldsValid() {
        return !getField(classNameEditText).isEmpty()
                & !getField(dateEditText).isEmpty()
                & !getField(startTimeEditText).isEmpty()
                & !getField(endTimeEditText).isEmpty();
    }

    private String getField(EditText field) {
        return field.getText().toString();
    }

    // populate activeMagicKeys with data in /MagicKeys in DB
    private void getActiveMagicKeys() {
        activeMagicKeys = new HashMap<>();
        FirebaseDatabase.getInstance().getReference("/MagicKeys").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("BOBCHILD", "onChildAdded: " + dataSnapshot.getKey() + dataSnapshot.getValue());
                activeMagicKeys.put(Integer.valueOf(dataSnapshot.getKey()), dataSnapshot.getValue(String.class));
                generateMagicKey();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("BOBCHILD", "onChildCHANGED: " + dataSnapshot.getKey() + dataSnapshot.getValue());
                activeMagicKeys.put(Integer.valueOf(dataSnapshot.getKey()), dataSnapshot.getValue(String.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("BOBCHILD", "onChildREMOVED: " + dataSnapshot.getKey() + dataSnapshot.getValue());
                activeMagicKeys.remove(Integer.valueOf(dataSnapshot.getKey()));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
