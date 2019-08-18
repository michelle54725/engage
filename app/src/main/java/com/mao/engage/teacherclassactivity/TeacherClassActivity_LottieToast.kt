package com.mao.engage.teacherclassactivity

import android.app.Activity
import android.content.Intent
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.mao.engage.FirebaseUtils
import com.mao.engage.R

class LottieToast {
    companion object {
        fun showEndOfSectionToast(
                currActivity: Activity,
                sectionRefKey: String,
                name: String,
                targetActivity: Activity,
                isTeacher: Boolean
        ) {
            var alertDialog = LottieAlertDialog
                    .Builder(currActivity, DialogTypes.TYPE_QUESTION)
                    .setTitle("Section has ended")
                    .setDescription("Would you like to save your data?")
                    .setPositiveText("Yes")
                    .setNegativeText("No")
                    .setPositiveButtonColor(R.color.positiveButtonColor)
                    .setPositiveTextColor(R.color.positiveTextColor)
                    .setNegativeButtonColor(R.color.negativeButtonColor)
                    .setNegativeTextColor(R.color.negativeTextColor)
                    .setPositiveListener(object : ClickListener {
                        override fun onClick(dialog: LottieAlertDialog) {
                            dialog.dismiss()
                            FirebaseUtils.removeAllUsers(sectionRefKey)
                            val intent = Intent(currActivity, targetActivity::class.java)
                            intent.putExtra("name", name)
                            currActivity.startActivity(intent)
                            if (isTeacher) {
                                FirebaseUtils.removeSection(sectionRefKey, FirebaseUtils.getPsuedoUniqueID())
                            }
                        }
                    }).build()
            alertDialog.show()
        }
    }
}
