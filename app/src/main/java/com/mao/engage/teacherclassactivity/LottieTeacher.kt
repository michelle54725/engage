/*
    StudentLoginActivity: where students input the magic word to key into a section
    - If magic word matches existing section, create a new UserSession in the DB

    Triggered by: "JOIN AS STUDENT" button from StartActivity

    Transitions to: StudentClassActivity (via findSection)
 */

package com.mao.engage.teacherclassactivity
/*
    Activity that manages and contains the fragments (timeline and attendance) associated with the teacher view
    Triggered by: intent call from section adapter.
 */

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog

import com.mao.engage.FirebaseUtils
import com.mao.engage.R
import com.mao.engage.teacher.TeacherCreateClassActivity
import com.mao.engage.teacher.TeacherOptionsActivity
import com.mao.engage.teacherclassactivity.AttendanceFragment
import com.mao.engage.teacherclassactivity.TimelineFragment

import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask

import info.hoang8f.android.segmented.SegmentedGroup

import com.mao.engage.teacherclassactivity.TimelineFragment.getBitmapFromView
import com.mao.engage.teacherclassactivity.TimelineFragment.takeScreenshot

class LottieTeacher : AppCompatActivity(), TimelineFragment.OnFragmentInteractionListener {

    // will store information passed on from SectionAdapter to this activity
    lateinit var mSectionRefKey: String
    // items related to UI design
    lateinit var segmentedBar: SegmentedGroup
    lateinit var nowTabBtn: RadioButton
    lateinit var timelineTabBtn: RadioButton
    lateinit var endSectionBtn: Button
    lateinit var fragmentManager: FragmentManager
    lateinit var attendanceFragment: AttendanceFragment
    lateinit var timelineFragment: TimelineFragment
    lateinit var endTime: String
    lateinit var name: String
    lateinit var toasty: Handler

    var toastTask: Runnable = Runnable {
//        var alertDialog : LottieAlertDialog
        var alertDialog= LottieAlertDialog.Builder (this@LottieTeacher, DialogTypes.TYPE_QUESTION)
                .setTitle("What Type")
                .setDescription("Would you like to see ?")
                .setPositiveText("Error")
                .setNegativeText("Warning")
                .setNoneText("None")
                .setPositiveButtonColor(Color.parseColor("#f44242"))
                .setPositiveTextColor(Color.parseColor("#ffeaea"))
                .setNegativeButtonColor(Color.parseColor("#ffbb00"))
                .setNegativeTextColor(Color.parseColor("#0a0906"))
                .setNoneButtonColor(Color.parseColor("#1cd3ef"))
                .setNoneTextColor(Color.parseColor("#c407c4"))

                .setPositiveListener(object: ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        // This is the usage same instance of view
                        alertDialog.changeDialog(LottieAlertDialog.Builder(this@LottieTeacher,DialogTypes.TYPE_ERROR)
                                .setTitle("Error")
                                .setDescription("Some error has happened.")
                                .setPositiveText("Okay")
                                .setPositiveListener(object : ClickListener{
                                    override fun onClick(dialog: LottieAlertDialog) {
                                        dialog.dismiss()
                                    }
                                })
                        )
                    }
                })
                // Warning View
                .setNegativeListener(object : ClickListener
                {
                    override fun onClick(dialog: LottieAlertDialog) {
                        // This is the usage same instance of view
                        alertDialog.changeDialog(LottieAlertDialog.Builder(this@LottieTeacher,DialogTypes.TYPE_WARNING)
                                .setTitle("Warning")
                                .setDescription("Some warning.")
                                .setPositiveText("Okay")
                                .setPositiveListener(object : ClickListener{
                                    override fun onClick(dialog: LottieAlertDialog) {
                                        dialog.dismiss()
                                    }
                                }))
                    }
                })
                // Dismiss View
                .setNoneListener(object: ClickListener
                {
                    override fun onClick(dialog: LottieAlertDialog) {
                        dialog.dismiss()
                    }
                })
        )
        .build()

        alertDialog.show()

//        val builder = AlertDialog.Builder(this@LottieTeacher)
//        builder.setTitle("Section has ended!")
//        builder.setMessage("Would you like to save your graph?")
//        builder.setNegativeButton("No") { dialog, which ->
//            dialog.cancel()
//            FirebaseUtils.removeAllUsers(mSectionRefKey)
//            val intent = Intent(this@LottieTeacher, TeacherOptionsActivity::class.java)
//            Log.d("TEST", "name: $name")
//            intent.putExtra("name", name)
//            startActivity(intent)
//            FirebaseUtils.removeSection(mSectionRefKey, FirebaseUtils.getPsuedoUniqueID())
//        }
//        builder.setPositiveButton("Yes") { dialog, which ->
//            dialog.dismiss()
//            FirebaseUtils.removeAllUsers(mSectionRefKey)
//            val fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.constraintLayout, timelineFragment)
//            fragmentTransaction.commit()
//            //takeScreenshot();
//            val toSave = getBitmapFromView(this@LottieTeacher.window.decorView.rootView)
//
//            val root = Environment.getExternalStorageDirectory().toString()
//            val myDir = File("$root/req_images")
//            myDir.mkdirs()
//            val fname = "Image-$mSectionRefKey.jpg"
//            val file = File(myDir, fname)
//            Log.i("TEST", "" + file)
//            if (file.exists())
//                file.delete()
//            try {
//                Log.d("TEST", "before outputstream")
//                val out = FileOutputStream(file)
//                Log.d("TEST", "after outputstream")
//                toSave.compress(Bitmap.CompressFormat.JPEG, 90, out)
//                out.flush()
//                out.close()
//                Log.d("TEST", "saved")
//            } catch (e: Exception) {
//                Log.d("TEST", "outputstream error")
//                e.printStackTrace()
//            }
//
//            val intent = Intent(this@LottieTeacher, TeacherOptionsActivity::class.java)
//            Log.d("TEST", "name: $name")
//            intent.putExtra("name", name)
//            startActivity(intent)
//            FirebaseUtils.removeSection(mSectionRefKey, FirebaseUtils.getPsuedoUniqueID())
//        }
//        builder.show()
    }

    //NowFragment nowFragment; //now fragment is not used anymore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_teacher_class)

        segmentedBar = findViewById(R.id.segmentedBar)
        nowTabBtn = findViewById(R.id.nowTabBtn)
        timelineTabBtn = findViewById(R.id.timelineTabBtn)
        endSectionBtn = findViewById(R.id.endSectionBtn)
        name = intent.getStringExtra("name")
        Log.d("TEST", "firstname: $name")

        segmentedBar.setTintColor(resources.getColor(R.color.colorPrimary))
        nowTabBtn.setTextColor(Color.WHITE)
        timelineTabBtn.setTextColor(Color.WHITE)

        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        //nowFragment = new NowFragment(); // not used anymore

        /*
            sends information (magic word, section name, section ref key, and timeline data)
            to attendance and timeline fragment in a bundle
        */
        attendanceFragment = AttendanceFragment()
        timelineFragment = TimelineFragment()
        val bundle = Bundle()
        bundle.putString("magic_word", intent.getStringExtra("magic_word"))
        val timelineData = ArrayList<Int>()
        bundle.putString("section_name", intent.getStringExtra("section_name"))
        mSectionRefKey = intent.getStringExtra("sectionRefKey")
        bundle.putString("sectionRefKey", intent.getStringExtra("sectionRefKey"))
        bundle.putIntegerArrayList("timelinedata", timelineData)
        bundle.putString("start_time", intent.getStringExtra("start_time"))
        bundle.putString("end_time", intent.getStringExtra("end_time"))
        attendanceFragment.arguments = bundle
        timelineFragment.arguments = bundle

        fragmentTransaction.replace(R.id.constraintLayout, attendanceFragment)
        fragmentTransaction.commit()

        FirebaseUtils.setUserIdinSectionListener(mSectionRefKey)
        endTime = FirebaseUtils.getEndTime(mSectionRefKey)

        //Handler to call toast after section is over!
        val calendar = Calendar.getInstance()
        val currentTimestamp = calendar.timeInMillis
        val desiredHour = Integer.parseInt(endTime.substring(0, 2))
        val desiredMinute = Integer.parseInt(endTime.substring(3, 5))
        if (endTime.substring(5, 7).toLowerCase() == "pm") {
            calendar.set(Calendar.HOUR_OF_DAY, desiredHour + 12)
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, desiredHour)
        }
        calendar.set(Calendar.MINUTE, desiredMinute)
        calendar.set(Calendar.SECOND, 0)
        val diffTimestamp = calendar.timeInMillis - currentTimestamp
        toasty = Handler()
        toasty.postDelayed(toastTask, diffTimestamp)


        //sets triggers for the two buttons on our screen that link to each individual fragment
        nowTabBtn.setOnClickListener { handleFragmentTransaction(attendanceFragment) }
        timelineTabBtn.setOnClickListener { handleFragmentTransaction(timelineFragment) }

        endSectionBtn.setOnClickListener {
            //switch to timelineFragment if not being displayed TODO: this crashes the app a couple seconds after return to OptionsActivity -Mao
            //                if (!timelineFragment.isVisible()) {
            //                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //                    fragmentTransaction.replace(R.id.constraintLayout, timelineFragment);
            //                    fragmentTransaction.commitNowAllowingStateLoss(); //synchronous
            //                }
            toasty.post(toastTask)
        }
    }

    private fun handleFragmentTransaction(fragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.constraintLayout, fragment, fragment.toString())
        fragmentTransaction.commit()
    }

    override fun onFragmentInteraction(uri: Uri) {
        Log.d("BOBOB", "onFragmentInteraction: $uri")
    }
}
