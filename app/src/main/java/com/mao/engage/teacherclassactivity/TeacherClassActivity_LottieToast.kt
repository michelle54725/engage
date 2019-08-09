package com.mao.engage.teacherclassactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.mao.engage.FirebaseUtils
import com.mao.engage.teacher.TeacherOptionsActivity

internal fun lottieToast(teacherClassActivity: Activity, mSectionRefKey: String, name: String) {
    var alertDialog : LottieAlertDialog
    alertDialog = LottieAlertDialog.Builder (teacherClassActivity, DialogTypes.TYPE_QUESTION)
            .setTitle("Section has ended")
            .setDescription("Would you like to save your data?")
            .setPositiveText("Yes")
            .setNegativeText("No")
//            .setNoneText("None")
            .setPositiveButtonColor(Color.parseColor("#f44242"))
            .setPositiveTextColor(Color.parseColor("#ffeaea"))
            .setNegativeButtonColor(Color.parseColor("#ffbb00"))
            .setNegativeTextColor(Color.parseColor("#0a0906"))
//            .setNoneButtonColor(Color.parseColor("#1cd3ef"))
//            .setNoneTextColor(Color.parseColor("#c407c4"))
            // Error View
            .setPositiveListener(object: ClickListener {
                override fun onClick(dialog: LottieAlertDialog) {
                    dialog.dismiss()
                    FirebaseUtils.removeAllUsers(mSectionRefKey)
                    val intent = Intent(teacherClassActivity, TeacherOptionsActivity::class.java)
                    intent.putExtra("name", name)
                    teacherClassActivity.startActivity(intent)
                    FirebaseUtils.removeSection(mSectionRefKey, FirebaseUtils.getPsuedoUniqueID())
                }
            })
            // Warning View
            .setNegativeListener(object : ClickListener
            {
                override fun onClick(dialog: LottieAlertDialog) {
                    dialog.dismiss()
                    FirebaseUtils.removeAllUsers(mSectionRefKey)
                    val intent = Intent(teacherClassActivity, TeacherOptionsActivity::class.java)
                    intent.putExtra("name", name)
                    teacherClassActivity.startActivity(intent)
                    FirebaseUtils.removeSection(mSectionRefKey, FirebaseUtils.getPsuedoUniqueID())
                }
            })
            .build()

    alertDialog.show()
}
