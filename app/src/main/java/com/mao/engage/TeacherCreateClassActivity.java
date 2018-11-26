package com.mao.engage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TeacherCreateClassActivity extends AppCompatActivity {
    // Hardcoded instance variables (that should not be hardcoded)
    final static String USER_ID = "user_id_1";
    final static String USERNAME = "Michelle Mao";
    final static String START = "2018-12-31-2000";
    final static String END = "2018-12-31-2200";
    final static String TA_NAME = "John Denero";
    final static String SECTION_ID = "CS70134A";
    final static int MAGICKEY = 421;

    ImageButton backBtn;
    EditText dateEditText;
    EditText startTimeEditText;
    EditText endTimeEditText;
    Button createClassBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_teacher_create_class);

        backBtn = findViewById(R.id.backBtn);
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
                // create SectionSesh and push to Firebase
                DatabaseReference mSectionRef = FirebaseDatabase.getInstance().getReference("/Sections");
                final String mSectionRefKey = mSectionRef.push().getKey(); //create empty node to get key of it
                final SectionSesh mSectionSesh = new SectionSesh(
                        START, END, TA_NAME, SECTION_ID, mSectionRefKey, MAGICKEY, new ArrayList<String>());
                FirebaseUtils.createSection(mSectionSesh);

                AlertDialog.Builder builder = new AlertDialog.Builder(TeacherCreateClassActivity.this);
                builder.setTitle("Success!");
                builder.setMessage("Magic word: 420\nShare this with the class");
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
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });

    }
}
