package com.mao.engage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Util class to check for various permissions that are not explicitly granted within
 * the android manifest (such as accessing and writing to local disk storage)
 */

class PermissionsUtil {
    companion object {
        fun checkStoragePermission(context: Context): Boolean {
            val writeResult = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val readResut = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            return writeResult == PackageManager.PERMISSION_GRANTED && readResut == PackageManager.PERMISSION_GRANTED
        }
    }
}