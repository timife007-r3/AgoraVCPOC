package com.timife.agoravcpoc

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity


private val permissionReqId = 22

// Obtain recording, camera and other permissions required to implement real-time audio and video interaction
private fun getRequiredPermissions(): Array<String> {
    // Determine the permissions required when targetSDKVersion is 31 or above
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.RECORD_AUDIO, // Recording permission
            Manifest.permission.CAMERA, // Camera permission
            Manifest.permission.READ_PHONE_STATE, // Permission to read phone status
            Manifest.permission.BLUETOOTH_CONNECT // Bluetooth connection permission
        )
    } else {
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
    }
}

 fun checkPermissions(activity: Activity): Boolean {
    for (permission in getRequiredPermissions()) {
        val permissionCheck = ContextCompat.checkSelfPermission(activity, permission)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}