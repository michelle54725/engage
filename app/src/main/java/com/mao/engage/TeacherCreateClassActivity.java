package com.mao.engage;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class TeacherCreateClassActivity extends AppCompatActivity {
    // Hardcoded instance variables (that should not be hardcoded)
    private String START = "2018-12-31-2000";
    private String END = "2018-12-31-2200";
    private String TA_KEY = "hardcoded device key";
    private String SECTION_ID = "CS70134A";
    private int MAGICKEY = 422;

    ImageButton backBtn;
    EditText classNameEditText;
    EditText dateEditText;
    EditText startTimeEditText;
    EditText endTimeEditText;
    Button createClassBtn;

    DatabaseReference mMagicKeyRef;
    HashMap<Integer, String> activeMagicKeys;
    private int magicKey;
    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_teacher_create_class);


        backBtn = findViewById(R.id.backBtn);
        classNameEditText = findViewById(R.id.classNameEditText);
        dateEditText = findViewById(R.id.dateEditText);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        endTimeEditText = findViewById(R.id.endTimeEditText);
        createClassBtn = findViewById(R.id.createClassBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dateEditText.setFocusable(false);
        startTimeEditText.setFocusable(false);
        endTimeEditText.setFocusable(false);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TeacherCreateClassActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateEditText.setText(String.format(Locale.US, "%02d/%02d/%02d", month, dayOfMonth, year));
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        startTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(TeacherCreateClassActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        Log.d("BOBOBOB", "onTimeSet: " + hourOfDay + minute);
                        if (hourOfDay == 0) {
                            hourOfDay = 12;
                            amPm = "AM";
                        }
                        else if (hourOfDay == 12) {
                            amPm = "PM";
                        }
                        else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        startTimeEditText.setText(String.format(Locale.US, "%02d:%02d%s", hourOfDay % 13, minute, amPm));
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        endTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY) + 1;
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(TeacherCreateClassActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String amPm;
                        if (hourOfDay == 0) {
                            hourOfDay = 12;
                            amPm = "AM";
                        }
                        else if (hourOfDay == 12) {
                            amPm = "PM";
                        }
                        else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        endTimeEditText.setText(String.format(Locale.US, "%02d:%02d%s", hourOfDay % 13, minute, amPm));
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        createClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check validity of fields
                if (fieldsValid()) {
                    setFields();

                    // create SectionSesh and push to Firebase
                    DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
                    final String mSectionRefKey = mSectionRef.push().getKey(); //create empty node to get key of it
                    final SectionSesh mSectionSesh = new SectionSesh(
                            START, END, TA_KEY, classNameEditText.getText().toString(), mSectionRefKey, magicKey, new ArrayList<String>());
                    final String mMagicWord = String.format(Locale.US, "%03d", magicKey);
                    FirebaseUtils.createSection(mSectionSesh);
                    FirebaseUtils.updateTeacher(getIntent().getStringExtra("name"), mSectionRefKey, mSectionSesh.getSection_id()); // update Teachers in Firebase
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
                            Log.d("TEST-MAGIC", mMagicWord);
                            intent.putExtra("magic_word", mMagicWord);
                            intent.putExtra("section_name", classNameEditText.getText().toString());
                            startActivity(intent);
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(TeacherCreateClassActivity.this, "Invalid Fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activeMagicKeys = new HashMap<>();
        mMagicKeyRef = FirebaseDatabase.getInstance().getReference("/MagicKeys");
        mMagicKeyRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("BOBCHILD", "onChildAdded: " + dataSnapshot.getKey() + dataSnapshot.getValue());
                activeMagicKeys.put(Integer.valueOf(dataSnapshot.getKey()), dataSnapshot.getValue(String.class));
                generateMagicWord();
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

    private void generateMagicWord() {
        magicKey = new Random().nextInt(1000);
        Log.d("LOOOOOP", "generateMagicWord: LOOOOOOOOP");
        if (activeMagicKeys.containsKey(magicKey)) { //TODO: @Jaiveer activeMagicKeys is null = crash
            final String conflictingSectionId = activeMagicKeys.get(magicKey);
            Log.d("BOBOBactive", "CONFLICT Time to resolve" + conflictingSectionId);
            final DatabaseReference conflictingSectionRef = FirebaseDatabase.getInstance().getReference("/Sections/").child(activeMagicKeys.get(magicKey));
            conflictingSectionRef.child("b_end").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy-HH:mma", Locale.US);
                    try {
                        Date endTime = dateFormat.parse((String) dataSnapshot.getValue());
                        Date now = Calendar.getInstance().getTime();
                        if (now.after(endTime)) {
                            //late enough to remove

                            Log.d("BOBOBB", "onDataChange: " + "CAN DELETE");
                            conflictingSectionRef.child("ta_key").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String ta_key = (String) dataSnapshot.getValue();
                                    DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("/Teachers/" + ta_key + "/existingSections");
                                    Log.d("BOBOBOBBOB", "onDataChange: BOBOBOBOBOBOBO TEACHER DELETE" + tempRef.getPath().toString());
                                    tempRef.child(conflictingSectionId).removeValue();
                                    conflictingSectionRef.child("user_ids").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                                String user_key = (String) userSnapshot.getValue();
                                                DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("/UserSessions/" + user_key);
                                                Log.d("BOBOBOBBOB", "onDataChange: BOBOBOBOBOBOBO USERS DELETE" + tempRef.getPath().toString());
                                                //TODO:  ask michelle abt purpose of the following code?  currently crashes!
//                                                if (FirebaseUtils.allUsers.get(user_key).equals(conflictingSectionId)) {
//                                                    tempRef.removeValue();
//                                                } else {
                                                Log.d("BOBOB", "onChildAdded: REEEE FIREBASE" + FirebaseUtils.allUsers.get(user_key));
                                               // }
                                            }
                                            conflictingSectionRef.removeValue();
                                            FirebaseDatabase.getInstance().getReference("/MagicKeys/" + magicKey).removeValue();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            //TODO: REMOVE FROM TEACHERS' REFERENCE TOO

                        } else {
                            generateMagicWord();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

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
}
