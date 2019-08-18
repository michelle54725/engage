package com.mao.engage.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.github.mikephil.charting.charts.LineChart
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.mao.engage.FirebaseUtils
import com.mao.engage.PermissionsUtil
import com.mao.engage.R
import java.util.*

class LottieToast {
    companion object {
        fun showEndOfSectionToast(
                currActivity: Activity,
                sectionRefKey: String,
                name: String,
                targetActivity: Activity,
                isTeacher: Boolean,
                chart: LineChart
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
                            val currentTime = Calendar.getInstance().time.toString()
                            val deviceID = FirebaseUtils.getPsuedoUniqueID()
                            val exportName = String.format("%s_%s", currentTime, deviceID)
                            while (true) {
                                if (PermissionsUtil.checkStoragePermission(currActivity)) {
                                    val success = chart.saveToGallery(exportName)
                                    if (success) {
                                        Toast.makeText(currActivity, "Image has been saved successfully!", Toast.LENGTH_LONG).show()
                                        break
                                    } else {
                                        Toast.makeText(currActivity, "Export failed :( please try again later", Toast.LENGTH_LONG).show()
                                        return
                                    }
                                } else {
                                    ActivityCompat.requestPermissions(
                                            currActivity,
                                            arrayOf(
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                            ),
                                            1
                                    )
                                }
                            }
                            intent.putExtra("name", name)
                            currActivity.startActivity(intent)
                            if (isTeacher) {
                                FirebaseUtils.removeSection(sectionRefKey, FirebaseUtils.getPsuedoUniqueID())
                            }
                        }
                    })
                    .setNegativeListener(object : ClickListener{
                        override fun onClick(dialog: LottieAlertDialog) {
                            dialog.dismiss()
                        }
                    })
                    .build()
            alertDialog.show()
        }
    }
}
